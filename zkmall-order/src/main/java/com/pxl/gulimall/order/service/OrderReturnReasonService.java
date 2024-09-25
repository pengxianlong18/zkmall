package com.pxl.zkmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxl.common.utils.PageUtils;
import com.pxl.zkmall.order.entity.OrderReturnReasonEntity;

import java.util.Map;

/**
 * 退货原因
 *
 * @author pengxianlong
 * @email pengxianlong@gmail.com
 * @date 2024-05-17 15:59:10
 */
public interface OrderReturnReasonService extends IService<OrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

