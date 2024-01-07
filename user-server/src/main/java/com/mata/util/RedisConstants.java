package com.mata.util;

public class RedisConstants {
    //验证码登录的键前缀
    public static final String LOGIN_CODE_KEY = "login:code:";


    //登录token的键前缀
    public static final String LOGIN_TOKEN_KEY = "login:token:";

    //登录token的存活时间
    public static final Long USER_TOKEN_TTL = 30L;

    //修改密码验证码的键前缀
    public static final String CHANGE_PASSWORD_CODE_KEY = "change:password:code:";




}
