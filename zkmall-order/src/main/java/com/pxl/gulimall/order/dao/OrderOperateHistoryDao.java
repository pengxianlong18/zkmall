package com.pxl.zkmall.order.dao;

import com.pxl.zkmall.order.entity.OrderOperateHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单操作历史记录
 * 
 * @author pengxianlong
 * @email pengxianlong@gmail.com
 * @date 2024-05-17 15:59:10
 */
@Mapper
public interface OrderOperateHistoryDao extends BaseMapper<OrderOperateHistoryEntity> {
	
}
