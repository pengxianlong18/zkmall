package com.pxl.zkmall.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.pxl.zkmall.order.entity.OrderEntity;
import com.pxl.zkmall.order.service.OrderService;
import com.pxl.common.utils.PageUtils;
import com.pxl.common.utils.R;



/**
 * 订单
 *
 * @author pengxianlong
 * @email pengxianlong@gmail.com
 * @date 2024-05-17 15:59:10
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;


    @GetMapping("/status/{orderSn}")
    public  R getOrderStatus(@PathVariable("orderSn") String orderSn ){
        OrderEntity orderEntity = orderService.getOrderStatusByOrderSn(orderSn);
        return   R.ok().setData(orderEntity);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("order:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }

//查询当前用户的所有订单
    @PostMapping("/listWithItem")
    public R listWithItem(@RequestBody Map<String, Object> params){
        PageUtils page = orderService.queryPageWithItem(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("order:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
  //  @RequiresPermissions("order:order:save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("order:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("order:order:delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
