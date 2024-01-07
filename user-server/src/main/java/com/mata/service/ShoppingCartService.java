package com.mata.service;

import com.mata.dto.Result;
import com.mata.pojo.ShoppingCart;

public interface ShoppingCartService {
    //给新用户添加购物车
    void addNewShoppingCart(Integer userId);

    //根据用户id获取购物车
    Result getShoppingCartById();

    //添加商品到购物车
    Result addGoodToShoppingCart(Long goodId);

    //在购物车删除商品
    Result deleteGood(Long goodId);

    //删除所有商品
    Result deleteAllGood();
}
