package com.mata.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import com.mata.dto.Code;
import com.mata.dto.Result;
import com.mata.service.EmailService;
import com.mata.util.EmailMessage;
import com.mata.util.RedisConstants;
import com.mata.util.SendEmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SendEmailUtil sendEmailUtil;

    /**
     * 发送邮箱登录验证码
     *
     * @param email 邮箱
     * @return message
     */
    public Result sendLoginCode(String email) {
        if(email.length()>30){
            return new Result(null, Code.SEND_CODE_ERR, "邮箱格式错误");
        }
        //判断是不是email
        boolean isEmail = Validator.isEmail(email);
        if (!isEmail) {
            return new Result(null, Code.SEND_CODE_ERR, "邮箱格式错误");
        } else {
            //设置随机6位验证码
            String code = RandomUtil.randomNumbers(6);
            //发送邮箱
            sendEmailUtil.SendEmail(email, "Shop"
                    ,EmailMessage.SEND_LOGIN_CODE_MESSAGE_FOREBODY + code + EmailMessage.SEND_LOGIN_CODE_MESSAGE_BEHINDBODY);
            //存入Redis 存活时间2min
            stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + email, code,
                    RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);
            return new Result(null, null, "发送成功");
        }
    }

    /**
     * 发送邮箱修改密码验证码
     *
     * @param email 邮箱
     * @return message
     */
    @Override
    public Result sendChangePasswordCode(String email) {
        //判断是不是email
        boolean isEmail = Validator.isEmail(email);
        if (!isEmail) {
            return new Result(null, Code.SEND_CODE_ERR, "邮箱格式错误");
        } else {
            //设置随机6位验证码
            String code = RandomUtil.randomNumbers(6);
            //发送邮箱
            sendEmailUtil.SendEmail(email, "Shop"
                    ,EmailMessage.SEND_CHANGE_PASSWORD_CODE_MESSAGE_FOREBODY + code + EmailMessage.SEND_CHANGE_PASSWORD_CODE_MESSAGE_BEHINDBODY);
            //存入Redis 存活时间2min
            stringRedisTemplate.opsForValue().set(RedisConstants.CHANGE_PASSWORD_CODE_KEY + email, code,
                    RedisConstants.CHANGE_PASSWORD_CODE_TTL, TimeUnit.MINUTES);
            return new Result(null, null, "发送成功");
        }
    }

    /**
     * 发送购买成功邮箱
     *
     * @param email 邮箱
     * @param goodName 商品名称
     * @return message
     */
    @Override
    public Result sendBuySuccessCode(String email,String goodName) {
        boolean isEmail = Validator.isEmail(email);
        if (!isEmail) {
            return new Result(null, Code.SEND_CODE_ERR, "邮箱格式错误");
        }else {
            sendEmailUtil.SendEmail(email, "Shop"
                    ,EmailMessage.SEND_BUY_MESSAGE + goodName + EmailMessage.SEND_BUY_SUCCESS_MESSAGE);
        }
        return new Result(null, null, "发送成功");
    }


    /**
     * 发送购买邮箱
     *
     * @param email 邮箱
     * @param goodName 商品名称
     * @return message
     */
    @Override
    public Result sendBuyFailCode(String email, String goodName) {
        boolean isEmail = Validator.isEmail(email);
        if (!isEmail) {
            return new Result(null, Code.SEND_CODE_ERR, "邮箱格式错误");
        }else {
            sendEmailUtil.SendEmail(email, "Shop"
                    ,EmailMessage.SEND_BUY_MESSAGE + goodName + EmailMessage.SEND_BUY_FAIL_MESSAGE);
        }
        return new Result(null, null, "发送成功");
    }
}
