package com.mata.feign;

import com.mata.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("email-code-server")
public interface EmailClient {
    @PostMapping("/send-email-code/send-buy/success-code/{goodName}")
    Result sendBuySuccessCode(@RequestHeader("email") String email, @PathVariable("goodName") String goodName);

    @PostMapping("/send-email-code/send-buy/fail-code/{goodName}")
    Result sendBuyFailCode(@RequestHeader("email") String email,@PathVariable("goodName") String goodName);


}
