package com.sky.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 构造dateList
        List<LocalDate> dateList = new ArrayList<>();
        // 构造turnoverList
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date = begin; date.isBefore(end.plusDays(1)); date = date.plusDays(1)) {
            dateList.add(date);
            
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Double turnover = orderMapper.getSumByTimeAndStatus(beginTime, endTime, Orders.COMPLETED);
            turnover = turnover == null? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        String dateListString = StringUtils.join(dateList, ",");
        String turnoverListString = StringUtils.join(turnoverList, ",");

        return TurnoverReportVO.builder()
               .dateList(dateListString)
               .turnoverList(turnoverListString)
               .build();
    }
    
}
