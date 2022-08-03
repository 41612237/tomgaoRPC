package com.tomgao.rpc;

import com.tomgao.rpc.entity.RpcRequest;
import com.tomgao.rpc.serializer.CommonSerializer;

public interface RpcClient {

    Object sendRequest(RpcRequest rpcRequest);

    void setSerializer(CommonSerializer serializer);

}
