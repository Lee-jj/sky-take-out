package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.entity.AddressBook;

@Mapper
public interface AddressBookMapper {

    /**
     * 根据用户id查询地址列表
     * @param userId
     * @return
     */
    @Select("select * from address_book where user_id=#{userId}")
    List<AddressBook> getByUserId(Long userId);
    
}
