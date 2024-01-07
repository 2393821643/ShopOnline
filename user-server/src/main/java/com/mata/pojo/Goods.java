package com.mata.pojo;


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
public class Goods {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id; //id
    private String name; //商品名称
    private Double price; //价格
    private Integer stock; //库存

    private String img; //地址

}
