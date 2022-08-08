package com.tomgao.test;

import com.tomgao.rpc.annotation.ServiceScan;
import com.tomgao.rpc.api.HelloService;
import com.tomgao.rpc.serializer.CommonSerializer;
import com.tomgao.rpc.transport.RpcServer;
import com.tomgao.rpc.transport.socket.server.SocketServer;

@ServiceScan
public class SocketTestServer {

    public static void main(String[] args) {
//        HelloService helloService = new HelloServiceImpl2();
//        SocketServer socketServer = new SocketServer("127.0.0.1", 9998, CommonSerializer.HESSIAN_SERIALIZER);
//        socketServer.publishService(helloService, HelloService.class);
        RpcServer server = new SocketServer("127.0.0.1", 9998, CommonSerializer.HESSIAN_SERIALIZER);
        server.start();
    }
}
