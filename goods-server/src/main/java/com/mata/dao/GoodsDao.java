package com.mata.dao;

import com.mata.pojo.Goods;

import java.io.IOException;
import java.util.List;

public interface GoodsDao {
    //添加商品
    void addGoods(Goods goods) throws IOException;

    //通过id获取商品
    Goods getGoodById(Long id) throws IOException;

    //通过关键字获取商品
    List<Goods> getGoodsByKeyword(String keyword,Integer page) throws IOException;

    //修改库存,购买时调用，不开接口，消息队列调用
    void updateStockGoods(Long id,Integer stock) throws IOException;

    //通过id删除id
    void deleteGood(Long id) throws IOException;


}
