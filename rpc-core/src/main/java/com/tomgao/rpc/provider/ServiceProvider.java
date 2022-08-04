package com.tomgao.rpc.provider;

/**
 * 保存和提供服务实例对象
 */
public interface ServiceProvider {

    /**
     * <T> 表示该方法是泛型方法
     * @param service
     * @param <T>
     */
    <T> void addServiceProvider(T service, Class<T> serviceClass);

    Object getServiceProvider(String serviceName);
}
