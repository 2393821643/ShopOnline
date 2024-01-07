package com.mata.feign;

import com.mata.dto.Result;
//import com.mata.feign.fallback.UserFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

//@FeignClient(name = "user-server",fallback = UserFallback.class)
@FeignClient(name = "user-server")
public interface UserClient {
    @GetMapping("/user/after-login")
    Result getUserAfterLogin(@RequestHeader("authorization") String token);
}
