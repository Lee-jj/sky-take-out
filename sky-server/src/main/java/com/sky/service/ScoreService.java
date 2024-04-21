package com.sky.service;

import java.util.List;

public interface ScoreService {

    /**
     * 用户打分
     * @param dishId
     * @param score
     * @return
     */
    void score(Long dishId, Integer score);
    
    /**
     * 获取推荐菜品列表
     * @return
     */
    List<Long> getRecommendation();

}
