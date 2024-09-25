package com.pxl.zkmall.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

	@Controller
    public class HelloController {

        @Value("${sso.server.url}")
        private String SSOServerUrl;

        /**
		 * 无需认证即可访问
		 *
		 * @return
		 */
        @GetMapping("/hello")
        @ResponseBody
        public String hello() {
            return "hello";
        }

        @GetMapping("/employees")
        public String employees(Model model, HttpSession session, @RequestParam(value = "token", required = false) String token) {
            if (!StringUtils.isEmpty(token)) {
                //TODO：去认证中心查询用户信息
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> forEntity = restTemplate.getForEntity("http://ssoserver.com:8080/userInfo?token=" + token, String.class);
                String body = forEntity.getBody();
                session.setAttribute("loginUser", body);
            }


            Object loginUser = session.getAttribute("loginUser");
            if (loginUser == null) {
                return "redirect:" + SSOServerUrl + "?redirect_url=http://client1.com:8081/employees";
            } else {
                List<Object> emps = new ArrayList<>();
                emps.add("zhangsan");
                emps.add("lisi");
                model.addAttribute("emps", emps);
                return "employees";
            }
        }
    }
