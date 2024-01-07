package com.mata.feign;

import com.mata.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("admin-server")
public interface AdminClient {
    @GetMapping("/admin/after-login")
    Result getUserAfterLogin(@RequestHeader("admin_authorization") String token);
}
