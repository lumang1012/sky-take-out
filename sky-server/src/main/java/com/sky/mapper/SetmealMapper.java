package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    //根据种类id查询此种类下套餐的个数
    @Select("select count() from setmeal where category_id = #{id}")
    Integer countByCategoryId(Long id);
}
