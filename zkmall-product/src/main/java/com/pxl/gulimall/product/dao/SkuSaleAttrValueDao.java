package com.pxl.zkmall.product.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pxl.zkmall.product.entity.SkuSaleAttrValueEntity;
import com.pxl.zkmall.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author pengxianlong
 * @email pengxianlong@zkmall.com
 * @date 2024-06-09 10:02:30
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVo> getSaleAttrBySpuId(@Param("spuId") Long spuId);

    List<String> getSkuSaleAttrValues(@Param("skuId") Long skuId);
}
