package com.sky.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.sky.entity.TScore;

@Mapper
public interface ScoreMapper {
    
    /**
     * 根据用户id和得分值插入打分记录
     * @param userId
     * @param dishId
     * @param score
     */
    @Insert("insert into t_score(user_id, dish_id, score) values(#{userId}, #{dishId}, #{score})")
    void insert(Long userId, Long dishId, Integer score);

    /**
     * 根据用户id和菜品id查询打分记录
     * @param userId
     * @param dishId
     * @return
     */
    @Select("select * from t_score where user_id = #{userId} and dish_id = #{dishId}")
    TScore getScoreByUserIdAndDishId(Long userId, Long dishId);

    /**
     * 根据用户id和菜品id更新打分记录
     * @param userId
     * @param dishId
     * @param score
     */
    @Update("update t_score set score = #{score} where user_id = #{userId} and dish_id = #{dishId}")
    void updateByUserIdAndDishId(Long userId, Long dishId, Integer score);
}
