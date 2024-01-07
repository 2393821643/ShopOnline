package com.mata.dao;

import com.mata.pojo.Goods;
import com.mata.pojo.ShoppingCart;

public interface ShoppingCartDao {
    //给新用户添加配置单
    void addNewShoppingCart(Integer userId);

    //获取配置单
    ShoppingCart getShoppingCartById(Integer userId);

    //添加商品到购物车
    void addGoodToShoppingCart(Integer userId, Goods goods);

    //从购物车删除商品
    void deleteGoodById(Long goodId, Integer userId);

    //删除所有购物车商品
    void deleteAllGood(Integer userId);
}
