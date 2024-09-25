package com.pxl.zkmall.zkmallcart.service;

import com.pxl.zkmall.zkmallcart.vo.Cart;
import com.pxl.zkmall.zkmallcart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {
    /**
     * 将商品添加到购物车
     * @param skuId
     * @param num
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取购物项
     * @param skuId
     * @return
     */
    CartItem getCartItem(Long skuId);

    /**
     * 获取购物车
     * @return
     */
    Cart getCart() throws ExecutionException, InterruptedException;
    void clearCart(String cartKey);

    /**
     * 修改购物项选中状态
     * @param skuId
     * @param checked
     */
    void checkItem(Long skuId, Integer checked);

    void changeCountItem(Long skuId, Integer num);

    void deleteItem(Long skuId);

    List<CartItem> getUserCartItems();

}
