package com.mata.service;

import com.mata.dto.Result;

public interface UserService {
    //通过密码登录
    public Result loginByPassword(String accounts, String password);

    //验证码登录
    public Result loginByCode(String email, String code);

    //修改密码
    public Result changePassword(String password, String email, String code);

    //修改用户名
    public Result changeUsername(String username,String token);

    //退出登录
    public Result logOut(String token);


    //修改收货人信息
    Result changeReceiveMessage(String phone, String address, String consignee, String token);
}
