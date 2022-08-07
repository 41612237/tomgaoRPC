package com.tomgao.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.tomgao.rpc.enumeration.RpcError;
import com.tomgao.rpc.exception.RpcException;
import com.tomgao.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author： tomgao
 * @date： 2022-08-04 13:30
 * @Description: Nacos服务注册中心
 */

public class NacosServiceRegistry implements ServiceRegistry{

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void registry(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName, inetSocketAddress);
           } catch (NacosException e) {
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

}
