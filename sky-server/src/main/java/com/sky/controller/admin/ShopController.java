package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")//目的是区分项目
@Slf4j
@RequestMapping("/admin/shop")
@Api(tags = "店铺有关的接口")
public class ShopController {

    public static final String KEY = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;


//    设置店铺营业状态
    @ApiOperation("设置店铺营业状态")
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态{}",status==1 ? "营业中":"打样中");
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }


    //查询店铺营业状态
    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> getStatus(){

        Integer shop_status = (Integer)redisTemplate.opsForValue().get(KEY);
        log.info("获取到店铺的营业状态为{}",shop_status==1?"营业中":"打烊中");
        return Result.success(shop_status);
    }
}



