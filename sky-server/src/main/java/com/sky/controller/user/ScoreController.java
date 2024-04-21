package com.sky.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.result.Result;
import com.sky.service.ScoreService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user/score")
@Api(tags = "用户打分相关接口")
@Slf4j
public class ScoreController {
    
    @Autowired
    private ScoreService scoreService;

    /**
     * 用户打分
     * @param dishId
     * @param score
     * @return
     */
    @PostMapping
    @ApiOperation("用户打分")
    public Result score(Long dishId, Integer score) {
        log.info("用户打分，订单id：{}，评分：{}", dishId, score);
        scoreService.score(dishId, score);
        return Result.success();
    }
}
