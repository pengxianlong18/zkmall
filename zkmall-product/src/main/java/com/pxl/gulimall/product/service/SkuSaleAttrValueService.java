package com.pxl.zkmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxl.common.utils.PageUtils;

import com.pxl.zkmall.product.entity.SkuSaleAttrValueEntity;
import com.pxl.zkmall.product.vo.SkuItemSaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author pengxianlong
 * @email pengxianlong@zkmall.com
 * @date 2024-06-09 10:02:30
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId);

    List<String> getSkuSaleAttrValues(Long skuId);
}

