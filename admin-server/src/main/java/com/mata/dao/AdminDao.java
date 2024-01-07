package com.mata.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mata.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminDao extends BaseMapper<Admin> {
}
