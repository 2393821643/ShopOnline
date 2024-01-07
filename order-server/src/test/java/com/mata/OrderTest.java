package com.mata;

import com.mata.pojo.Order;
import com.mata.pojo.User;
import com.mata.service.OrderService;
import com.mata.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OrderTest {
    @Autowired
   private OrderServiceImpl orderService;

    @Test
    void test(){
        User user=new User();
        user.setUserPhone("2341");
        user.setUserAddress("213");
        //user.setUserConsignee("1223");
        boolean b = orderService.judgeReceivingMessage(user);
        System.out.println(b);
    }


//    @Test
//    private void testUpdate(){
//        Order order = new Order();
//        order.setOrderId(1725158051503398912);
//    }
}
