package com.mata.service.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mata.service.ShoppingCartService;
import com.mata.dao.UserDao;
import com.mata.dto.Code;
import com.mata.dto.Result;
import com.mata.dto.UserDto;

import com.mata.exception.BusinessException;
import com.mata.pojo.User;
import com.mata.service.UserService;
import com.mata.util.RedisConstants;
import com.mata.util.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加token
     *
     * @param user 用户信息
     * @return token
     */
    private String addToken(User user) {
        //设置一个token
        String token = IdUtil.simpleUUID();
        //User->Json
        String userJson = JSONUtil.toJsonStr(user);
        //存入Redis 存活时间30MIN
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_TOKEN_KEY + token, userJson,
                RedisConstants.USER_TOKEN_TTL, TimeUnit.MINUTES);
        return token;
    }

    /**
     * 用户通过id/email和密码登录
     *
     * @param accounts id/email
     * @return token
     */
    @Override
    public Result loginByPassword(String accounts, String password) {
        //判断accounts是id还是email
        boolean email = Validator.isEmail(accounts);
        //如果是邮箱
        if (email) {
            User resultUser = getUserByEmailAndPassword(accounts, password);
            if (resultUser != null) {
                //设置一个token
                String token = addToken(resultUser);
                return new Result(token, null);
            } else {
                return new Result(Code.LOGIN_ERR, "密码错误");
            }
        }
        //如果是id
        else {
            User resultUser = getUserByIdAndPassword(accounts, password);
            if (resultUser != null) {
                String token = addToken(resultUser);
                return new Result(token, null);
            } else {
                return new Result(Code.LOGIN_ERR, "密码错误");
            }
        }
    }


    /**
     * 通过email和password查用户
     *
     * @param email    邮箱
     * @param password 密码
     * @return user
     */
    private User getUserByEmailAndPassword(String email, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        //设置条件，password进行md5加密
        wrapper.eq(User::getUserEmail, email).eq(User::getUserPassword, DigestUtils.md5DigestAsHex(password.getBytes()));
        wrapper.select(User::getUserId, User::getUsername,User::getUserEmail,User::getUserAddress,User::getUserPhone,User::getUserConsignee);
        return userDao.selectOne(wrapper);
    }

    /**
     * 通过id和password查用户
     *
     * @param id       用户Id
     * @param password 密码
     * @return user
     */
    private User getUserByIdAndPassword(String id, String password) {
        int userId = 0;
        try {
            userId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new BusinessException("密码错误", Code.LOGIN_ERR);
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        //设置条件，password进行md5加密
        wrapper.eq(User::getUserId, userId).eq(User::getUserPassword, DigestUtils.md5DigestAsHex(password.getBytes()));
        wrapper.select(User::getUserId, User::getUsername,User::getUserEmail,User::getUserAddress,User::getUserPhone,User::getUserConsignee);
        return userDao.selectOne(wrapper);
    }


    /**
     * 邮箱验证码登录
     *
     * @param email 邮箱
     * @param code  验证码
     * @return token/message
     */
    @Override
    public Result loginByCode(String email, String code) {
        //判断是不是email
        boolean isEmail = Validator.isEmail(email);
        if (!isEmail) {
            return new Result(null, Code.EMAIL_ERR, "邮箱格式错误");
        } else {
            //获取此邮箱的验证码
            String returnCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + email);
            //判断是否对应
            if (!Objects.equals(returnCode, code)) {
                return new Result(null, Code.LOGIN_ERR, "登陆失败，验证码错误");
            }
            //从数据库查此邮箱是否存在用户
            User resultUser = getUserByEmail(email);
            //如果不存在则注册用户
            if (resultUser == null) {
                resultUser = registerUser(email);
            }
            //添加token
            String token = addToken(resultUser);
            return new Result(token, null);
        }
    }

    /**
     * 通过email查用户
     *
     * @param email 邮箱
     * @return user 用户信息
     */
    private User getUserByEmail(String email) {
        //从数据库查此邮箱是否存在用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserEmail, email);
        wrapper.select(User::getUserId, User::getUsername,User::getUserEmail,User::getUserAddress,User::getUserPhone,User::getUserConsignee);
        return userDao.selectOne(wrapper);
    }

    /**
     * 添加新的用户
     *
     * @param email 邮箱
     * @return user 新创建的user
     */
    private User registerUser(String email) {
        User user = new User();
        user.setUserEmail(email);
        //设置名字为10个随机字符
        user.setUsername("user_" + RandomUtil.randomString(10));
        //设密码为30个随机字符
        user.setUserPassword(RandomUtil.randomString(30));
        userDao.insert(user);
        user.setUserEmail(null);
        user.setUserPassword(null);
        //添加购物车
        shoppingCartService.addNewShoppingCart(user.getUserId());
        return user;
    }


    /**
     * 修改密码
     *
     * @param password 修改的密码
     * @param email    邮箱
     * @param code     验证码
     */
    public Result changePassword(String password, String email, String code) {
        //判断是不是email
        boolean isEmail = Validator.isEmail(email);
        if (!isEmail) {
            return new Result(null, Code.EMAIL_ERR, "邮箱格式错误");
        }
        //获取此邮箱的验证码
        String returnCode = stringRedisTemplate.opsForValue().get(RedisConstants.CHANGE_PASSWORD_CODE_KEY + email);
        //判断验证码是否对应
        if (!Objects.equals(returnCode, code)) {
            return new Result(null, Code.CHANGE_PASSWORD_ERR, "修改失败，验证码错误");
        }
        //判断密码是否符合规范（大于5，小于20）
        if (password.length() < 5 || password.length() > 20) {
            return new Result(null, Code.CHANGE_PASSWORD_ERR, "修改失败，密码格式错误");
        }
        //判断此邮箱用户是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserEmail, email);
        User resultUser = userDao.selectOne(wrapper);
        if (resultUser == null) {
            return new Result(null, Code.CHANGE_PASSWORD_ERR, "修改失败，此用户不存在");
        }
        //修改密码，同时md5加密
        resultUser.setUserPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        userDao.update(resultUser, wrapper);
        return new Result(null, null, "修改成功");
    }

    /**
     * 修改用户名
     *
     * @param username 新用户名
     * @return user/message
     */
    @Transactional
    public Result changeUsername(String username,String token){
        //判断用户名是否标准
        if(username.length()>30 ||username.length()==0){
            return new Result(false, Code.CHANGE_USERNAME_ERR, "修改失败,名字长度不符合大于0，小于30");
        }
        //获取当前线程（请求）的User信息
        UserDto resultUser = UserHolder.getUser();
        User user = BeanUtil.toBean(resultUser, User.class);
        user.setUsername(username);
        //修改数据库User的信息
        userDao.updateById(user);
        //修改redis中的信息
        String userJson = JSONUtil.toJsonStr(user);
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_TOKEN_KEY+token,userJson);
        return new Result(true,null,"修改成功");
    }

    @Override
    public Result logOut(String token) {
        //删除redis中的用户
        stringRedisTemplate.delete(RedisConstants.LOGIN_TOKEN_KEY+token);
        return new Result();
    }


    /**
     * 修改收获信息
     *
     * @param phone 手机号
     * @param address 地址
     * @param consignee 收货人姓名
     * @return message
     */
    @Override
    public Result changeReceiveMessage(String phone, String address, String consignee, String token) {
        //校验手机号
        if(!Validator.isMobile(phone)){
            return new Result(false,Code.CHANGE_RECEIVE_MESSAGE_ERR,"手机号校验错误");
        }
        if(address.length()>50 ||address.length()==0){
            return new Result(false,Code.CHANGE_RECEIVE_MESSAGE_ERR,"地址校验失败");
        }
        if(consignee.length()>50 ||consignee.length()==0){
            return new Result(false,Code.CHANGE_RECEIVE_MESSAGE_ERR,"收货人校验失败");
        }
        //获取当前线程（请求）的User信息
        UserDto resultUser = UserHolder.getUser();
        User user = BeanUtil.toBean(resultUser, User.class);
        user.setUserPhone(phone);
        user.setUserAddress(address);
        user.setUserConsignee(consignee);
        //修改数据库User的信息
        userDao.updateById(user);
        //修改Redis的User信息
        String userJson = JSONUtil.toJsonStr(user);
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_TOKEN_KEY+token,userJson);
        return new Result(true,null,"修改成功");
    }

}
