package com.sky.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

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

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 起售中的菜品不能删除
        for (Long id: ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        
        // 被套餐关联的菜品不能删除
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 可以删除
        // for (Long id: ids) {
        //     dishMapper.deleteById(id);
        //     // 删除菜品关联的口味数据,不管有没有直接根据dish_id进行删除
        //     dishFlavorMapper.deleteByDishId(id);
        // }

        // 优化
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);

    }

    /**
     * 根据id查询菜品及对应的口味表
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 根据id查询菜品数据
        Dish dish = dishMapper.getById(id);

        // 根据dishId查询口味数据
        List<DishFlavor> dishflavors = dishFlavorMapper.getByDishId(id);

        // 进行封装 dishVO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishflavors);

        return dishVO;
    }

    /**
     * 根据id更新菜品及对应的口味表
     * @param dishDTO
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 更新基本信息
        dishMapper.update(dish);

        // 口味数据先删除再添加
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors!= null && flavors.size() > 0) {
            // 为flavors中的每一个都设置dishID
            flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
            
            // 向dish_flavor表中插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }
    
}
