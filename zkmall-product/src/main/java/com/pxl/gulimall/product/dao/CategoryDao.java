package com.pxl.zkmall.product.dao;

import com.pxl.zkmall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author pengxianlong
 * @email pengxianlon@gmail.com
 * @date 2024-05-16 21:22:05
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
