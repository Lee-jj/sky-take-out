package com.sky.service;

public interface ScoreService {

    /**
     * 用户打分
     * @param dishId
     * @param score
     * @return
     */
    void score(Long dishId, Integer score);
    
}
