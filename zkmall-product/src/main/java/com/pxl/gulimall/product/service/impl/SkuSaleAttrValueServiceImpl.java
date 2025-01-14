package com.pxl.zkmall.product.service.impl;

import com.pxl.zkmall.product.entity.SkuSaleAttrValueEntity;
import com.pxl.zkmall.product.vo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pxl.common.utils.PageUtils;
import com.pxl.common.utils.Query;

import com.pxl.zkmall.product.dao.SkuSaleAttrValueDao;

import com.pxl.zkmall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId) {
        List<SkuItemSaleAttrVo> saleAttrVos = this.baseMapper.getSaleAttrBySpuId(spuId);
        return saleAttrVos;
    }

    @Override
    public List<String> getSkuSaleAttrValues(Long skuId) {
       return this.baseMapper.getSkuSaleAttrValues(skuId);
    }

}