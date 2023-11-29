package com.sky.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
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
    @Autowired
    private WorkspaceService workspaceService;

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
    

        /**
     * 导出运营数据报表
     * @param response
     */
    @Override
    public void exportDates(HttpServletResponse response) {
        // 1. 查询数据库，获取营业数据
        LocalDateTime beginTime = LocalDateTime.now().minusDays(30).with(LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.now().minusDays(1).with(LocalTime.MAX);

        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);

        // 2. 通过POI将数据写入到excel表格中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            // 基于模板文件创建一个新的Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);
            
            // 获取sheet
            XSSFSheet sheet = excel.getSheet("Sheet1");

            // 填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + beginTime.toLocalDate() + "至" + endTime.toLocalDate());

            // 填充数据--营业额
            sheet.getRow(3).getCell(2).setCellValue(businessData.getTurnover());

            // 填充数据--订单完成率
            sheet.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());

            // 填充数据--新增用户数
            sheet.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());

            // 填充数据--有效订单数
            sheet.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());

            // 填充数据--平均客单价
            sheet.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());

            // 填充数据--明细数据
            for (int i = 0; i < 30; ++i) {
                BusinessDataVO businessDatai = workspaceService.getBusinessData(beginTime.plusDays(i), beginTime.plusDays(i).with(LocalTime.MAX));

                XSSFRow row = sheet.getRow(i + 7);
                row.getCell(1).setCellValue(beginTime.plusDays(i).toLocalDate().toString());
                row.getCell(2).setCellValue(businessDatai.getTurnover());
                row.getCell(3).setCellValue(businessDatai.getValidOrderCount());
                row.getCell(4).setCellValue(businessDatai.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDatai.getUnitPrice());
                row.getCell(6).setCellValue(businessDatai.getNewUsers());
            }

            // 3. 通过输出流将excel文件下载到客户端浏览器中
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            // 关闭资源
            outputStream.close();
            excel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
