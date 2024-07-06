package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishFlavor implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    //菜品id
    private Long dishId;
    //口味名字
    private String name;
    //口味数据
    private String value;

}
