package com.mata;

import com.mata.dto.Result;
import com.mata.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestFun {
    @Autowired
    private AdminService adminService;

    @Test
    public void testLogin(){
        Result result = adminService.loginByPassword("10000", "123456");
        System.out.println(result);
    }
}
