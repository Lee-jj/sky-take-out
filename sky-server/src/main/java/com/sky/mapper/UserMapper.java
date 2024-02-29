package com.sky.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.entity.TUser;
import com.sky.entity.User;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户数据
     * @param user
     */
    void insert(User user);

    /**
     * 根据时间动态查询用户数量
     * @param map
     * @return
     */
    Integer getSumByTime(Map map);

    /**
     * 根据手机号查询用户
     * @param phone
     * @return
     */
    @Select("select * from t_user where phone = #{phone}")
    TUser getByPhone(String phone);

    /**
     * 插入用户暂时
     * @param user
     */
    void insertTUser(TUser user);

    /**
     * 根据id查询用户
     * @return
     */
    @Select("select * from t_user where id = #{id}")
    TUser getById(Long id);
}
