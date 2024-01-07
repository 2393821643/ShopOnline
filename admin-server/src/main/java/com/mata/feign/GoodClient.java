package com.mata.feign;

import com.mata.dto.GoodsDto;
import com.mata.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient("goods-server")
public interface GoodClient {
    @PostMapping(value = "/goods",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result addGoods(GoodsDto goodsDto);

    @PutMapping("/goods/{goodId}")
    Result updateGood(@PathVariable("goodId") Long goodId, @RequestBody GoodsDto goodsDto);

    @DeleteMapping("/goods/{goodId}")
    Result deleteGood(@PathVariable("goodId") Long goodId);

    @PostMapping("/goods/flash-kill/{goodId}/{time}")
    public Result setFlashKillGood(@PathVariable("goodId") Long goodId,@PathVariable("time")Long time);
}
