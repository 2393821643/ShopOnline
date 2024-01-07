package com.mata.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_user")
public class User {
    @TableField(value = "user_id")  //自定义映射
    @TableId(type = IdType.AUTO) //设置id生成策略
    private Integer userId; //用户id
    @TableField(value = "user_email")
    private String userEmail; //用户邮箱
    @TableField(value = "user_password")
    private String userPassword; //用户密码
    @TableField(value = "username")
    private String username; //用户密码

    @TableField(value = "user_phone")
    private String userPhone; //用户电话
    @TableField(value = "user_address")
    private String userAddress; //用户地址
    @TableField(value = "user_consignee")
    private String userConsignee; //收件人名称



}
