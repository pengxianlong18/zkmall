package com.pxl.zkmall.zkmallcart.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pxl.common.constant.AuthServerConstant;
import com.pxl.zkmall.zkmallcart.interceptor.CartInterceptor;
import com.pxl.zkmall.zkmallcart.service.CartService;
import com.pxl.zkmall.zkmallcart.vo.Cart;
import com.pxl.zkmall.zkmallcart.vo.CartItem;
import com.pxl.zkmall.zkmallcart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItem> getCurrentUserCartItems() throws ExecutionException, InterruptedException {
        return  cartService.getUserCartItems();
    }

    //删除购物项
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.zkmall.com/cart.html";
    }

    /**
     * 修改数量
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/changeCountItem")
    public String changeCountItem(@RequestParam("skuId") Long skuId,
                                  @RequestParam("num") Integer num){
        cartService.changeCountItem(skuId,num);

        return "redirect:http://cart.zkmall.com/cart.html";
    }

    @GetMapping("/checkItem")
    public String  checkItem(@RequestParam(value = "skuId") Long skuId,@RequestParam("checked") Integer checked){

        cartService.checkItem(skuId,checked);

        return "redirect:http://cart.zkmall.com/cart.html";
    }

    /**
     * 浏览器有一个cookie：user-key 标识用户 ，一个月后过期
     * 第一次使用购物车功能，会给一个临时的用户身份
     * 浏览器以后保存，每次访问都会带上这个cookie
     *
     * 登录 session中有
     * 没登录：按照cookie带来的user-key
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
//        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
//        System.out.println(userInfoTo);

       Cart cart = cartService.getCart();
       model.addAttribute("cart",cart);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes attributes) throws ExecutionException, InterruptedException {

        CartItem cartItem = cartService.addToCart(skuId,num);
        attributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.zkmall.com/addToCartSuccess.html";
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(Model model,@RequestParam("skuId") Long skuId){
        //重定向到成功页码。再次查询购物车数据
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("item",cartItem);
        return "success";
    }
}
