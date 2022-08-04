package com.tomgao.test;

import com.tomgao.rpc.api.HelloObject;
import com.tomgao.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author： tomgao
 * @date： 2022-08-04 20:03
 * @Description:
 */

public class HelloServiceImpl2 implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl2.class);
    @Override
    public String hello(HelloObject object) {
        logger.info("接收到消息: {}", object.getMessage());
        return "本次处理来自Socket服务";
    }
}
