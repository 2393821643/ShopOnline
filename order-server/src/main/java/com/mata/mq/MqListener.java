package com.mata.mq;

import com.mata.service.OrderService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqListener {
    @Autowired
    private OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.add-order"),
            exchange = @Exchange(name = "mata.order",type = ExchangeTypes.DIRECT),
            key = {"add-order"}
    ))
    public void listenAddOrderQueue(String orderId) {
        orderService.addOrder(Long.parseLong(orderId));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.delete-order-cache"),
            exchange = @Exchange(name = "mata.order",type = ExchangeTypes.DIRECT),
            key = {"delete-order-cache"}
    ))
    public void listenDeleteOrderCacheQueue(String orderId) {
        orderService.deleteOrderCache(Long.parseLong(orderId));
    }
}
