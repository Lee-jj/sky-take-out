package com.sky.utils;

import org.springframework.beans.factory.annotation.Autowired;

import com.sky.entity.TUser;
import com.sky.mapper.UserMapper;
import org.springframework.stereotype.Component;

/**
 * 生成用户的工具类，[很耗时但是不想优化了...]
 */
@Component
public class UserUtil {
    
    @Autowired
    private UserMapper userMapper;

    public void CreateUser() {
        TUser user = new TUser();
        for (int i = 0; i < 10000; ++i) {
            String phone = "1234567" + String.format("%04d", Integer.valueOf(i));
            String userName = "user_" + String.format("%04d", Integer.valueOf(i));
            user.setStatus(1);
            user.setPhone(phone);
            user.setUserName(userName);
            user.setPassword("e10adc3949ba59abbe56e057f20f883e");
            userMapper.insertTUser(user);
        }
    }
}
