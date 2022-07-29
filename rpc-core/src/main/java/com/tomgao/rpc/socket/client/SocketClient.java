package com.tomgao.rpc.socket.client;

import com.tomgao.rpc.RpcClient;
import com.tomgao.rpc.RpcException;
import com.tomgao.rpc.entity.RpcRequest;
import com.tomgao.rpc.entity.RpcResponse;
import com.tomgao.rpc.enumeration.ResponseCode;
import com.tomgao.rpc.enumeration.RpcError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 远程方法调用的消费者
 */
public class SocketClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final String host;
    private final int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //    public Object sendRequest(RpcRequest rpcRequest, String host, int port) throws IOException {
//        Socket socket = new Socket(host, port);
//        try {
//            // ?????
//            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            logger.info("客户端socket输入输出流建立成功");
//            objectOutputStream.writeObject(rpcRequest);
//            objectOutputStream.flush();
//            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();
//            if (rpcResponse == null) {
//                logger.error("rpc服务调用失败, 失败service: {}", rpcRequest.getInterfaceName());
//                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "service: " + rpcRequest.getInterfaceName());
//            }
//            if (rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
//                logger.error("调用服务失败, service : {}, reponse: {}", rpcRequest.getInterfaceName(), rpcResponse);
//                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
//            }
//            return rpcResponse.getData();
//        } catch (Exception e) {
//            logger.error("调用时有错误发生", e);
//            throw new RpcException("服务调用失败", e);
//        }
//    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();
            if(rpcResponse == null) {
                logger.error("服务调用失败，service：{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            if(rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
                logger.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            return rpcResponse.getData();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误发生：", e);
            throw new RpcException("服务调用失败: ", e);
        }
    }
}
