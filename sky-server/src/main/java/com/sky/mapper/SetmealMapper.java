package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.DeleteMapping;

@Mapper
public interface SetmealMapper {

    //根据种类id查询此种类下套餐的个数
    @Select("select count() from setmeal where category_id = #{id}")
    Integer countByCategoryId(Long id);

    //新增套餐
    @AutoFill(OperationType.INSERT)//用于自动填充，如创建时间，人物
    void insert(Setmeal setmeal);

    //分页查询
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    //根据套餐id获取套餐
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    //根据套餐id删除套餐
    @Delete("delete from setmeal where id = #{setmealId}")
    void deleteById(Long setmealId);

    //更新套餐
    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);
}
