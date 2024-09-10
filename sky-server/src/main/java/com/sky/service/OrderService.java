package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    //用户下单
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    //分页查询历史订单
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    //根据订单id查询订单详情
    OrderVO details(Long id);

    //根据订单id取消订单
    void cancel(Long id);

    //再来一单
    void repetition(Long id);

    //订单查询
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    //各个状态订单数量统计
    OrderStatisticsVO statistics();

    //接单
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    //拒单
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    //取消订单
    void cancelOrders(OrdersCancelDTO ordersCancelDTO);

    //派送订单
    void delivery(Long id);

    //完成订单
    void complete(Long id);
}
