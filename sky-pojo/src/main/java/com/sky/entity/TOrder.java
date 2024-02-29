package com.sky.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TOrder implements Serializable{


    /**
     * 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
     */
    public static final Integer PENDING_PAYMENT = 1;
    public static final Integer TO_BE_CONFIRMED = 2;
    public static final Integer CONFIRMED = 3;
    public static final Integer DELIVERY_IN_PROGRESS = 4;
    public static final Integer COMPLETED = 5;
    public static final Integer CANCELLED = 6;

    /**
     * 支付状态 0未支付 1已支付 2退款
     */
    public static final Integer UN_PAID = 0;
    public static final Integer PAID = 1;
    public static final Integer REFUND = 2;
    
    /**
     * 配送状态 0选择具体时间 1立即送出
     */
    public static final Integer CHOSE_TIME = 0;
    public static final Integer SEND_NOW = 1;


    private static final long serialVersionUID = 1L;
    
    private Long id;

    private String number;

    private Integer status;

    private Long userId;

    private LocalDateTime orderTime;

    private Integer payMethod;

    private Integer payStatus;

    private BigDecimal amount;

    private String remark;

    private String phone;

    private String address;

    //配送状态  1立即送出  0选择具体时间
    private Integer deliveryStatus;

    private LocalDateTime deliveryTime;

}
