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
@TableName("tb_admin")
public class Admin {

    @TableId(type = IdType.AUTO) //设置id生成策略
    @TableField(value = "admin_id")  //自定义映射
    private Integer adminId;
    @TableField(value = "admin_password")
    private String password;
}
