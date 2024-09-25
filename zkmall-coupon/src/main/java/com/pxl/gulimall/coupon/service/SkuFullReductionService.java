package com.pxl.zkmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxl.common.to.SkuReductionTo;
import com.pxl.common.utils.PageUtils;
import com.pxl.zkmall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author pengxianlong
 * @email pengxianlong@gmail.com
 * @date 2024-05-17 15:35:04
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveReductionTo(SkuReductionTo reductionTo);
}

