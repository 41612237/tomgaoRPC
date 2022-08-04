package com.tomgao.rpc.transport.socket.server;

import com.tomgao.rpc.handler.RequestHandler;
import com.tomgao.rpc.provider.ServiceProvider;
import com.tomgao.rpc.provider.ServiceProviderImpl;
import com.tomgao.rpc.registry.NacosServiceRegistry;
import com.tomgao.rpc.transport.RpcServer;
import com.tomgao.rpc.enumeration.RpcError;
import com.tomgao.rpc.exception.RpcException;
import com.tomgao.rpc.registry.ServiceRegistry;
import com.tomgao.rpc.serializer.CommonSerializer;
import com.tomgao.rpc.util.ThreadPoolFactory;
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
    private CommonSerializer serializer;
    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;
    private ServiceProvider serviceProvider;

    public SocketServer(String host, int port) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
    }

    BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
    ThreadFactory threadFactory = Executors.defaultThreadFactory();

    public SocketServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
//        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
//        ThreadFactory threadFactory = Executors.defaultThreadFactory();
//        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    @Override
    public void start() {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器启动");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接IP: {} : {}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("连接时有错误发生..", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer =serializer;
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service);
        serviceRegistry.registry(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

}
