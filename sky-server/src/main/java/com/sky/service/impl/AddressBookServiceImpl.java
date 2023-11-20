package com.sky.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 查询用户地址列表
     * @return
     */
    @Override
    public List<AddressBook> list() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = AddressBook.builder().userId(userId).build();
        List<AddressBook> list = addressBookMapper.list(addressBook);
        return list;
    }

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @Override
    public void add(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @Override
    public AddressBook getById(Long id) {
        AddressBook addressBook = AddressBook.builder().id(id).build();
        List<AddressBook> list = addressBookMapper.list(addressBook);
        return list.get(0);
    }

    /**
     * 查询默认地址
     * @return
     */
    @Override
    public AddressBook getDefaultAddress() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = AddressBook.builder().userId(userId).isDefault(1).build();
        List<AddressBook> list = addressBookMapper.list(addressBook);
        if (list != null && list.size() > 0)
            return list.get(0);
        else 
            return null;
    }

    /**
     * 修改地址信息
     * @param addressBook
     * @return
     */
    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @Override
    public void setDefault(AddressBook addressBook) {
        // 先把当前用户所有的地址设置为非默认地址
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.updateDefalutByUserId(addressBook);

        // 再把指定的地址设置为默认地址
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }
    
}
