package com.tomgao.test;

import com.tomgao.rpc.api.HelloService;
import com.tomgao.rpc.registry.DefaultServiceRegistry;
import com.tomgao.rpc.registry.ServiceRegistry;
import com.tomgao.rpc.socket.server.SocketServer;

public class TestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.registry(helloService);
        SocketServer socketServer = new SocketServer(serviceRegistry);
        socketServer.start(9000);
    }
}
