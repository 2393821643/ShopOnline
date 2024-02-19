package com.mata.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mata.dao.OrderDao;
import com.mata.dto.Code;
import com.mata.dto.OrderMessage;
import com.mata.dto.Result;
import com.mata.feign.AdminClient;
import com.mata.feign.GoodsClient;
import com.mata.feign.UserClient;
import com.mata.pojo.Admin;
import com.mata.pojo.Goods;
import com.mata.pojo.Order;
import com.mata.pojo.User;
import com.mata.service.OrderService;
import com.mata.util.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private UserClient userClient;

    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private AdminClient adminClient;

    /**
     * 创建订单缓存
     *
     * @param token  用户token
     * @param goodId 商品id
     * @param count  购买的商品数量
     */
    @Override
    public Result createOrderCache(String token, Long goodId, Integer count) {
        Result userAfterLogin = userClient.getUserAfterLogin(token);
        //判断用户登录信息
        if (userAfterLogin == null) {
            return new Result(false, Code.USER_MESSAGE_ERR, "获取用户信息失败");
        }
        //获取token获取的User对象
        //将转化为实体对象
        Object objUser = userAfterLogin.getData();
        String userJson = JSONUtil.toJsonStr(objUser);
        User user = JSONUtil.toBean(userJson, User.class);
        //判断用户信息是否完整
        if (judgeReceivingMessage(user)) {
            return new Result(false, Code.USER_ADDRESS_ERR, "请完整设置收货地址");
        }
        //数量判断
        if (count<=0){
            return new Result(false, null, "数量错误");
        }
        //判断商品是否存在
        Result goodByKey = goodsClient.getGoodByKey(goodId);
        Object objData = goodByKey.getData();
        String goodJson = JSONUtil.toJsonStr(objData);
        Goods good = JSONUtil.toBean(goodJson, Goods.class);
        if (good.getId() == null) {
            return new Result(false, Code.USER_ADDRESS_ERR, "不存在此商品");
        }
        //创建订单缓存
        Order order = new Order(IdUtil.getSnowflakeNextId(), user.getUserId(),
                goodId, count,good.getName(), user.getUserAddress(), user.getUserPhone() ,user.getUserConsignee(), 0, good.getImg(), good.getPrice() * count);
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_ORDER_KEY + order.getOrderId().toString(),JSONUtil.toJsonStr(order),
                RedisConstants.CACHE_ORDER_TTL, TimeUnit.MINUTES);
        //将要发送的数据转成Json
        OrderMessage orderMessage = new OrderMessage(order.getOrderId(), user.getUserEmail(), goodId, count);
        String orderMessageJson = JSONUtil.toJsonStr(orderMessage);
        //消息队列发送消息查商品信息，发送购买状态邮箱
        rabbitTemplate.convertAndSend("mata.goods", "check-stock", orderMessageJson);
        //返回消息
        return new Result(true, null, "购买成功，购买消息将发到您的邮箱");
    }

    /**
     * 判断收货信息是否完整
     *
     * @return 信息完整返回true
     */
    private boolean judgeReceivingMessage(User user) {
        String userAddress = user.getUserAddress();
        String userPhone = user.getUserPhone();
        String userConsignee = user.getUserConsignee();
        return !StrUtil.isAllNotEmpty(userAddress, userPhone, userConsignee);
    }

    /**
     * 创建订单在数据库
     *
     * @param id 订单id
     */
    public void addOrder(Long id) {
        //从redis通过id获取order
        String orderJson = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_ORDER_KEY + id.toString());
        //json->pojo
        Order order = JSONUtil.toBean(orderJson, Order.class);
        orderDao.insert(order);
    }

    /**
     * 删除缓存订单
     *
     * @param id 订单id
     */
    public void deleteOrderCache(Long id) {
        stringRedisTemplate.delete(RedisConstants.CACHE_ORDER_KEY + id.toString());
    }


    /**
     * 获取用户的订单 用户接口
     *
     * @param token 用户的token 判断是否登录
     */
    @Override
    public Result getOrderByUserId(String token) {
        Result userAfterLogin = userClient.getUserAfterLogin(token);
        //判断用户登录信息
        if (userAfterLogin.getData() == null) {
            return new Result(false, Code.USER_MESSAGE_ERR, "获取用户信息失败");
        }
        //获取token获取的User对象
        //将转化为实体对象
        Object objUser = userAfterLogin.getData();
        String userJson = JSONUtil.toJsonStr(objUser);
        User user = JSONUtil.toBean(userJson, User.class);
        //从数据库获取订单
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, user.getUserId());
        List<Order> orders = orderDao.selectList(wrapper);
        return new Result(orders, null, null);
    }


    /**
     * 获取用户的订单 admin接口
     *
     * @param token  管理员的token 判断是否登录
     * @param userId 用户id
     */
    @Override
    public Result getOrderByUserIdToAdmin(String token, Integer userId) {
        Result userAfterLogin = adminClient.getUserAfterLogin(token);
        if (userAfterLogin == null) {
            return new Result(false, Code.USER_MESSAGE_ERR, "获取用户信息失败");
        }
        //从数据库获取订单
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        List<Order> orders = orderDao.selectList(wrapper);
        return new Result(orders, null, null);
    }


    /**
     * 获取用户的订单 admin接口
     *
     * @param token   管理员的token 判断是否登录
     * @param orderId 订单ID
     */
    @Override
    public Result getOrderById(String token, Long orderId) {
        Result userAfterLogin = adminClient.getUserAfterLogin(token);
        if (userAfterLogin == null) {
            return new Result(false, Code.USER_MESSAGE_ERR, "获取用户信息失败");
        }
        //从数据库获取订单
        Order order = getOrderByIdMethod(orderId);
        return new Result(order, null, null);
    }

    private Order getOrderByIdMethod(Long orderId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderId, orderId);
        return orderDao.selectOne(wrapper);
    }


    /**
     * 获取用户的订单 admin接口
     *
     * @param token   管理员的token 判断是否登录
     * @param orderId 订单ID
     */
    @Override
    public Result putOrderState(String token, Long orderId) {
        Result userAfterLogin = adminClient.getUserAfterLogin(token);
        if (userAfterLogin == null) {
            return new Result(false, Code.USER_MESSAGE_ERR, "获取用户信息失败");
        }
        Order order = getOrderByIdMethod(orderId);
        order.setState(1);
        orderDao.updateById(order);
        return new Result(true, null, "修改状态成功");

    }


}
