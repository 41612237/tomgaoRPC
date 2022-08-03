package com.tomgao.rpc.netty.server;

import com.tomgao.rpc.RequestHandler;
import com.tomgao.rpc.entity.RpcRequest;
import com.tomgao.rpc.entity.RpcResponse;
import com.tomgao.rpc.registry.DefaultServiceRegistry;
import com.tomgao.rpc.registry.ServiceRegistry;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler =  new RequestHandler();
    private static ServiceRegistry serviceRegistry = new DefaultServiceRegistry();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {

        try {
            logger.info("服务器收到请求: {}", rpcRequest);
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(rpcRequest, service);
            ChannelFuture future = channelHandlerContext.writeAndFlush(RpcResponse.success(result, rpcRequest.getRequestId()));
            future.addListener(ChannelFutureListener.CLOSE); // ?
        } finally {
            ReferenceCountUtil.release(rpcRequest);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生: ");
        cause.printStackTrace();
        ctx.close();
    }
}
