package com.tomgao.test;

import com.tomgao.rpc.annotation.Service;
import com.tomgao.rpc.api.ByeService;

/**
 * @author： tomgao
 * @date： 2022-08-07 20:17
 * @Description:
 */

@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye " + name;
    }
}
