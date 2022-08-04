package com.tomgao.test;

import com.tomgao.rpc.api.HelloService;
import com.tomgao.rpc.serializer.HessianSerializer;
import com.tomgao.rpc.transport.socket.server.SocketServer;

public class SocketTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl2();
        SocketServer socketServer = new SocketServer("127.0.0.1", 9998);
        socketServer.setSerializer(new HessianSerializer());
        socketServer.publishService(helloService, HelloService.class);
    }
}
