package com.pxl.zkmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxl.common.utils.PageUtils;
import com.pxl.zkmall.product.entity.AttrEntity;
import com.pxl.zkmall.product.vo.AttrRespVO;
import com.pxl.zkmall.product.vo.AttrVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author pengxianlong
 * @email pengxianlon@gmail.com
 * @date 2024-05-16 21:22:05
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVO attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType);


    AttrRespVO getAttrInfo(Long attrId);

    void updateAttr(AttrVO attr);

    List<AttrEntity> getRelation(Long attrgroupId);

    PageUtils getNoRelation(Map<String, Object> params, Long attrgroupId);

    /**
     * 在指定的所有属性集合里面，跳出检
     * @param attrIds
     * @return
     */
    List<Long> selectSearchAttrs(List<Long> attrIds);
}

