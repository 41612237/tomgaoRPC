package com.tomgao.test;

import com.tomgao.rpc.RpcClient;
import com.tomgao.rpc.RpcClientProxy;
import com.tomgao.rpc.api.HelloObject;
import com.tomgao.rpc.api.HelloService;
import com.tomgao.rpc.netty.client.NettyClient;

public class NettyTestClient {

    public static void main(String[] args) {
        RpcClient client = new NettyClient("127.0.0.1", 9999);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "this is a message from tomgao");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
