package com.pxl.zkmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxl.common.utils.PageUtils;
import com.pxl.zkmall.member.entity.MemberLoginLogEntity;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author pengxianlong
 * @email pengxianlong@gmail.com
 * @date 2024-05-17 15:53:49
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

