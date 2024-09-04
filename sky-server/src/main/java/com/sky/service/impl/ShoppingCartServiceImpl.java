package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.SetmealService;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    //添加购物车
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //首先通过线程的获取用户id
        System.out.printf("id是%d", BaseContext.getCurrentId());

        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //通过条件查询获取购物车中的商品
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);//一处错误，写作shoppingCartDTO

        //首先判断添加的商品购物车是否已有
        if(shoppingCarts != null && shoppingCarts.size()==1){
            //表明购物车中已存在该商品
            shoppingCart = shoppingCarts.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber()+1);
            shoppingCartMapper.updateNumberById(shoppingCart);
        }
        else{
            //商品不存在于购物车，进一步判断是套餐还是菜品
            Long dishId = shoppingCartDTO.getDishId();//错误写作shoppingCart
            if(dishId != null){
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
            }
            else{
                //如果为套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
            }

            //记算商品数量，以及该商品的创建时间
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    //查看购物车
    @Override
    public List<ShoppingCart> list() {
        return shoppingCartMapper.list(ShoppingCart.builder().userId(BaseContext.getCurrentId()).build());
    }

    //清空购物车
    @Override
    public void clean() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

    //删除购物车中的商品
    @Override
    public void delete(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if(shoppingCartList != null && shoppingCartList.size() >0){
            shoppingCart = shoppingCartList.get(0);
            if(shoppingCart.getNumber() == 1){
                shoppingCartMapper.deleteById(shoppingCart.getId());
            }
            else{
                shoppingCart.setNumber(shoppingCart.getNumber()-1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }
    }
}
