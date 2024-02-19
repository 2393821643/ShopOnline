package com.mata.config;

import com.mata.feign.fallback.UserFallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultFeignConfiguration {
    @Bean
    public UserFallback userClientFallbackFactory(){
        return new UserFallback();
    }
}
