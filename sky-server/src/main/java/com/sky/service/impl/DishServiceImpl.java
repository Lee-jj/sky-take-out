package com.sky.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品及对应的口味表
     * @param dishDTO
     */
    @Override
    @Transactional  // 开启事务，需要使用两张表，保持原子性
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 向dish表中插入一条数据
        dishMapper.insert(dish);
        
        Long dishId = dish.getId(); // 获取插入后的菜品id,需要设置动态sql中的属性

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors!= null && flavors.size() > 0) {
            // 为flavors中的每一个都设置dishID
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            
            // 向dish_flavor表中插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }
    
}
