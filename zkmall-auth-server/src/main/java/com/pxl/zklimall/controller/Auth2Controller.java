package com.pxl.zklimall.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.pxl.common.constant.AuthServerConstant;
import com.pxl.common.utils.HttpUtils;
import com.pxl.common.utils.R;
import com.pxl.zklimall.feign.MemberFeignService;
import com.pxl.common.vo.MemberResponseVo;
import com.pxl.zklimall.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理社交登录请求
 */
@Controller
@Slf4j
public class Auth2Controller {

    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/auth2.0/gitee/success")
    public String gitee(@RequestParam("code") String code ,HttpSession session) throws Exception {
        Map<String,String> map = new HashMap();
        map.put("grant_type","authorization_code");
        map.put("client_id","37294bae5c11b5fcf30c669a6c6253904308d51f91088bb8441958fd8bb97b85");
        map.put("redirect_uri","http://auth.zkmall.com/auth2.0/gitee/success");
        map.put("client_secret","de932c81763c4744bcb78f910791d4c6c68a7f635c395a3c203b3943106dbc4c");
        map.put("code",code);
        //1、根据code获取accessToken
        HttpResponse response = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post",
                new HashMap<>(), map, new HashMap<>());
        
        if (response.getStatusLine().getStatusCode() == 200){
            //成功获取到accessToken
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
            //2、根据accessToken获取用户信息
            // 1）、当前用用户是第一登录，则进行注册
            //判断社交用户是登录还是注册
            R r = memberFeignService.oauthlogin(socialUser);
            if (r.getCode() == 0){
                MemberResponseVo data = r.getData("data", new TypeReference<MemberResponseVo>() {
                });
                log.info("登录成功：用户信息：{}",data.toString());

                //1、第一次使用session，命令浏览器保存卡号，JSESSIONID这个cookie
                //以后浏览器访问哪个网站就会带上这个网站的cookie
                //TODO 1、默认发的令牌。当前域（解决子域session共享问题）
                //TODO 2、使用JSON的序列化方式来序列化对象到Redis中
                session.setAttribute(AuthServerConstant.LOGIN_USER,data);
                return "redirect:http://zkmall.com";
            }else {
                //登录失败
                return "redirect:http://auth.zkmall.com/login.html";
            }

        }else {
            return "redirect:http://auth.zkmall.com/login.html";
        }

    }
}
