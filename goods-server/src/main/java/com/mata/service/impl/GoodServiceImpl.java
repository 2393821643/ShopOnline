package com.mata.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.mata.dao.GoodsDao;
import com.mata.dto.Code;
import com.mata.dto.GoodsDto;
import com.mata.dto.OrderMessage;
import com.mata.dto.Result;
import com.mata.exception.BusinessException;
import com.mata.feign.EmailClient;
import com.mata.pojo.Goods;
import com.mata.service.GoodsService;
import com.mata.util.CosClientUtil;
import com.mata.util.RedisConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class GoodServiceImpl implements GoodsService {
    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private CosClientUtil cosClientUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private EmailClient emailClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 添加商品
     *
     * @param goodsDto 商品信息
     */
    @Override
    public Result addGoods(GoodsDto goodsDto) {
        Goods good = new Goods(goodsDto);
        if (goodsDto.getName() == null || goodsDto.getImage() == null || goodsDto.getPrice() == null || goodsDto.getStock() == null) {
            return new Result(null, Code.ADD_GOOD_ERR, "请上传完整消息");
        }
        //设置全局Id 雪花算法
        good.setId(IdUtil.getSnowflakeNextId());
        //图片存储/转url
        String imgUrl = saveImg(goodsDto.getImage());
        good.setImg(imgUrl);
        try {
            goodsDao.addGoods(good);
        } catch (IOException e) {
            return new Result(null, Code.ADD_GOOD_ERR, "上传失败");
        }
        //添加到新品列表
        addNewGoodToList(good);
        return new Result(good.getId().toString(), null, "上传成功");
    }

    /**
     * 将图片上传图床
     *
     * @param image 图片
     */
    private String saveImg(MultipartFile image) {
        try {
            //将image转file 并写入本地
            byte[] fileBytes = image.getBytes();
            File convertedFile = new File("goods-server/src/main/resources/static/images/" + image.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(convertedFile);
            fos.write(fileBytes);
            fos.close();
            //写入cos
            String imgUrl = cosClientUtil.sendFile(convertedFile);
            //删除本地File
            convertedFile.delete();
            return imgUrl;
        } catch (IOException e) {
            throw new BusinessException("不是文件", Code.FILE_ERR);
        }

    }

    /**
     * 通过关键词找商品
     *
     * @param key  商品信息
     * @param page 当前页数
     */
    @Override
    public Result getGoodsByKeyword(String key, Integer page) {
        try {
            List<Goods> goodsByKeyword = goodsDao.getGoodsByKeyword(key, page);
            return new Result(goodsByKeyword, null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 通过id找商品
     *
     * @param id 商品id
     */
    @Override
    public Result getGoodById(Long id) {
        try {
            Goods good = goodsDao.getGoodById(id);
            return new Result(good, null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改库存，购买时调用
     *
     * @param orderId   订单id
     * @param id        商品id
     * @param count     购买数量
     * @param userEmail 用户邮箱
     */
    @Override
    public void updateStockGoods(Long orderId, String userEmail, Long id, Integer count) {
        String lockKey = RedisConstants.LOCK_GOOD_STOCK_KEY + id;
        try {
            //获取锁
            boolean isLock = tryLock(lockKey);
            if (!isLock) {
                //获取失败
                Thread.sleep(50);
                updateStockGoods(orderId, userEmail, id, count);
            } else {
                //获取商品消息
                Goods good = goodsDao.getGoodById(id);
                if (good.getId() == null) {
                    return;
                }
                Integer stock = good.getStock() - count;
                //库存>0 修改库存
                if (stock >= 0) {
                    goodsDao.updateStockGoods(id, stock);
                    //发邮箱成功购买
                    emailClient.sendBuySuccessCode(userEmail, good.getName());
                    //信息队列在OrderServer创建订单
                    rabbitTemplate.convertAndSend("mata.order", "add-order", orderId.toString());

                } else {
                    //发邮箱购买失败
                    emailClient.sendBuyFailCode(userEmail, good.getName());
                    //信息队列在OrderServer删除订单缓存
                    rabbitTemplate.convertAndSend("mata.order", "delete-order-cache", orderId.toString());
                }
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            //释放互斥锁
            unlock(lockKey);
        }
    }


    /**
     * 尝试获取锁
     *
     * @param key 代入的锁的key
     */
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", RedisConstants.LOCK_GOOD_STOCK_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }


    /**
     * 释放锁
     *
     * @param key 要释放的锁
     */
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }


    /**
     * 根据id修改商品
     *
     * @param goodId   商品id
     * @param goodsDto 要修改商品的数据
     */
    @Override
    public Result updateGoodById(Long goodId, GoodsDto goodsDto) {
        try {
            Goods good = goodsDao.getGoodById(goodId);
            if (good.getId() == null) {
                return new Result(false, Code.UPDATE_ERR, "不存在此商品");
            }
            if (goodsDto.getName() == null || goodsDto.getPrice() == null || goodsDto.getStock() == null) {
                return new Result(false, Code.UPDATE_ERR, "修改失败，数据不能为空");
            }
            good.setName(goodsDto.getName());
            good.setPrice(goodsDto.getPrice());
            good.setStock(goodsDto.getStock());
            goodsDao.addGoods(good);
            return new Result(true, null, "修改成功");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除商品
     *
     * @param goodId 商品id
     */
    @Override
    public Result deleteGoodByID(Long goodId) {
        try {
            Goods good = goodsDao.getGoodById(goodId);
            if (good.getId() == null) {
                return new Result(null, Code.DELETE_ERR, "不存在此商品");
            }
            goodsDao.deleteGood(goodId);
            return new Result(true, null, "删除成功");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置秒杀商品
     *
     * @param goodId 商品id
     * @param time   秒杀时间，单位为分钟
     */
    @Override
    public Result setFlashKillGoodById(Long goodId, Long time) {
        try {
            //获取商品
            Goods good = goodsDao.getGoodById(goodId);
            if (good.getId() == null) {
                return new Result(false, Code.FLASH_KILL_ERR, "不存在此商品");
            }
            if (time == null || time <= 0) {
                return new Result(false, Code.FLASH_KILL_ERR, "请设置正确的时间");
            }
            //pojo->json
            String goodToJson = JSONUtil.toJsonStr(good);
            //加入redis
            stringRedisTemplate.opsForValue().set(RedisConstants.FLASH_KILL_KEY + (good.getId().toString()), goodToJson,
                    time, TimeUnit.MINUTES);
            return new Result(true, null, "设置秒杀成功");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /*
     * 获取所有秒杀商品
     **/
    @Override
    public Result getFlashKillGood() {
        //获取key前缀为秒杀的前缀
        Set<String> keys = stringRedisTemplate.keys(RedisConstants.FLASH_KILL_KEY + "*");
        if (keys.size() == 0) {
            return new Result(null, null, "现在没有秒杀商品");
        }
        //转List
        List<String> goodJsonList = stringRedisTemplate.opsForValue().multiGet(keys);
        List<Goods> goodList = new ArrayList<>();
        for (String goodJson : goodJsonList) {
            //json->pojo
            Goods good = JSONUtil.toBean(goodJson, Goods.class);
            goodList.add(good);
        }
        return new Result(goodList, null, null);
    }

    /**
     * 添加商品到新品区
     *
     * @param good 新品信息
     */
    public void addNewGoodToList(Goods good) {
        String goodToJson = JSONUtil.toJsonStr(good);
        //新品时间为3天
        stringRedisTemplate.opsForValue().set(RedisConstants.NEW_GOOD_KEY + (good.getId().toString()), goodToJson,
                RedisConstants.NEW_GOOD_TTL, TimeUnit.DAYS);
    }

    /*
     * 获取所有秒杀商品
     **/
    @Override
    public Result getNewGood() {
        //获取key前缀为秒杀的前缀
        Set<String> keys = stringRedisTemplate.keys(RedisConstants.NEW_GOOD_KEY + "*");
        if (keys.size() == 0) {
            return new Result(null, null, "现在没有秒杀商品");
        }
        //转List
        List<String> goodJsonList = stringRedisTemplate.opsForValue().multiGet(keys);
        List<Goods> goodList = new ArrayList<>();
        for (String goodJson : goodJsonList) {
            //json->pojo
            Goods good = JSONUtil.toBean(goodJson, Goods.class);
            goodList.add(good);
        }
        return new Result(goodList, null, null);
    }

}
