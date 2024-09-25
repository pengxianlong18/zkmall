package com.pxl.zkmall.ware.Vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {

    private MemberAddressVo address;

    private BigDecimal fare;

}
