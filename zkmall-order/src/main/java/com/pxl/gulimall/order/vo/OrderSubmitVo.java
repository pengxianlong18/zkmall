package com.pxl.zkmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单提交的数据
 */
@Data
public class OrderSubmitVo {
    private Long addrId; //收货地址id
    private Integer payType; //支付类型

    private String orderToken;
    private BigDecimal payPrice;
    /**
     * 订单备注
     */
    private String remarks;
}
