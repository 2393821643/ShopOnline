package com.mata.controller;

import com.mata.dto.GoodsDto;
import com.mata.dto.Result;
import com.mata.feign.GoodClient;
import com.mata.service.ManageGoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage-good")
public class ManageGoodController {

    @Autowired
    private ManageGoodService manageGoodService;

    //添加商品
    @PostMapping()
    public Result addGood(GoodsDto goodsDto){
        return manageGoodService.addGood(goodsDto);
    }

    //根据id修改商品
    @PutMapping("{goodId}")
    public Result updateGood(@PathVariable("goodId") Long goodId,@RequestBody GoodsDto goodsDto){
        return manageGoodService.updateGoodById(goodId,goodsDto);
    }

    //删除商品
    @DeleteMapping("{goodId}")
    public Result deleteGood(@PathVariable("goodId") Long goodId){
        return manageGoodService.deleteGoodByID(goodId);
    }

    //设置秒杀商品
    @PostMapping("/flash-kill/{goodId}/{time}")
    public Result setFlashKillGood(@PathVariable("goodId") Long goodId,@PathVariable("time")Long time){
        return manageGoodService.setFlashKillGood(goodId,time);
    }
}
