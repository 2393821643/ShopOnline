package com.mata.controller;

import com.mata.dto.Result;
import com.mata.service.AdminService;
import com.mata.util.AdminHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    //根据id/邮箱 密码登录
    @GetMapping("/password-login/{accounts}/{password}")
    public Result loginByPassword(@PathVariable("accounts") String accounts,
                                  @PathVariable("password") String password) {
        return adminService.loginByPassword(accounts, password);
    }

    //获取登录后用户信息
    @GetMapping("/after-login")
    public Result getUserAfterLogin(){
        return new Result(AdminHolder.getAdmin(),null,null);
    }

    //退出登录
    @DeleteMapping()
    public Result logOut(HttpServletRequest request){
        String token = request.getHeader("admin_authorization");
        return adminService.logOut(token);
    }
}
