package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {

    //统计营业额数据
    TurnoverReportVO getTurnover(LocalDate begin, LocalDate end);

    //统计用户数据
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    //订单数据统计
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);
    //销量数据排名
    SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end);

    //导出数据报表
    void exportBusinessData(HttpServletResponse response);
}
