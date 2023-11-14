package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;

@Mapper
public interface SetmealMapper {

    /**
     * 根据id查找总数
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入数据
     * @param setmeal
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 更新套餐数据
     * @param setmeal
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    /**
     * 根据套餐id批量删除套餐
     * @param ids
     */
    void deletBatch(List<Long> ids);

    /**
     * 根据分类id查询起售套餐
     * @param categoryId
     */
    @Select("select * from setmeal where category_id = #{categoryId} and status = 1")
    List<Setmeal> getByCategoryId(Long categoryId);
}
