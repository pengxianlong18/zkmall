package com.pxl.zkmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxl.common.utils.PageUtils;
import com.pxl.zkmall.coupon.entity.CouponSpuRelationEntity;

import java.util.Map;

/**
 * 优惠券与产品关联
 *
 * @author pengxianlong
 * @email pengxianlong@gmail.com
 * @date 2024-05-17 15:35:04
 */
public interface CouponSpuRelationService extends IService<CouponSpuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

