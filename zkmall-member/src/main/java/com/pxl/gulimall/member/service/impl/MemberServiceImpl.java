package com.pxl.zkmall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pxl.common.utils.HttpUtils;
import com.pxl.zkmall.member.dao.MemberLevelDao;
import com.pxl.zkmall.member.entity.MemberLevelEntity;
import com.pxl.zkmall.member.exception.PhoneExistException;
import com.pxl.zkmall.member.exception.UserNameExistException;
import com.pxl.zkmall.member.vo.MemberLoginVo;
import com.pxl.zkmall.member.vo.MemberRegisterVo;
import com.pxl.zkmall.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pxl.common.utils.PageUtils;
import com.pxl.common.utils.Query;

import com.pxl.zkmall.member.dao.MemberDao;
import com.pxl.zkmall.member.entity.MemberEntity;
import com.pxl.zkmall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo vo) {
        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = new MemberEntity();

        //设置会员等级
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(levelEntity.getId());

        //检测用户名和手机号唯一性
        checkUsernameUnique(vo.getUserName());
        checkPhoneUnique(vo.getPhone());


        //设置手机号
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUserName());

        //设置密码 密码进行加密存储
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
        memberEntity.setNickname(vo.getUserName());

        // 其他默认信息
        memberEntity.setCreateTime(new Date());


        memberDao.insert(memberEntity);

    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException{
        MemberDao memberDao = this.baseMapper;
        Long count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0){
            throw new PhoneExistException();
        }

    }

    @Override
    public void checkUsernameUnique(String username) throws UserNameExistException{
        MemberDao memberDao = this.baseMapper;
        Long count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0){
            throw  new UserNameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        MemberDao memberDao = this.baseMapper;
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct)
                .or().eq("mobile", loginacct));
        if (memberEntity == null){
            return  null;
        }else {
            String passwordDB = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(password, passwordDB);
            if (matches){
                return memberEntity;
            }else {
                return null;
            }
        }
    }

    @Override
    public MemberEntity login(SocialUser vo) throws Exception {
        MemberDao memberDao = this.baseMapper;
        //通过https://gitee.com/api/v5/user?access_token获取用户信息
        Map<String,String> map = new HashMap<>();
        String name ="";
        map.put("access_token",vo.getAccess_token());
        HttpResponse response = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get", new HashMap<>(), map);
        if (response.getStatusLine().getStatusCode() == 200){
            String s = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSON.parseObject(s);
            String id = jsonObject.getString("id");
            name = jsonObject.getString("name");
            vo.setSocialUid(id);
        }
        MemberEntity entity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", vo.getSocialUid()));
        if (entity != null){
            //用户已注册
            //Access_token存在过期时间，所以每一次登录需要更新Access_token
            MemberEntity update = new MemberEntity();
            update.setId(entity.getId());
            update.setAccessToken(vo.getAccess_token());
            update.setExpiresIn(vo.getExpires_in());
            memberDao.updateById(update);

            entity.setAccessToken(vo.getAccess_token());
            entity.setExpiresIn(vo.getExpires_in());
            return entity;

        }else {
            //该用户没有注册
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setSocialUid(vo.getSocialUid());
            memberEntity.setAccessToken(vo.getAccess_token());
            memberEntity.setExpiresIn(vo.getExpires_in());
            memberEntity.setNickname(name);
            memberEntity.setCreateTime(new Date());
            memberDao.insert(memberEntity);
            return memberEntity;
        }
    }

}