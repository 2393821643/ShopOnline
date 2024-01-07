package com.mata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderMessage {
    private Long orderId;
    private String userEmail;
    private Long goodId;
    private Integer count;
}
