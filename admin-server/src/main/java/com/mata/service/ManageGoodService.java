package com.mata.service;

import com.mata.dto.GoodsDto;
import com.mata.dto.Result;

public interface ManageGoodService {
    Result addGood(GoodsDto goodsDto);

    Result updateGoodById(Long goodId, GoodsDto goodsDto);

    Result deleteGoodByID(Long goodId);

    Result setFlashKillGood(Long goodId, Long time);
}
