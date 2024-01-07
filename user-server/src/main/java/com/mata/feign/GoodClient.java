package com.mata.feign;

import com.mata.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("goods-server")
public interface GoodClient {
    @GetMapping("/goods/search/id/{id}")
    Result getGoodByKey(@PathVariable("id") Long id);


}
