package com.sky.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController("userCategoryController")
@RequestMapping("user/category")
@Api(tags = "用户分类查询")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    
    /**
     * 用户分类查询
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("用户分类查询")
    public Result<List<Category>> list(Integer type) {
        log.info("用户分类查询,{}", type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
