package com.tomgao.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author： tomgao
 * @date： 2022-08-07 18:32
 * @Description: 负载均衡
 */

public interface LoadBalancer {

    Instance select(List<Instance> instances);
}
