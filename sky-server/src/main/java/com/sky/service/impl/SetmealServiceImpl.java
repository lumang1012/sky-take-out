package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.controller.admin.SetmealController;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;//????这是干嘛的
//    新增套餐
    @Override
    @Transactional//保证套餐表与套餐菜品关联表均运行
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        //为何此处不添加修改人与修改时间
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //向套餐表注入数据
        setmealMapper.insert(setmeal);
        //获取生成套餐id
        Long setmealId = setmeal.getId();
        //将生成的套餐id全部注入菜品与套餐的表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {setmealDish.setSetmealId(setmealId);});
        //保存套餐和菜品的关联关系
        setmealDishMapper.insertBatch(setmealDishes);
    }

    //分页查询
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        int pageNum = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    //批量删除套餐
    @Override
    public void deleteBatch(List<Long> ids) {
        //只能删除停售的套餐
        ids.forEach(id ->{Setmeal setmeal = setmealMapper.getById(id);
        if(StatusConstant.ENABLE==setmeal.getStatus()){
        //起售的套餐不可以删除
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        });

        ids.forEach(setmealId ->{
            //根据id删除套餐表
            setmealMapper.deleteById(setmealId);
            //删除套餐菜品关系表
            setmealDishMapper.deleteBySetmealId(setmealId);
        });
    }

    //根据id获取套餐
    @Override
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    //修改套餐
    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {
        //先将setmealDTO转化为setmeal
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //不要忘记自动填充
        setmealMapper.update(setmeal);
        //获取套餐id
        Long setmealId = setmealDTO.getId();
        //获取套餐中的菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //搞不懂为啥要重写设置套餐id，原先是否带有？？？
        setmealDishes.forEach(setmealDish -> {setmealDish.setSetmealId(setmealId);});
        setmealDishMapper.deleteBySetmealId(setmealId);
        setmealDishMapper.insertBatch(setmealDishes);

    }

    @Override
    public void startOrStop(Integer status, Long id) {
        //首先要判断是否要修改为起售，因为，如果要修改为起售，前提是套餐中的菜品要都是起售的
        if(status == StatusConstant.ENABLE){
            Setmeal setmeal = setmealMapper.getById(id);
            List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
            if(setmealDishes!=null && setmealDishes.size()>0) {

                setmealDishes.forEach(setmealDish -> {
                    Integer status2 = dishMapper.getById(setmealDish.getDishId()).getStatus();
                    if (status2 == StatusConstant.DISABLE) {
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }

                });
            }
        }

        //将套餐修改状态
        Setmeal setmeal = Setmeal.builder().id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);

    }

}
