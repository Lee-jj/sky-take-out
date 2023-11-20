package com.sky.service;

import java.util.List;

import com.sky.entity.AddressBook;

public interface AddressBookService {

    /**
     * 查询用户地址列表
     * @return
     */
    List<AddressBook> list();

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    void add(AddressBook addressBook);

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 查询默认地址
     * @return
     */
    AddressBook getDefaultAddress();

    /**
     * 修改地址信息
     * @param addressBook
     * @return
     */
    void update(AddressBook addressBook);

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    void setDefault(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     * @return
     */
    void deleteById(Long id);
    
}
