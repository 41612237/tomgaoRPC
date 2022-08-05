package com.tomgao.rpc.transport;

import com.tomgao.rpc.entity.RpcRequest;
import com.tomgao.rpc.serializer.CommonSerializer;

public interface RpcClient {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);

}
