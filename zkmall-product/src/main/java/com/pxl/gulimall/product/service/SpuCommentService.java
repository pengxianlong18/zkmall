package com.pxl.zkmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxl.common.utils.PageUtils;
import com.pxl.zkmall.product.entity.SpuCommentEntity;


import java.util.Map;

/**
 * 商品评价
 *
 * @author pengxianlong
 * @email pengxianlong@zkmall.com
 * @date 2024-06-09 10:02:30
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

