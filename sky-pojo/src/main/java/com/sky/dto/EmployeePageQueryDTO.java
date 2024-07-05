package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeePageQueryDTO implements Serializable {//实现该接口可以由json文件转变为DTO
    //员工姓名
    private String name;
    //页码
    private int page;
    //每页记录数
    private int pageSize;
}
