package com.tomgao.rpc;

import com.tomgao.rpc.socket.client.SocketClient;
import com.tomgao.rpc.entity.RpcRequest;
import lombok.Cleanup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
     * @param Class<T> clazz 传入要被代理的接口
     */
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    // todo Object proxy, Method method, Object[] args 为什么args就是RpcRequest的参数
    // String res = helloService.hello(object);
    // 调用这个方法的时候传入的参数,都会被封装到args里面 然后调用代理类的invoke方法 进行方法的增强
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("调用类和方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes());
        return client.sendRequest(rpcRequest);
    }
}
