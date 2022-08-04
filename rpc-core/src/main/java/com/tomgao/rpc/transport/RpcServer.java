package com.tomgao.rpc.transport;

import com.tomgao.rpc.serializer.CommonSerializer;

public interface RpcServer {

    void start();

    void setSerializer(CommonSerializer serializer);

    <T> void publishService(T service, Class<T> serviceClass);
}
