package com.tomgao.rpc;

import com.tomgao.rpc.entity.RpcRequest;

public interface RpcClient {

    Object sendRequest(RpcRequest rpcRequest);

}
