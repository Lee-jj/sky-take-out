package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.sky.entity.SetmealDish;

@Mapper
public interface SetmealDishMapper {
    
    /**
     * 根据菜品id获得套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量插入套餐菜品信息
     * @param setmealDishes
     */
    void insertSetmealDishes(List<SetmealDish> setmealDishes);
}
