package com.mata.feign.fallback;

import com.mata.dto.Result;
import com.mata.feign.UserClient;
import com.mata.pojo.User;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class UserFallback implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable throwable) {
        return new UserClient() {
            @Override
            public Result getUserAfterLogin(String token) {
                log.info("获取失败xxxxx");
                return new Result("test",null,null);
            }
        };
    }
}
