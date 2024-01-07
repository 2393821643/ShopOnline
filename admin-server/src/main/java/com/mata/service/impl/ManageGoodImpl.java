package com.mata.service.impl;

import com.mata.dto.GoodsDto;
import com.mata.dto.Result;
import com.mata.feign.GoodClient;
import com.mata.service.ManageGoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManageGoodImpl implements ManageGoodService {
    @Autowired
    private GoodClient goodClient;
    /**
     * 添加商品
     * @param goodsDto 商品信息
     */
    @Override
    public Result addGood(GoodsDto goodsDto) {
        return goodClient.addGoods(goodsDto);
    }


    /**
     * 修改商品
     * @param goodId 商品id
     * @param goodsDto 商品信息
     */
    @Override
    public Result updateGoodById(Long goodId, GoodsDto goodsDto) {
        return goodClient.updateGood(goodId,goodsDto);
    }


    /**
     * 删除商品
     * @param goodId 商品id
     */
    @Override
    public Result deleteGoodByID(Long goodId) {
        return goodClient.deleteGood(goodId);
    }

    /**
     * 设置秒杀商品
     * @param goodId 商品id
     * @param time 秒杀时间，单位为分钟
     */
    @Override
    public Result setFlashKillGood(Long goodId, Long time) {
        return goodClient.setFlashKillGood(goodId,time);
    }
}
