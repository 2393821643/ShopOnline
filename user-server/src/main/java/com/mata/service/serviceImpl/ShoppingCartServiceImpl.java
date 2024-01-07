package com.mata.service.serviceImpl;

import cn.hutool.json.JSONUtil;
import com.mata.dto.Code;
import com.mata.feign.GoodClient;
import com.mata.pojo.Goods;
import com.mata.service.ShoppingCartService;
import com.mata.dao.ShoppingCartDao;
import com.mata.dto.Result;
import com.mata.dto.UserDto;
import com.mata.pojo.ShoppingCart;
import com.mata.util.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartDao shoppingCartDao;

    @Autowired
    private GoodClient goodClient;

    /**
     * 添加购物车
     * @param userId 用户id
     */
    @Override
    public void addNewShoppingCart(Integer userId) {
        shoppingCartDao.addNewShoppingCart(userId);
    }


    /**
     * 根据用户id获取购物车
     */
    @Override
    public Result getShoppingCartById() {
        UserDto user = UserHolder.getUser();
        ShoppingCart shoppingCartById = shoppingCartDao.getShoppingCartById(user.getUserId());
        return new Result(shoppingCartById,null,null);
    }

    /**
     * 添加商品到购物车、
     * @param goodId 商品id
     */
    @Override
    public Result addGoodToShoppingCart(Long goodId) {
        //获取当前线程的uesr
        UserDto user = UserHolder.getUser();
        //检查商品是否存在
        Result goodByKey = goodClient.getGoodByKey(goodId);
        //转化为正常对象
        Object goodObj = goodByKey.getData();
        String goodJson = JSONUtil.toJsonStr(goodObj);
        Goods goods = JSONUtil.toBean(goodJson, Goods.class);
        if (goods.getId()==null){
            return new Result(false, Code.ADD_GOOD_ERR,"不存在此商品");
        }
        shoppingCartDao.addGoodToShoppingCart(user.getUserId(),goods);
        return new Result(true,null,"添加成功");
    }

    /**
     * 从购物车删除商品
     * @param goodId 商品id
     */
    @Override
    public Result deleteGood(Long goodId) {
        //获取当前线程的uesr
        UserDto user = UserHolder.getUser();
        shoppingCartDao.deleteGoodById(goodId,user.getUserId());
        return new Result(true,null,"删除成功");
    }


    /**
     * 删除购物车所有商品
     */
    @Override
    public Result deleteAllGood() {
        UserDto user = UserHolder.getUser();
        shoppingCartDao.deleteAllGood(user.getUserId());
        return new Result(true,null,"删除成功");
    }


}
