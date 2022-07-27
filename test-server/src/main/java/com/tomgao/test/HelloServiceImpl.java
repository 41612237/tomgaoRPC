package com.tomgao.test;

import com.tomgao.rpc.api.HelloObject;
import com.tomgao.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到:{}", object.getMessage());
        return "成功调用hello, id = " + object.getId();
    }
}
