package com.mata.service;

import com.mata.dto.GoodsDto;
import com.mata.dto.Result;

import java.io.IOException;

public interface GoodsService {
    //添加商品
    Result addGoods(GoodsDto goodsDto);

    //通过关键字获取商品
    Result getGoodsByKeyword(String key, Integer page);

    //通过id获取商品
    Result getGoodById(Long id);

    //修改库存,购买时调用，不开接口，消息队列调用
    void updateStockGoods(Long orderId,String userEmail,Long id,Integer count) throws IOException;

    //修改商品
    Result updateGoodById(Long goodId,GoodsDto goodsDto);

    //删除商品
    Result deleteGoodByID(Long goodId);

    //设置秒杀商品
    Result setFlashKillGoodById(Long goodId, Long time);

    //获取所有秒杀商品
    Result getFlashKillGood();

    //获取所有新品
    Result getNewGood();
}
