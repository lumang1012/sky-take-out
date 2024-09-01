package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceimpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SetmealMapper setmealMapper;

//    新增菜品
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        //拷贝属性
        BeanUtils.copyProperties(dishDTO,dish);
        //此时不用在设置创建时间等操作，因为之前已经做过公共字段自动插入
        //不过注意要加注解，在mapper的update与insert中
        dishMapper.insert(dish);

        //获取insert语句生成的主键值
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {dishFlavor.setDishId(dishId);});
        }
        //向口味表插入n条数据
        dishFlavorMapper.insertBatch(flavors);
    }

    //分页查询菜品
    @Override
    public PageResult dishPageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        List<DishVO> records = page.getResult();
        long total = page.getTotal();
        return new PageResult(total, records);
    }

    //批量删除菜品，
    @Transactional
    //因为删除菜品的同时还要删除口味表中的口味
    @Override
    public void deleteBatch(List<Long> ids) {
        //首先判断菜品是否起售，起售的菜品不可以删除
        for(Long id : ids) {
            //通过id获取菜品对象
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断该菜品是否与套餐关联，与套餐关联的菜品不可以删除，除非解除关联
        List<Long> setmealIds = setmealDishMapper.getSetmeaIdsByDishIds(ids);
        if(setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品后也要同时删除口味
        for(Long id : ids){
            //删除菜品
            dishMapper.deleteById(id);
            //删除与菜品相连的口味
            dishFlavorMapper.deleteByDishId(id);
        }


    }

    //获取菜品以及口味
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //获取菜品信息
        Dish dish = dishMapper.getById(id);
        Long categoryId = dish.getCategoryId();
        //通过种类id来获取种类信息???????????
//        String categoryName = categoryMapper.getById(categoryId);
        //通过菜品id获取口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        //将查询到的数据封装到vo
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    //修改菜品及口味
    //涉及修改不要忘记autofill这个注解，修改人和时间
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        //拷贝属性，注意加上修改人和时间
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        //关于口味的修改，先将原本的口味全部删除，然后再批量插入新的口味
        //有个疑问，为何不使用更新方法,估计想更省事，直接服用代码
        dishFlavorMapper.deleteByDishId(dish.getId());
        //重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //首先判断是否存在口味数据
        if(flavors != null && flavors.size() > 0){
            //为每一个口味设置菜品id
            flavors.forEach(dishFlavor -> {dishFlavor.setDishId(dishDTO.getId());});
            dishFlavorMapper.insertBatch(flavors);
        }

    }


    //根据类型id查询菜品
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    //修改菜品的起售停售
    @Override
    public void startOrStop(Integer status, Long id) {
        //首先判断该菜品是否是停售，所属的套餐是否为起售，
        if(status == StatusConstant.DISABLE){
            List<Long> ids = new ArrayList<>();
            ids.add(id);
            List<Long> setmeaIds = setmealDishMapper.getSetmeaIdsByDishIds(ids);
            for(Long setmeaId : setmeaIds){
                Integer status1 = setmealMapper.getById(setmeaId).getStatus();
                if(status1 == StatusConstant.DISABLE){
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);
    }
}
