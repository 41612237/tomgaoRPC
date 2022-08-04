package com.tomgao.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册中心通用接口
 */
public interface ServiceRegistry {

    /**
     *  将一个服务注册进注册表
     * @param serviceName 服务名称
     * @param inetSocketAddress 提供服务的地址
     */
     void registry(String serviceName, InetSocketAddress inetSocketAddress);

    /**
     * 根据服务名称查找服务实体IP和端口
     * @param serviceName
     * @return
     */
     InetSocketAddress lookupService(String serviceName);
}
