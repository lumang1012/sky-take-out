package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper

public interface CategoryMapper {

    //新增分类
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into category (type, name, sort, " +
            "status, create_time, update_time, create_user, update_user) values (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Category category);

    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    //根据id删除分类
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);

    //修改分类
    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);


    //根据type查询分类
    List<Category> list(Integer type);

//    //根据id获取种类名字
//    String getById(Long categoryId);
}
