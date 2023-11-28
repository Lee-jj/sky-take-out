package com.sky.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

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

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 构造dateList
        List<LocalDate> dateList = new ArrayList<>();
        // 构造totalUserList
        List<Integer> totalUserList = new ArrayList<>();
        // 构造newUserList
        List<Integer> newUserList = new ArrayList<>();

        for (LocalDate date = begin; date.isBefore(end.plusDays(1)); date = date.plusDays(1)) {
            dateList.add(date);

            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("end", endTime);
            Integer totalUser = userMapper.getSumByTime(map);

            map.put("begin", beginTime);
            Integer newUser = userMapper.getSumByTime(map);

            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                    .dateList(StringUtils.join(dateList, ","))                    
                    .totalUserList(StringUtils.join(totalUserList, ","))
                    .newUserList(StringUtils.join(newUserList, ","))
                    .build();
    }
    
}
