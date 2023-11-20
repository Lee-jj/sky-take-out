package com.sky.service;

import java.util.List;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

public interface ShoppingCartService {

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     * @return
     */
    List<ShoppingCart> list();

    /**
     * 清空购物车
     * @return
     */
    void clean();

    /**
     * 删除购物车中的一个商品
     * @param shoppingCartDTO
     * @return
     */
    void sub(ShoppingCartDTO shoppingCartDTO);
    
}
