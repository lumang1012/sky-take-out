package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService{

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    //营业额统计
    @Override
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        //首先获取日期，判断begin不等于end
        while(!begin.equals(end)){
            begin = begin.plusDays(1);//获得指定的后一天
            dateList.add(begin);
        }

        //获取营业额，
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //划定一天的范围
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //因为要传递多个条件，所以用map传递
            Map map = new HashMap<>();
            map.put("status", Orders.COMPLETED);
            map.put("begin", beginTime);
            map.put("end", endTime);
            Double turnover = orderMapper.sumByMap(map);
            //获取到的营业额还要判断其是否为空，为空，转为0.0
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    //统计用户数据
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Double> totalUserList = new ArrayList<>();
        List<Double> newUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Double totalUser = getUserCount(null, endTime);
            totalUserList.add(totalUser);
            Double newUser = getUserCount(beginTime, endTime);
            newUserList.add(newUser);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    //统计订单数据
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        if(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //调用私有的方法，计算每天的订单书，以及有效订单数
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        Integer totalOrderCount = orderCountList.stream().reduce(Integer :: sum).get();
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer :: sum).get();
        //订单完成率
        Double orderCompletionRate = 0.0;
        //确保分母不为零
        if(totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }


    //销量数据排名
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesDTOList = orderMapper.getSalesTop10(beginTime, endTime);
        List<String> nameList = salesDTOList.stream().map(GoodsSalesDTO :: getName).collect(Collectors.toList());
        List<Integer> numberList = salesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }

    //通过时间区间，以及订单状态计算订单数
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status){
        Map map = new HashMap();
        map.put("status", status);
        map.put("begin", begin);
        map.put("end", end);
        Integer count = orderMapper.countByMap(map);
        return count;
    }


    //通过时间区间计算用户数量
    private Double getUserCount(LocalDateTime beginTime, LocalDateTime endTime){
        Map map = new HashMap();
        map.put("begin", beginTime);
        map.put("end", endTime);
        return userMapper.countByMap(map);
    }
}
