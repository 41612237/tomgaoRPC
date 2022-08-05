package com.tomgao.test;

import com.tomgao.rpc.api.HelloObject;
import com.tomgao.rpc.api.HelloService;
import com.tomgao.rpc.serializer.CommonSerializer;
import com.tomgao.rpc.transport.RpcClient;
import com.tomgao.rpc.transport.RpcClientProxy;
import com.tomgao.rpc.transport.netty.client.NettyClient;

public class NettyTestClient {

    public static void main(String[] args) {
        RpcClient client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService proxy = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "this is a message from tomgao");
        String res = proxy.hello(object);
        System.out.println(res);
    }
}
