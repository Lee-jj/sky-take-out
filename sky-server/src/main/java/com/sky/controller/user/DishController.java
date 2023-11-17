package com.sky.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Api(tags = "用户菜品查询")
@Slf4j
public class DishController {
    
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("用户菜品查询")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("用户菜品查询,{}", categoryId);

        // 构造redis中的key（dish_分类id）
        String key = "dish_" + categoryId;

        // 查询redis中是否存在菜品数据
        List<DishVO> dishVOs = (List<DishVO>) redisTemplate.opsForValue().get(key);

        // 如果存在，直接返回，无需查询数据库
        if (dishVOs!= null && dishVOs.size() > 0) {
            return Result.success(dishVOs);
        }

        // 如果不存在，查询数据库，将查询到的数据放入redis中
        dishVOs = dishService.listWithFlavor(categoryId);
        redisTemplate.opsForValue().set(key, dishVOs);
        
        return Result.success(dishVOs);
    }
}
