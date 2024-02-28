package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.dto.UserLoginDTOv1;
import com.sky.entity.TUser;
import com.sky.entity.User;

public interface UserService {
    
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);

    /**
     * 手机号码登录（临时）
     * @param userLoginDTO
     * @return
     */
    TUser login(UserLoginDTOv1 userLoginDTO);

    /**
     * 新增用户
     * @param userLoginDTO
     */
    void save(UserLoginDTOv1 userLoginDTO);
}
