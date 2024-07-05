package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类相关接口")
@Slf4j
public class CategoryController {

    //注入service
    @Autowired
    private CategoryService categoryService;


    //新增分类
    @PostMapping
    @ApiOperation("新增分类")
    //泛型<String>??????
    public Result<String> save(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    //分类分页查询
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类分页查询{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    //删除分类
    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result<String> deleteById(Long id) {
        log.info("删除id{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    //修改分类，疑问，没有查询操作，为何可以回显
    @PutMapping
    @ApiOperation("修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    //修改分类的状态
    @PostMapping("/status/{status}")
    @ApiOperation("修改分类状态")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        log.info("修改分类状态{}{}", status, id);
        categoryService.startOrStop(status, id);
        return Result.success();
    }

    //根据类型查询分类，感觉多余，因为之前的pagequequery就已经实现了
    //不多余，这个类型查询要求status为1，但是前端的查询类型是如何同时连接两个接口，
    //没被使用？？？？？？
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> list(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }

}