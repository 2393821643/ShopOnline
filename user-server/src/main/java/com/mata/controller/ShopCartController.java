package com.mata.controller;

import com.mata.dto.Result;
import com.mata.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop-cart")
public class ShopCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    //获取购物车
    @GetMapping()
    public Result getShoppingCartById(){
        return shoppingCartService.getShoppingCartById();
    }

    //添加商品到购物车
    @PostMapping("/{goodId}")
    public Result addGoodToShoppingCart(@PathVariable("goodId") Long goodId){
        return shoppingCartService.addGoodToShoppingCart(goodId);
    }

    //从购物车删除商品
    @DeleteMapping("/{goodId}")
    public Result deleteGood(@PathVariable("goodId") Long goodId){
        return shoppingCartService.deleteGood(goodId);
    }


    //删除所有购物车商品
    @DeleteMapping("/delete-all")
    public Result deleteAllGood(){
        return shoppingCartService.deleteAllGood();
    }
}
