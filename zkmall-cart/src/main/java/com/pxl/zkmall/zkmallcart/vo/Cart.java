package com.pxl.zkmall.zkmallcart.vo;

import java.math.BigDecimal;
import java.util.List;

public class Cart {

    List<CartItem> items;

    private Integer countNum; //商品数量
    private Integer countType; //商品种类
    private BigDecimal totalAmount; //商品总价
    private BigDecimal reduce = new BigDecimal("0"); //减免价格

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size() > 0){
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        return count;
    }



    public Integer getCountType() {
        int count = 0;
        if (items != null && items.size() > 0){
            for (CartItem item : items) {
                count += 1;
            }
        }
        return count;
    }



    public BigDecimal getTotalAmount() {

        BigDecimal totalAmount = new BigDecimal("0");
        // 1、计算总价
        if (items != null && items.size() > 0){
            for (CartItem item : items) {
                if (item.getChecked()){
                    totalAmount = totalAmount.add(item.getTotalPrice());
                }
            }
        }
        // 2、减去优惠
        totalAmount = totalAmount.subtract(this.getReduce());
        return totalAmount;
    }



    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
