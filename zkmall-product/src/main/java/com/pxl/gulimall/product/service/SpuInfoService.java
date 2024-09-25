package com.pxl.zkmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxl.common.utils.PageUtils;

import com.pxl.zkmall.product.entity.SpuInfoEntity;
import com.pxl.zkmall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author pengxianlong
 * @email pengxianlong@zkmall.com
 * @date 2024-06-09 10:02:30
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo saveVo);

    void saveBsaeInfo(SpuInfoEntity infoEntity);


    PageUtils queryPageByCondition(Map<String, Object> params);

    void spuUp(Long spuId);


    SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

