package com.mata.util;

public class RedisConstants {


    //锁的前缀
    public static final String LOCK_GOOD_STOCK_KEY = "lock:good:stock:";

    //锁创建库存缓存的存活时间
    public static final Long LOCK_GOOD_STOCK_TTL = 5L;

    //秒杀商品key的前缀
    public static final String FLASH_KILL_KEY = "flashkill:good:";

    //设置新品的key
    public static final String NEW_GOOD_KEY="new:good:";

    //设置新品的时间
    public static final Long NEW_GOOD_TTL = 3L;



}
