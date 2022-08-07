package com.tomgao.rpc.transport;

import com.tomgao.rpc.entity.RpcRequest;
import com.tomgao.rpc.entity.RpcResponse;
import com.tomgao.rpc.transport.netty.client.NettyClient;
import com.tomgao.rpc.transport.socket.client.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * RPC客户端动态代理
 */
public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    /**
     *
     * 返回一个代理对象
     * 调用某个具体方法时, invoke(Object proxy, Method method, Object[] args)
     * @param Class<T> clazz 传入要被代理的接口
     */
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    @SuppressWarnings("unchecked")
    // Object proxy, Method method, Object[] args是要调用的method的参数
    // String res = helloService.hello(object);
    // 调用这个方法的时候传入的参数,都会被封装到args里面 然后调用代理类的invoke方法 进行方法的增强
    public Object invoke(Object proxy, Method method, Object[] args) {
        logger.info("调用类和方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes(), false);
        Object result = null;
        if (client instanceof NettyClient) {
            CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest);
            try {
                result = completableFuture.get().getData();
            } catch (ExecutionException | InterruptedException e) {
                logger.error("方法调用请求发送失败", e);
                return null;
            }
        }
        if (client instanceof SocketClient) {
            RpcResponse rpcResponse = (RpcResponse) client.sendRequest(rpcRequest);
            result = rpcResponse.getData();
        }
        return result;
    }
}
