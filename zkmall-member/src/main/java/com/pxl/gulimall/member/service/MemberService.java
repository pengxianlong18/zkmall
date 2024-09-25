package com.pxl.zkmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pxl.common.utils.PageUtils;
import com.pxl.zkmall.member.entity.MemberEntity;
import com.pxl.zkmall.member.exception.PhoneExistException;
import com.pxl.zkmall.member.exception.UserNameExistException;
import com.pxl.zkmall.member.vo.MemberLoginVo;
import com.pxl.zkmall.member.vo.MemberRegisterVo;
import com.pxl.zkmall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author pengxianlong
 * @email pengxianlong@gmail.com
 * @date 2024-05-17 15:53:49
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo vo);

    void checkPhoneUnique(String phone) throws PhoneExistException;
    void checkUsernameUnique(String username) throws UserNameExistException;

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser vo) throws Exception;
}

