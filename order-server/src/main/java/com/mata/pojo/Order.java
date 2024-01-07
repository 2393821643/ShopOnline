package com.mata.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_order")
public class Order {
    @TableField(value = "order_id")  //自定义映射
    @TableId(type = IdType.NONE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;  //订单号
    @TableField(value = "user_id")
    private Integer UserId; //用户id
    @TableField(value = "good_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long goodId; //商品id
    @TableField(value = "good_count")
    private Integer goodCount; //商品数量
    @TableField(value = "good_name")
    private String goodName;
    @TableField(value = "address")
    private String address; //地址
    @TableField(value = "user_phone")
    private String userPhone; //用户电话
    @TableField(value = "user_consignee")
    private String userConsignee; //收件人名称
    @TableField(value = "state")
    private Integer state; //订单状态（是否收货）
    @TableField(value = "img")
    private String img; //图片位置
    @TableField(value = "price") // 价格
    private Double price;
}
