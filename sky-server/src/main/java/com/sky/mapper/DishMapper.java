package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface DishMapper {
    @Select("select count() from dish where category_id = #{id}")
    Integer countByCategoryId(Long id);

    //新增菜品
    @AutoFill(value = OperationType.INSERT)

    void insert(Dish dish);

    //分页查询
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    //通过id获取菜品对象
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    //通过id删除菜品
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    //更新菜品
    //千万不要忘记加注解
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    //根据菜品分类id查询菜品(动态查询菜品)
    List<Dish> list(Dish dish);
}
