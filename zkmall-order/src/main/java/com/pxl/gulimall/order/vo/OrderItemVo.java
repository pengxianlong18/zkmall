package com.pxl.zkmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description
 */
@Data
public class OrderItemVo {

    private Long skuId;

    /**
     * 是否被选中
     */
    private Boolean check;

    private String title;
    private String skuName;

    /**
     * 默认图片
     */
    private String image;

    /**
     * 商品套餐属性
     */
    private List<String> skuAttrValues;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 商品数量
     */
    private Integer count;

    /**
     * 总价
     */
    private BigDecimal totalPrice;
    private BigDecimal weigh;

}
