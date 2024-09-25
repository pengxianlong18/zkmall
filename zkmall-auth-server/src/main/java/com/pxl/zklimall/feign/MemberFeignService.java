package com.pxl.zklimall.feign;

import com.pxl.common.utils.R;
import com.pxl.zklimall.vo.SocialUser;
import com.pxl.zklimall.vo.UserLoginVo;
import com.pxl.zklimall.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("zkmall-member")
public interface MemberFeignService {
    @PostMapping("/member/member/register")
    public R register(@RequestBody UserRegisterVo vo);

    @PostMapping("/member/member/login")
    public R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth/login")
    public R oauthlogin(@RequestBody SocialUser vo)throws Exception;
}
