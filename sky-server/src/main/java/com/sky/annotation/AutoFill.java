package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {

    //OperationType value()：定义了一个属性 value，其类型为 OperationType 枚举，表示数据库操作类型。
    //数据库操作类型
    OperationType value();
}
