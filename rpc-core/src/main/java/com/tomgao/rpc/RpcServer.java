package com.tomgao.rpc;

import com.tomgao.rpc.serializer.CommonSerializer;

public interface RpcServer {

    void start(int port);

    void setSerializer(CommonSerializer serializer);
}
