package com.tomgao.rpc.netty.client;

import com.tomgao.rpc.RpcClient;
import com.tomgao.rpc.codec.CommonDecoder;
import com.tomgao.rpc.codec.CommonEncoder;
import com.tomgao.rpc.entity.RpcRequest;
import com.tomgao.rpc.entity.RpcResponse;
import com.tomgao.rpc.enumeration.RpcError;
import com.tomgao.rpc.exception.RpcException;
import com.tomgao.rpc.netty.server.NettyServerHandler;
import com.tomgao.rpc.serializer.CommonSerializer;
import com.tomgao.rpc.serializer.HessianSerializer;
import com.tomgao.rpc.serializer.JsonSerializer;
import com.tomgao.rpc.serializer.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final Bootstrap bootstrap;

    private CommonSerializer serializer;

    private String host;
    private int port;


    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    static {
        EventLoopGroup group = new NioEventLoopGroup();
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
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new CommonDecoder())
                        .addLast(new CommonEncoder(serializer))
                        .addLast(new NettyClientHandler());
            }
        });

        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("客户端连接到服务器 {}: {}", host, port);
            Channel channel = future.channel();
            if (channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()) {
                        logger.info("客户端发送消息: {}", rpcRequest.toString());
                    } else {
                        logger.error("发送消息时有错误发生", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();

                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            logger.error("发送消息时有错误发生", e);
        }
        return null;
    }

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
