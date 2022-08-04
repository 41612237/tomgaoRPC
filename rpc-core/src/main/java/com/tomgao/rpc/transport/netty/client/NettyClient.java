package com.tomgao.rpc.transport.netty.client;

import com.tomgao.rpc.entity.RpcRequest;
import com.tomgao.rpc.entity.RpcResponse;
import com.tomgao.rpc.enumeration.RpcError;
import com.tomgao.rpc.exception.RpcException;
import com.tomgao.rpc.registry.NacosServiceDiscovery;
import com.tomgao.rpc.registry.ServiceDiscovery;
import com.tomgao.rpc.serializer.CommonSerializer;
import com.tomgao.rpc.transport.RpcClient;
import com.tomgao.rpc.util.RpcMessageChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;

    private final ServiceDiscovery serviceDiscovery;

    private CommonSerializer serializer;


    public NettyClient() {
        this.serviceDiscovery = new NacosServiceDiscovery();
    }

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {

        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        AtomicReference<Object> result = new AtomicReference<>(null);

        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            channel.writeAndFlush(rpcRequest).addListener(future -> {
                if (future.isSuccess()) {
                    logger.info("客户端发送消息:{}", rpcRequest.toString());
                } else {
                    logger.error("发送消息时有错误发生: ", future.cause());
                }
            });
            channel.closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
            RpcResponse rpcResponse = channel.attr(key).get();
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            result.set(rpcResponse.getData());
        } catch (InterruptedException e) {
            logger.error("发送消息时有错误发生", e);
            Thread.currentThread().interrupt();
        }
        return result.get();
    }

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
