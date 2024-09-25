package com.pxl.zkmall.member.dao;

import com.pxl.zkmall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author pengxianlong
 * @email pengxianlong@gmail.com
 * @date 2024-05-17 15:53:49
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}