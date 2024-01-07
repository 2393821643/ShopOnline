package com.mata.interceptor;


import cn.hutool.core.util.StrUtil;

import cn.hutool.json.JSONUtil;
import com.mata.dto.UserDto;
import com.mata.util.RedisConstants;
import com.mata.util.UserHolder;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;


public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate=stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1:获取请求体的token
        String token = request.getHeader("authorization");
        //1.1: 判断token是否为空
        if (StrUtil.isBlank(token)) {
            return true;
        }
        //2:获取redis的用户
        String userJson = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_TOKEN_KEY + token);
        //3：判断用户存在
        if (userJson==null) {
            return true;
        }
        //将Json->UserDto
        UserDto userDto = JSONUtil.toBean(userJson, UserDto.class);
        //5：存在，保存到ThreadLocal
        UserHolder.saveUser(userDto);
        //6:刷新token有效期
        stringRedisTemplate.expire(RedisConstants.LOGIN_TOKEN_KEY + token,RedisConstants.USER_TOKEN_TTL, TimeUnit.MINUTES);
        //7:放行
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //移除用户
        UserHolder.removeUser();
    }
}
