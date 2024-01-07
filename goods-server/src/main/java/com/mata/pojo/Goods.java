package com.mata.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mata.dto.GoodsDto;
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


    public Goods(GoodsDto goodsDto) {
        this.name = goodsDto.getName();
        this.price = goodsDto.getPrice();
        this.stock = goodsDto.getStock();
    }
}
