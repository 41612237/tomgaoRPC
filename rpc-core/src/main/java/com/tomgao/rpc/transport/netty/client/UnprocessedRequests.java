package com.tomgao.rpc.transport.netty.client;

import com.tomgao.rpc.entity.RpcResponse;
import java.util.concurrent.CompletableFuture;

import java.security.PublicKey;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author： tomgao
 * @date： 2022-08-07 11:40
 * @Description:
 */

public class UnprocessedRequests {

    // todo CompleteFuture干嘛的
    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedResponseFutures = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        unprocessedResponseFutures.put(requestId, future);
    }

    public void remove(String requestId) {
        unprocessedResponseFutures.remove(requestId);
    }

    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = unprocessedResponseFutures.remove(rpcResponse.getRequestId());
        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
