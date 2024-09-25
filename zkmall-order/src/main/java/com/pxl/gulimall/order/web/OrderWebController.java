package com.pxl.zkmall.order.web;

import com.pxl.zkmall.order.service.OrderService;
import com.pxl.zkmall.order.vo.OrderConfirmVo;
import com.pxl.zkmall.order.vo.OrderSubmitVo;
import com.pxl.zkmall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;



    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {

       OrderConfirmVo confirmVo = orderService.confirmOrder();
       model.addAttribute("orderConfirmData",confirmVo);
        return "confirm";
    }

    /**
     * 下单功能
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo,Model model, RedirectAttributes attributes){

        SubmitOrderResponseVo responseVo = orderService.sumbitOrder(vo);
        if (responseVo.getCode() == 0) {
         //成功 去支付选项页
            model.addAttribute("submitOrder",responseVo);
            return "pay";
        }else {
            String msg = "下单失败";
            switch (responseVo.getCode()) {
                case 1: msg += "令牌订单信息过期，请刷新再次提交"; break;
                case 2: msg += "订单商品价格发生变化，请确认后再次提交"; break;
                case 3: msg += "库存锁定失败，商品库存不足"; break;
            }
            attributes.addFlashAttribute("msg",msg);

            return "redirect:http://order.zkmall.com/toTrade";
        }
    }
}
