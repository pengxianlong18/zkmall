package com.pxl.zkmall.product.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pxl.zkmall.product.entity.SpuCommentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价
 * 
 * @author pengxianlong
 * @email pengxianlong@zkmall.com
 * @date 2024-06-09 10:02:30
 */
@Mapper
public interface SpuCommentDao extends BaseMapper<SpuCommentEntity> {
	
}
