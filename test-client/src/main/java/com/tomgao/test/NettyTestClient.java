package com.tomgao.test;

import com.tomgao.rpc.RpcClient;
import com.tomgao.rpc.RpcClientProxy;
import com.tomgao.rpc.api.HelloObject;
import com.tomgao.rpc.api.HelloService;
import com.tomgao.rpc.netty.client.NettyClient;
import com.tomgao.rpc.serializer.HessianSerializer;
import lombok.Cleanup;

public class NettyTestClient {

    public static void main(String[] args) {
        RpcClient client = new NettyClient("127.0.0.1", 9999);
        client.setSerializer(new HessianSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService proxy = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "this is a message from tomgao");
        String res = proxy.hello(object);
        System.out.println(res);
    }
}
