package com.pxl.zkmall.order.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description 订单确认页VO
 */

public class OrderConfirmVo {
    /**
     * 会员收获地址列表
     */
    @Getter
    @Setter
    List<MemberAddressVo> memberAddressVos;

    /**
     * 所有选中的购物项
     */
    @Getter
    @Setter
    List<OrderItemVo> items;

    /**
     * 优惠券（会员积分）
     */
    @Getter
    @Setter
    private Integer integration;

    /**
     * 防止重复提交的令牌
     */
    @Getter
    @Setter
    private String orderToken;

    @Getter @Setter
    Map<Long,Boolean> stocks;


    /**
     * 订单总价
     */
    @Getter
    @Setter
    private BigDecimal total;

    public BigDecimal getTotal() {
        BigDecimal bigDecimal = new BigDecimal("0");
        if (items != null){
            for (OrderItemVo item : items) {
                bigDecimal = bigDecimal.add(item.getPrice().multiply(new BigDecimal(item.getCount())));
            }
        }
        return bigDecimal;
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }

    /**
     * 获取商品总件数
     * @return
     */
    public Integer getCount(){
        Integer i =0;
        if (items != null){
            for (OrderItemVo item : items) {
                i += item.getCount();
            }
        }
        return i;
    }
}
