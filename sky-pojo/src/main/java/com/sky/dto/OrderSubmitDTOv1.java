package com.sky.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderSubmitDTOv1 implements Serializable{
    //付款方式
    private int payMethod;

    //备注
    private String remark;

    // 地址
    private String address;

    //配送状态  1立即送出  0选择具体时间
    private Integer deliveryStatus;

    //总金额
    private BigDecimal amount;
}
