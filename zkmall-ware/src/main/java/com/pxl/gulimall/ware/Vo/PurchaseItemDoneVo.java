package com.pxl.zkmall.ware.Vo;

import lombok.Data;

@Data
public class PurchaseItemDoneVo {

    private Long itemId;//采购项id
    private Integer status;//采购项状态
    private String reason;//原因
}
