package com.pxl.zkmall.product.vo;

import com.pxl.zkmall.product.entity.SkuImagesEntity;
import com.pxl.zkmall.product.entity.SkuInfoEntity;
import com.pxl.zkmall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {

    //1、sku的基本信息 pms_sku_info
    SkuInfoEntity info;

    boolean hasStock = true;
    //2、sku的图片信息 pms_sku_images
    List<SkuImagesEntity> image;

    //3、spu的销售属性信息 pms_sku_sale_attr_value
    List<SkuItemSaleAttrVo> saleAttr;

    //4、spu的介绍
    SpuInfoDescEntity desc;

    //5、获取spu的规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;





}
