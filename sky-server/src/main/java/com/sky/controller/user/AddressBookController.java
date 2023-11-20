package com.sky.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "用户地址管理相关接口")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;
    
    /**
     * 查询用户地址列表
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询用户地址列表")
    public Result<List<AddressBook>> list() {
        log.info("查询用户地址列表");
        List<AddressBook> list = addressBookService.list();
        return Result.success(list);
    }

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation("新增地址")
    public Result add(@RequestBody AddressBook addressBook) {
        log.info("新增地址，{}", addressBook);
        addressBookService.add(addressBook);
        return Result.success();
    }
}
