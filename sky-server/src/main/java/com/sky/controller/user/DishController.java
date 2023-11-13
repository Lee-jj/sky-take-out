package com.sky.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("用户菜品查询")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("用户菜品查询,{}", categoryId);
        List<DishVO> dishVOs = dishService.listWithFlavor(categoryId);
        return Result.success(dishVOs);
    }
}