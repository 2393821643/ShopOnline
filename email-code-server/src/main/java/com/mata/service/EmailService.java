package com.mata.service;

import com.mata.dto.Result;

public interface EmailService {
    //发送登录验证码
    Result sendLoginCode(String email);

    //发送修改密码验证码
    Result sendChangePasswordCode(String email);

    //发送购买成功邮箱
    Result sendBuySuccessCode(String email,String goodName);

    Result sendBuyFailCode(String email, String goodName);
}
