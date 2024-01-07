package com.mata.controller;

import com.mata.dto.ChangePasswordDto;
import com.mata.dto.Result;
import com.mata.service.UserService;
import com.mata.util.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //根据id/邮箱 密码登录
    @GetMapping("/password-login/{accounts}/{password}")
    public Result loginByPassword(@PathVariable("accounts") String accounts,
                                  @PathVariable("password") String password) {
        return userService.loginByPassword(accounts, password);
    }


    //验证码登录
    @GetMapping("/code-login/{email}/{code}")
    public Result loginByCode(@PathVariable("email") String email,
                              @PathVariable("code") String code) {
        return userService.loginByCode(email, code);
    }


    //验证码修改密码
    @PutMapping("/change-password")
    public Result changePassword(@RequestBody ChangePasswordDto cpd) {
        return userService.changePassword(cpd.getPassword(), cpd.getEmail(), cpd.getCode());
    }

    //获取登录后用户信息
    @GetMapping("/after-login")
    public Result getUserAfterLogin(){
        return new Result(UserHolder.getUser(),null,null);
    }

    //修改用户名
    @PutMapping("/change-username/{username}")
    public Result changeUserName(@PathVariable("username") String username,HttpServletRequest request){
        String token = request.getHeader("authorization");
        return userService.changeUsername(username,token);
    }

    //修改收货信息
    @PutMapping("/change-receive-message/{phone}/{address}/{consignee}")
    public Result changeReceiveMessage(@PathVariable("phone") String phone,@PathVariable("address") String address
            ,@PathVariable("consignee")String consignee,HttpServletRequest request){
        String token = request.getHeader("authorization");
        return userService.changeReceiveMessage(phone,address,consignee,token);
    }

    //退出登录
    @DeleteMapping()
    public Result logOut(HttpServletRequest request){
        String token = request.getHeader("authorization");
        return userService.logOut(token);
    }

}
