package com.mata.controller;

import com.mata.dto.Code;
import com.mata.dto.GoodsDto;
import com.mata.dto.Result;
import com.mata.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    //添加商品
    @PostMapping()
    public Result addGoods(GoodsDto goodsDto) {
        return goodsService.addGoods(goodsDto);
    }

    //通过关键词找商品
    @GetMapping("/search/key")
    public Result getGoodByKey(@RequestParam("key") String key, @RequestParam("page") Integer page) {
        return goodsService.getGoodsByKeyword(key,page);
    }

    //通过id找商品
    @GetMapping("/search/id/{id}")
    public Result getGoodByKey(@PathVariable("id") Long id) {
        return goodsService.getGoodById(id);
    }

    //根据id修改商品
    @PutMapping("{goodId}")
    public Result updateGood(@PathVariable("goodId") Long goodId,@RequestBody GoodsDto goodsDto){
        return goodsService.updateGoodById(goodId,goodsDto);
    }

    //删除商品
    @DeleteMapping("{goodId}")
    public Result deleteGood(@PathVariable("goodId") Long goodId){
        return goodsService.deleteGoodByID(goodId);
    }

    //设置秒杀商品
    @PostMapping("/flash-kill/{goodId}/{time}")
    public Result setFlashKillGood(@PathVariable("goodId") Long goodId,@PathVariable("time")Long time){
        return goodsService.setFlashKillGoodById(goodId,time);
    }

    //获取所有秒杀商品
    @GetMapping("/search/flash-kill")
    public Result getFlashKillGood(){
        return goodsService.getFlashKillGood();
    }

    //获取所有新商品
    @GetMapping("/search/new-goods")
    public Result getNewGood(){
        return goodsService.getNewGood();
    }


}
