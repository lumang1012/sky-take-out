package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import javax.annotation.ManagedBean;
import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> getSetmeaIdsByDishIds(List<Long> dishIds);
}
