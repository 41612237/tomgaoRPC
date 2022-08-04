package com.tomgao.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 序列化器的枚举
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {

    KRYO(0),
    JSON(1),
    HESSIAN(2),
    PROTOBUF(3);

    private final int code;
}
