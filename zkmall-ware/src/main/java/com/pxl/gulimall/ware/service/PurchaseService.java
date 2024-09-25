package com.pxl.zkmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxl.common.utils.PageUtils;
import com.pxl.zkmall.ware.Vo.MergeVo;
import com.pxl.zkmall.ware.Vo.PurchaseDoneVo;
import com.pxl.zkmall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author pengxianlong
 * @email pengxianlong@gmail.com
 * @date 2024-05-18 21:21:24
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void received(List<Long> ids);

    void done(PurchaseDoneVo doneVo);
}

