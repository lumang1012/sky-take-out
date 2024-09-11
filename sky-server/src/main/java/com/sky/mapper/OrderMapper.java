package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    //插入订单
    void insert(Orders order);

    //分页查询
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    //根据订单id查询订单
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    //更新订单
    void update(Orders orders);

    //计算各个状态订单数量
    @Select("Select count(*) from orders where status = #{status}")
    Integer countStatus(Integer status);

    //
    @Select("select * from orders where status = #{status} and order_time < #{time} ")
    List<Orders> getByStatusAndOrdertimeLT(Integer status, LocalDateTime time);

    //营业额数据统计
    Double sumByMap(Map map);

    //计算订单数据
    Integer countByMap(Map map);

    //销量数据统计
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);

    //
}
