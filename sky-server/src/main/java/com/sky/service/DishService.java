package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    //新增菜品，还有口味
    void saveWithFlavor(DishDTO dishDTO);

    //分页查询
    PageResult dishPageQuery(DishPageQueryDTO dishPageQueryDTO);

    //批量删除菜品
    void deleteBatch(List<Long> ids);

    //根据id获取菜品信息，还有口味
    DishVO getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDTO dishDTO);
}
