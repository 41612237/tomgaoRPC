package com.tomgao.rpc.transport.netty.server;

import com.tomgao.rpc.entity.RpcRequest;
import com.tomgao.rpc.entity.RpcResponse;
import com.tomgao.rpc.factory.SingletonFactory;
import com.tomgao.rpc.handler.RequestHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private final RequestHandler requestHandler;

    public NettyServerHandler() {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {

        try {
            if (rpcRequest.getHeartBeat()) {
                logger.info("接受到用户心跳包...");
                return;
            }
            logger.info("接收到客户端请求...");
            Object result = requestHandler.handle(rpcRequest);
            if (channelHandlerContext.channel().isActive() && channelHandlerContext.channel().isWritable()){
                channelHandlerContext.writeAndFlush(RpcResponse.success(result, rpcRequest.getRequestId()));
            } else {
                logger.error("channel不可写");
            }
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

    // todo 这是干嘛的?
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                logger.info("长时间未收到心跳包, 断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }
}
