package com.mata;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mata.dao.ShoppingCartDao;
import com.mata.dao.UserDao;
import com.mata.dto.Result;
import com.mata.feign.GoodClient;
import com.mata.pojo.Goods;
import com.mata.pojo.ShoppingCart;
import com.mata.pojo.User;
import com.mata.service.ShoppingCartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Set;

@SpringBootTest
public class SimpleTest {
    @Autowired
    public UserDao userDao;
    @Test
    public void getUserByEmail() {
        //从数据库查此邮箱是否存在用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserEmail, "2393821643@qq.com");
        wrapper.select(User::getUserId, User::getUsername);
        User user = userDao.selectOne(wrapper);
        System.out.println(user);
    }

    @Test
    public void registerUser() {
        User user = new User();
        user.setUserEmail("2393821643@qq.com");
        //设置名字为10个随机字符
        user.setUsername("user_" + RandomUtil.randomString(10));
        //设密码为30个随机字符
        user.setUserPassword(RandomUtil.randomString(30));
        userDao.insert(user);
        user.setUserEmail(null);
        user.setUserPassword(null);
    }

    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis(){
        Set<String> keys = stringRedisTemplate.keys("test:abc:*");
        List<String> list = stringRedisTemplate.opsForValue().multiGet(keys);
        System.out.println(list);
    }
    @Test
    public void testGet(){
        String s = stringRedisTemplate.opsForValue().get("test:abc:xx1");
        System.out.println(s);
    }

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ShoppingCartDao shoppingCartDao;
    @Test
    public void testMongo(){
        ShoppingCart shoppingCartById = shoppingCartDao.getShoppingCartById(10001);
        System.out.println(shoppingCartById);
    }

    @Test
    public void testAdd(){
        Result result = goodClient.getGoodByKey(1725791966182236160L);
        Object goodObj = result.getData();
        String goodJson = JSONUtil.toJsonStr(goodObj);
        Goods goods = JSONUtil.toBean(goodJson, Goods.class);
        System.out.println(goods);
        shoppingCartDao.addGoodToShoppingCart(10001,goods);
    }

    @Test
    public void testDelete(){
        shoppingCartDao.deleteGoodById(1725791966182236160L,10001);
    }


    @Autowired
    private GoodClient goodClient;
    @Test
    public void testClient(){
        Result result = goodClient.getGoodByKey(17257919661822361L);
        Object goodObj = result.getData();
        String goodJson = JSONUtil.toJsonStr(goodObj);
        Goods goods = JSONUtil.toBean(goodJson, Goods.class);
        System.out.println(goods.getId());

    }
}
