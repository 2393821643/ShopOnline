package com.mata.service;

import com.mata.dto.Result;
import com.mata.pojo.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {

    //订单缓存添加Redis数据库
    Result createOrderCache(String token,Long goodId,Integer count);

    //订单添加数据库
    void addOrder(Long id);

    //删除缓存订单
    void deleteOrderCache(Long id);

    //获取用户的订单
    Result getOrderByUserId(String token);

    //获取用户的订单
    Result getOrderByUserIdToAdmin(String token,Integer userId);

    //根据订单id获取订单
    Result getOrderById(String token, Long orderId);

    //修改订单状态
    Result putOrderState(String token, Long orderId);
}
