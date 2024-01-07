package com.mata.interceptor;


import com.mata.util.AdminHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断是否拦截（ThreadLocal是否有用户）
        if(AdminHolder.getAdmin() == null){
            response.setStatus(401);
            return false;
        }
        return true;
    }

}
