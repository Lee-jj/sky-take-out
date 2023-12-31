package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.PathVariable;

import com.sky.entity.SetmealDish;
import com.sky.result.Result;
import com.sky.vo.DishItemVO;

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

    /**
     * 根据套餐id删除套餐菜品表信息
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据套餐id查询菜品信息
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);

    /**
     * 根据套餐id批量删除菜品信息
     * @param setmealIds
     */
    void deleteBySetmealIds(List<Long> setmealIds);

    /**
     * 根据套餐id查询菜品详细信息
     * @param id
     * @return
     */
    List<DishItemVO> getDishBySetmealId(Long setmealId);
}
