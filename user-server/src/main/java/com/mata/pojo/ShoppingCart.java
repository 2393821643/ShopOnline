package com.mata.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "shopping_cart")
public class ShoppingCart {
    @Id
    private Integer id;

    @Field("goodList")
    private List<Goods> goodsList;
}
