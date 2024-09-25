package com.pxl.zkmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxl.common.utils.PageUtils;
import com.pxl.zkmall.coupon.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * 专题商品
 *
 * @author pengxianlong
 * @email pengxianlong@gmail.com
 * @date 2024-05-17 15:35:04
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

