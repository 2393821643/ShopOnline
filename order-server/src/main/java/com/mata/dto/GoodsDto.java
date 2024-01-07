package com.mata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GoodsDto {
    private String name; //商品名称
    private Double price; //价格
    private Integer stock; //库存
    private MultipartFile image; //地址
}
