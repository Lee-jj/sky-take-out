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
    
}
