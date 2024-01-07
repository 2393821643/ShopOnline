package com.mata.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Integer userId;
    private String userEmail;
    private String userPassword;
    private String username;
    private String userPhone;
    private String userAddress;
    private String userConsignee;



}
