package com.pxl.zkmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxl.common.utils.PageUtils;
import com.pxl.zkmall.coupon.entity.HomeAdvEntity;

import java.util.Map;

/**
 * 首页轮播广告
 *
 * @author pengxianlong
 * @email pengxianlong@gmail.com
 * @date 2024-05-17 15:35:04
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

