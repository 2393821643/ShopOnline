package com.mata.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mata.pojo.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDao extends BaseMapper<Order> {
}
