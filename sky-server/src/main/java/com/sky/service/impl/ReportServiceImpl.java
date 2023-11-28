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

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

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

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;

        for (LocalDate date = begin; date.isBefore(end.plusDays(1)); date = date.plusDays(1)) {
            dateList.add(date);

            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Integer totalCount = orderMapper.getOrderCountByTimeAndStatus(map);
            map.put("status", Orders.COMPLETED);
            Integer validCount = orderMapper.getOrderCountByTimeAndStatus(map);
            orderCountList.add(totalCount);
            totalOrderCount += totalCount;
            validOrderCountList.add(validCount);
            validOrderCount += validCount;
        }

        return OrderReportVO.builder()
               .dateList(StringUtils.join(dateList, ","))
               .orderCountList(StringUtils.join(orderCountList, ","))
               .validOrderCountList(StringUtils.join(validOrderCountList, ","))
               .totalOrderCount(totalOrderCount)
               .validOrderCount(validOrderCount)
               .orderCompletionRate(validOrderCount.doubleValue() / totalOrderCount.doubleValue())
               .build();
    }

    /**
     * 统计商品top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> list = orderDetailMapper.getTop10ByTime(beginTime, endTime, Orders.COMPLETED);
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();

        for (GoodsSalesDTO goodsSalesDTO : list) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }
        
        return SalesTop10ReportVO.builder()
               .nameList(StringUtils.join(nameList, ","))
               .numberList(StringUtils.join(numberList, ","))
               .build();
    }
    
}
