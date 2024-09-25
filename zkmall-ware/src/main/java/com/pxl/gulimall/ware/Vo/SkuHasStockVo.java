package com.pxl.zkmall.ware.Vo;

import lombok.Data;

@Data
public class SkuHasStockVo {
    private Long skuId;
    private Boolean hasStock; // 是否有库存
}
