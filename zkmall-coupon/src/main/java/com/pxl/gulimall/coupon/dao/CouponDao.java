package com.pxl.zkmall.coupon.dao;

import com.pxl.zkmall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author pengxianlong
 * @email pengxianlong@gmail.com
 * @date 2024-05-17 15:35:04
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}