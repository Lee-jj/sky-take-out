package com.sky.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.dto.UserLoginDTOv1;
import com.sky.entity.TUser;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import com.sky.vo.UserLoginVOv1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user/user")
@Api(tags = "用户相关接口")
@Slf4j
public class UserController {
    
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("微信用户登录,{}", userLoginDTO.getCode());
        // 微信登录
        User user = userService.wxLogin(userLoginDTO);
        
        // 为微信登录生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId()); 
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }


    /**
     * 手机号码登录（临时）
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login/v1")
    @ApiOperation("用户登录v1")
    public Result<UserLoginVOv1> loginV1(@RequestBody UserLoginDTOv1 userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);
        // 用户登录
        TUser user = userService.login(userLoginDTO);

        // 生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
            jwtProperties.getUserSecretKey(),
            jwtProperties.getUserTtl(),
            claims);

        UserLoginVOv1 userLoginVO = UserLoginVOv1.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .phone(user.getPhone())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }


    /**
     * 新增用户
     * @param userLoginDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增用户")
    public Result save(@RequestBody UserLoginDTOv1 userLoginDTO) {
        log.info("新增用户，{}", userLoginDTO);
        userService.save(userLoginDTO);
        return Result.success();
    }

}
