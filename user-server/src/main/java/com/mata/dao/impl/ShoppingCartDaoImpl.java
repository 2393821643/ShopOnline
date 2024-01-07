package com.mata.dao.impl;

import com.mata.dao.ShoppingCartDao;
import com.mata.pojo.Goods;
import com.mata.pojo.ShoppingCart;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ShoppingCartDaoImpl implements ShoppingCartDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 添加购物车
     * @param userId 用户id
     */
    @Override
    public void addNewShoppingCart(Integer userId) {
        List<Goods> goodsList=new ArrayList<>();
        ShoppingCart shoppingCart = new ShoppingCart(userId,goodsList);
        mongoTemplate.insert(shoppingCart);
    }


    /**
     * 根据用户id获取购物车
     * @param userId 用户id
     */
    @Override
    public ShoppingCart getShoppingCartById(Integer userId) {
        return mongoTemplate.findById(userId, ShoppingCart.class);
    }

    /**
     * 添加商品到购物车
     * @param userId 用户id
     * @param goods 商品信息
     */
    @Override
    public void addGoodToShoppingCart(Integer userId, Goods goods) {
        Query query = new Query(Criteria.where("_id").is(userId));
        Update update = new Update();
        update.push("goodList",goods);
        UpdateResult updateResult = mongoTemplate.upsert(query, update, ShoppingCart.class);
    }


    /**
     * 从购物车删除商品
     * @param userId 用户id
     * @param goodId 商品id
     */
    @Override
    public void deleteGoodById(Long goodId, Integer userId) {
        Query query = new Query(Criteria.where("_id").is(userId));
        //获取购物车信息
        ShoppingCart shoppingCart = mongoTemplate.findById(userId, ShoppingCart.class);
        List<Goods> goodsList = shoppingCart.getGoodsList();
        goodsList.removeIf(goods -> Objects.equals(goods.getId(), goodId));
        //设置数据库
        Update update=new Update();
        update.set("goodList",goodsList);
        UpdateResult updateResult = mongoTemplate.upsert(query, update, ShoppingCart.class);
    }

    /*
    * 删除所有购物车商品
    **/
    @Override
    public void deleteAllGood(Integer userId) {
        Query query = new Query(Criteria.where("_id").is(userId));
        //获取购物车信息
        ShoppingCart shoppingCart = mongoTemplate.findById(userId, ShoppingCart.class);
        List<Goods> goodsList = shoppingCart.getGoodsList();
        goodsList.clear();
        //设置数据库
        Update update=new Update();
        update.set("goodList",goodsList);
        UpdateResult updateResult = mongoTemplate.upsert(query, update, ShoppingCart.class);
    }
}
