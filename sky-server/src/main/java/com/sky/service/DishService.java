package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishService {

    /**
     * 新增菜品及对应的口味表
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);
    
}