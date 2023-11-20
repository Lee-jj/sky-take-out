package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
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

    /**
     * 新增地址
     * @param addressBook
     */
    @Insert("insert into address_book (user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default)" + 
            "values (#{userId}, #{consignee}, #{sex}, #{phone}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(AddressBook addressBook);
    
}
