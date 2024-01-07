package com.mata.mq;

import cn.hutool.json.JSONUtil;
import com.mata.dto.OrderMessage;
import com.mata.service.GoodsService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MqListener {
    @Autowired
    private GoodsService goodsService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.check-stock"),
            exchange = @Exchange(name = "mata.goods",type = ExchangeTypes.DIRECT),
            key = {"check-stock"}
    ))
    public void listenCheckStockQueue(String orderMessageJson) {
        try {
            //将接收的消息转化为pojo
            OrderMessage orderMessage = JSONUtil.toBean(orderMessageJson, OrderMessage.class);
            goodsService.updateStockGoods(orderMessage.getOrderId(),orderMessage.getUserEmail(), orderMessage.getGoodId(), orderMessage.getCount());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
