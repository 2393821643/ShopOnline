package com.mata.service;

import com.mata.dto.Result;

public interface AdminService {
    Result loginByPassword(String accounts, String password);

    Result logOut(String token);
}
