package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
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
//通过传递来的菜品信息更新菜品及口味
    void updateWithFlavor(DishDTO dishDTO);
//根据分类id展示菜品
    List<Dish> list(Long categoryId);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);

    //修改菜品的起售停售
    void startOrStop(Integer status, Long id);
}
