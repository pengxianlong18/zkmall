package com.pxl.zkmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxl.common.utils.PageUtils;
import com.pxl.zkmall.ware.Vo.FareVo;
import com.pxl.zkmall.ware.entity.WareInfoEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author pengxianlong
 * @email pengxianlong@gmail.com
 * @date 2024-05-18 21:21:24
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    FareVo getFare(Long addId);
}

