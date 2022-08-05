package com.tomgao.rpc.transport.socket.server;

import com.sun.nio.sctp.ShutdownNotification;
import com.tomgao.rpc.handler.RequestHandler;
import com.tomgao.rpc.hook.ShutdownHook;
import com.tomgao.rpc.provider.ServiceProvider;
import com.tomgao.rpc.provider.ServiceProviderImpl;
import com.tomgao.rpc.registry.NacosServiceRegistry;
import com.tomgao.rpc.transport.RpcServer;
import com.tomgao.rpc.enumeration.RpcError;
import com.tomgao.rpc.exception.RpcException;
import com.tomgao.rpc.registry.ServiceRegistry;
import com.tomgao.rpc.serializer.CommonSerializer;
import com.tomgao.rpc.factory.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private final ExecutorService threadPool;
    private String host;
    private int port;
    private final CommonSerializer serializer = CommonSerializer.getByCode(DEFAULT_SERIALIZER);
    private final RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;
    private ServiceProvider serviceProvider;

    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
//        this.serializer = CommonSerializer.getByCode(serializer);
    }

    BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
    ThreadFactory threadFactory = Executors.defaultThreadFactory();

    public SocketServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
    }

    @Override
    public <T> void publishService(T service, Class<T> serviceClass) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service, serviceClass);
        serviceRegistry.registry(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

    @Override
    public void start() {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.bind(new InetSocketAddress(host, port));
            logger.info("服务器启动");
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接IP: {} : {}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("连接时有错误发生..", e);
        }
    }

}
