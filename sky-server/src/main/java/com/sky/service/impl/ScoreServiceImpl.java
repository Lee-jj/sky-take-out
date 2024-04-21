package com.sky.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.context.BaseContext;
import com.sky.entity.TScore;
import com.sky.mapper.ScoreMapper;
import com.sky.service.ScoreService;

@Service
public class ScoreServiceImpl implements ScoreService{

    @Autowired
    private ScoreMapper scoreMapper;

    /**
     * 用户打分
     * @param dishId
     * @param score
     * @return
     */
    @Override
    public void score(Long dishId, Integer score) {
        // 根据用户id和dishid查询是否有打分记录
        Long userId = BaseContext.getCurrentId();
        TScore tScore = scoreMapper.getScoreByUserIdAndDishId(userId, dishId);

        if (tScore != null) {
            // 如果有则修改
            scoreMapper.updateByUserIdAndDishId(userId, dishId, score);
        } else {
            // 如果没有则添加
            scoreMapper.insert(userId, dishId, score);
        }
    }
    
}
