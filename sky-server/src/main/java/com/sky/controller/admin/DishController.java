package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    //新增菜品
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        //由于菜品的改变所以需要清除缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        redisTemplate.delete(key);
        return Result.success();
    }

    //按页数查询菜品
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> dishPageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询{}",dishPageQueryDTO);
        PageResult pageResult = dishService.dishPageQuery(dishPageQueryDTO);

        return Result.success(pageResult);
    }

    //批量删除
    @DeleteMapping
    @ApiOperation("菜品的批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除{}",ids);
        dishService.deleteBatch(ids);
        //由于菜品涉及种类太多，所以所有菜品均删除
        //redisTemplate.delete("dish_*");该代码是错误的，因为删除操作没有通配符
        cleanCatch("dish_*");
        return Result.success();
    }

    //根据id查询菜品
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id获取菜品{}",id);
        //不要忘记还有口味
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    //修改菜品根据id
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品{}",dishDTO);
        //修改菜品兼顾修改口味
        dishService.updateWithFlavor(dishDTO);
        //如果涉及修改分类会涉及两个菜品分类，所以全删除缓存
        cleanCatch("dish_*");
        return Result.success();
    }

    //根据分类id查寻菜品
    @GetMapping("/list")
    @ApiOperation("根据分类id查菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    //菜品的起售停售
    @PostMapping("/status/{status}")
    @ApiOperation("菜品的起售停售")
    public Result startOrStop(@PathVariable Integer status, Long id){
        dishService.startOrStop(status, id);
        cleanCatch("dish_*");
        return Result.success();
    }

    //由于清理全部缓存的代码重复多次的使用，所以可以将该代码提取为一个方法
    private void cleanCatch(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
