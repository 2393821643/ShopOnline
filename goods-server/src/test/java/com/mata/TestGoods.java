package com.mata;

import cn.hutool.core.util.IdUtil;
import com.mata.dao.GoodsDao;
import com.mata.feign.EmailClient;
import com.mata.pojo.Goods;
import com.mata.service.GoodsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class TestGoods {
    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private EmailClient emailClient;
    @Autowired
    private GoodsService goodsService;

    @Test
    public void testSearch() throws IOException {
        Goods goodById = goodsDao.getGoodById(213L);
        System.out.println(goodById);
    }


    @Test
    public void testAdd() throws IOException {
        Goods goodById = goodsDao.getGoodById(231L);
        System.out.println(goodById.getName());
    }

    @Test
    public void testUpdate() throws IOException {
        Goods good = goodsDao.getGoodById(1724656130086633472L);
        System.out.println(good.getStock());
        Integer stock = good.getStock() -1;
        System.out.println(stock);
        goodsDao.updateStockGoods(1724656130086633472L, stock);

    }

    @Test
    public void send(){
        emailClient.sendBuyFailCode("2393821643@qq.com","a");
    }
}
