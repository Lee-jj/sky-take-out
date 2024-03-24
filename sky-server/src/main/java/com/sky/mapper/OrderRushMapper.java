package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.entity.TOrderRush;

@Mapper
public interface OrderRushMapper {

    @Select("select * from t_order_rush where user_id = #{userId} and order_id = #{orderId}")
    TOrderRush getByUserIdAndOrderId(Long userId, Long orderId);
    
}
