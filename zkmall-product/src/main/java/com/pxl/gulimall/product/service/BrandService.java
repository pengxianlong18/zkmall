package com.pxl.zkmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxl.common.utils.PageUtils;
import com.pxl.zkmall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author pengxianlong
 * @email pengxianlon@gmail.com
 * @date 2024-05-16 21:22:05
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);
}

