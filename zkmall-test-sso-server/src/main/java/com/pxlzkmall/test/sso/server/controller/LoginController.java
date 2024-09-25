package com.pxlzkmall.test.sso.server.controller;

import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("/userInfo")
    @ResponseBody
    public String userInfo(@RequestParam("token") String token){
        return redisTemplate.opsForValue().get(token);
    }

    @GetMapping("/login.html")
    public String login(@RequestParam("redirect_url") String url, Model model,
                        @CookieValue(value = "sso_token",required = false) String sso_token) {

        if (!StringUtils.isEmpty(sso_token)){
            //说明已经登陆过了
            return "redirect:" + url + "?token=" + sso_token;
        }

        model.addAttribute("url", url);
        return "login";
    }


    /**
     * 处理登录请求
     * @return
     */
    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("redirect_url") String url,
                          HttpServletResponse response) {

        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(uuid, username);
            Cookie sso_token = new Cookie("sso_token",uuid);
            response.addCookie(sso_token);
            return "redirect:" + url + "?token=" + uuid;
        }
        return "login";
    }

}
