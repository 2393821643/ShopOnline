package com.mata.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mata.dao.AdminDao;
import com.mata.dto.Code;
import com.mata.dto.Result;
import com.mata.exception.BusinessException;
import com.mata.pojo.Admin;
import com.mata.service.AdminService;
import com.mata.util.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.concurrent.TimeUnit;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminDao adminDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 添加token
     *
     * @param admin 管理员信息
     * @return token
     */
    private String addToken(Admin admin) {
        //设置一个token
        String token = IdUtil.simpleUUID();
        //User->Json
        String userJson = JSONUtil.toJsonStr(admin);
        //存入Redis 存活时间30MIN
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_TOKEN_KEY + token, userJson,
                RedisConstants.USER_TOKEN_TTL, TimeUnit.MINUTES);
        return token;
    }

    /**
     * 用户通过id和密码登录
     *
     * @param accounts id
     * @return token
     */
    @Override
    public Result loginByPassword(String accounts, String password) {
        Admin resultAdmin = getAdminByIdAndPassword(accounts, password);
        if (resultAdmin != null) {
            String token = addToken(resultAdmin);
            return new Result(token, null);
        } else {
            return new Result(Code.LOGIN_ERR, "密码错误");
        }
    }

    /**
     * 通过id和password查用户
     *
     * @param id       用户Id
     * @param password 密码
     * @return user
     */
    private Admin getAdminByIdAndPassword(String id, String password) {
        int adminId = 0;
        try {
            adminId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new BusinessException("密码错误", Code.LOGIN_ERR);
        }
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        //设置条件，password进行md5加密
        wrapper.eq(Admin::getAdminId, adminId).eq(Admin::getPassword, DigestUtils.md5DigestAsHex(password.getBytes()));
        wrapper.select(Admin::getAdminId);
        return adminDao.selectOne(wrapper);
    }

    @Override
    public Result logOut(String token) {
        stringRedisTemplate.delete(RedisConstants.LOGIN_TOKEN_KEY+token);
        return new Result();
    }
}
