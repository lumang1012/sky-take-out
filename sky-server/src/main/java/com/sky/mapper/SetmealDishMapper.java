package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import javax.annotation.ManagedBean;
import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> getSetmeaIdsByDishIds(List<Long> dishIds);

//    批量插入套餐菜品
    void insertBatch(List<SetmealDish> setmealDishes);

    //根据套餐id删除套餐中的菜品
    @Delete("delete from setmeal_dish where setmeal_id= #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    //根据套餐id查询套餐中的菜品
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);
}
