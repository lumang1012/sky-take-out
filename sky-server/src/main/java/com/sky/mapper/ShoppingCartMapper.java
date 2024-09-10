package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    //条件查询商品
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    //根据商品的id，查询商品并更新其数量
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    //向购物车中添加商品
    //购物车商品的id是自增的，所以在插入中没有
    @Insert("insert into shopping_cart (name,user_id, " +
            "dish_id, setmeal_id, dish_flavor, number, amount, image, create_time) values (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor}," +
            "#{number},#{amount},#{image},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    //清空购物车
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    //根据id删除购物车中的商品
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);


    //批量插入数据库
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
