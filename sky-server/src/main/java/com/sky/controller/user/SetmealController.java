package com.sky.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Api(tags = "用户套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    @Cacheable(cacheNames = "setmealCache", key = "#categoryId")
    public Result<List<Setmeal>> list(Long categoryId) {
        log.info("根据分类id查询套餐,{}", categoryId);
        List<Setmeal> setmeals = setmealService.listByCategoryId(categoryId);
        return Result.success(setmeals);
    }
    
    /**
     * 根据套餐id查询包含菜品
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询包含菜品")
    public Result<List<DishItemVO>> getDishList(@PathVariable Long id) {
        log.info("根据套餐id查询包含菜品,{}", id);
        List<DishItemVO> dishList = setmealService.getDishList(id);
        return Result.success(dishList);
    }
}
