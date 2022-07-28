package com.tomgao.rpc.registry;

public interface ServiceRegistry {

    /**
     * <T> 表示该方法是泛型方法
     * 将服务注册进注册表
     * @param service
     * @param <T>
     */
    <T> void registry(T service);

    /**
     * 根据服务名称获取服务实体
     * @param serviceName
     * @return
     */
    Object getService(String serviceName);
}
