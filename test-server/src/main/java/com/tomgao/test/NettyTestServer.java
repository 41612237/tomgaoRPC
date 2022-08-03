package com.tomgao.test;

import com.tomgao.rpc.api.HelloService;
import com.tomgao.rpc.netty.server.NettyServer;
import com.tomgao.rpc.registry.DefaultServiceRegistry;
import com.tomgao.rpc.registry.ServiceRegistry;
import com.tomgao.rpc.serializer.KryoSerializer;

import javax.annotation.Resource;

public class NettyTestServer {


    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.registry(helloService);
        NettyServer server = new NettyServer();
        server.setSerializer(new KryoSerializer());
        server.start(9999);
    }
}
