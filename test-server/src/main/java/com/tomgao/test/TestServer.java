package com.tomgao.test;

import com.tomgao.rpc.api.HelloService;
import com.tomgao.rpc.server.RpcServer;

public class TestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.registry(helloService, 9000);
    }
}
