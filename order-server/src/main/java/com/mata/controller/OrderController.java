package com.mata.controller;

import com.mata.dto.Result;
import com.mata.pojo.Order;
import com.mata.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    //创建订单缓存,后续商品正常的情况下可在mysql创建订单
    @PostMapping("/create-order-cache/{goodId}/{count}")
    public Result createOrderCache(@RequestHeader("authorization")String token,
                                   @PathVariable("goodId") Long goodId,
                                   @PathVariable("count") Integer count){
       return orderService.createOrderCache(token,goodId,count);
    }

    //根据userId获取用户订单，用户接口
    @GetMapping("/user-order")
    public Result getOrderByUserId(@RequestHeader("authorization") String token){
        return orderService.getOrderByUserId(token);
    }

    //根据userId获取订单，
    @GetMapping("/user-order-admin/{userId}")
    public Result getAllOrder(@RequestHeader("admin_authorization") String token,
                              @PathVariable("userId")Integer userId){
        return orderService.getOrderByUserIdToAdmin(token,userId);
    }

    //根据订单号获取订单，管理员接口
    @GetMapping("{orderId}")
    public Result getOrderById(@RequestHeader("admin_authorization") String token,
                               @PathVariable("orderId")Long orderId){
        return orderService.getOrderById(token,orderId);
    }


    //修改订单状态
    @PutMapping("/order-state/{orderId}")
    public Result putOrderState(@RequestHeader("admin_authorization") String token,
                                @PathVariable("orderId")Long orderId){
        return orderService.putOrderState(token,orderId);
    }
}
