package com.mata.util;

public class RedisConstants {
    //验证码登录的键前缀
    public static final String LOGIN_CODE_KEY = "login:code:";

    //验证码登录的存活时间
    public static final Long LOGIN_CODE_TTL = 2L;

    //修改密码验证码的键前缀
    public static final String CHANGE_PASSWORD_CODE_KEY = "change:password:code:";

    //修改密码验证码的存活时间
    public static final Long CHANGE_PASSWORD_CODE_TTL = 2L;



}
