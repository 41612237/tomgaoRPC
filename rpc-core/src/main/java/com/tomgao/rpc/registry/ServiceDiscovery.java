package com.tomgao.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @author： tomgao
 * @date： 2022-08-04 19:25
 * @Description: 服务发现接口
 */

public interface ServiceDiscovery {

    /**
     * 根据服务名称查找服务实体
     * @param serviceName
     * @return
     */
    InetSocketAddress lookupService(String serviceName);
}
