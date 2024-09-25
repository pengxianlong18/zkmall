package com.pxl.zklimall.controller;

import com.alibaba.fastjson.TypeReference;
import com.pxl.common.constant.AuthServerConstant;
import com.pxl.common.exception.BizCodeEnum;
import com.pxl.common.utils.R;
import com.pxl.common.vo.MemberResponseVo;
import com.pxl.zklimall.feign.MemberFeignService;
import com.pxl.zklimall.feign.ThirdPartFeignService;
import com.pxl.zklimall.vo.UserLoginVo;
import com.pxl.zklimall.vo.UserRegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class LoginController {

    @Autowired
    ThirdPartFeignService thirdPartFeignService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    MemberFeignService memberFeignService;



    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null){
            return "login";
        }else {
            return "redirect:http://zkmall.com";
        }
    }

    @GetMapping(value = "/sms/sendCode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {

        //TODO 1、接口防刷

        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)){
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000){
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(),  BizCodeEnum.SMS_CODE_EXCEPTION.getMessage());
            }
        }

        //2.验证码再次校验。reids 存key-phone，value-code
        String code = UUID.randomUUID().toString().substring(0, 6);
        //3、将验证码存入redis
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, code+"_"+System.currentTimeMillis(),10, TimeUnit.MINUTES);

        thirdPartFeignService.sendCode(phone, code.split("_")[0]);
        return R.ok();
    }

    /**
     *
     * TODO: 重定向携带数据：利用session原理，将数据放在session中。
     * TODO:只要跳转到下一个页面取出这个数据以后，session里面的数据就会删掉
     * TODO：分布下session问题
     * RedirectAttributes：重定向也可以保留数据，不会丢失
     * 用户注册
     * @return
     */
    @PostMapping("/register")
    public String regist(@Valid UserRegisterVo vo, BindingResult result ,
                         RedirectAttributes redirectAttributes){

        if (result.hasErrors()){
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(
                    FieldError::getField, FieldError::getDefaultMessage
            ));
//            redirectAttributes.addAttribute("errors",errors);
            redirectAttributes.addFlashAttribute("errors",errors);

            //Request method 'POST' not supported
            return "redirect:http://auth.zkmall.com/reg.html";
        }

        String code = vo.getCode();
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (!StringUtils.isEmpty(redisCode)){
        if (code.equalsIgnoreCase(redisCode.split("_")[0])){
            //删除验证码，令牌机制
            redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());

            // 调用远程微服务，进行注册
            R r = memberFeignService.register(vo);
            if (r.getCode() == 0){

                return "redirect:http://auth.zkmall.com/login.html";
            }else {
                Map<String, String> errors = new HashMap<>();
                errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
                redirectAttributes.addFlashAttribute("errors",errors);
                return  "redirect:http://auth.zkmall.com/reg.html";
            }
        }else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code","验证码错误");

            redirectAttributes.addFlashAttribute("errors",errors);

            //Request method 'POST' not supported
            return "redirect:http://auth.zkmall.com/reg.html";
        }
        }else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code","验证码过期，请重新发送");
            redirectAttributes.addFlashAttribute("errors",errors);
            //Request method 'POST' not supported
            return "redirect:http://auth.zkmall.com/reg.html";
        }
//        return "redirect:/login.html";
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes,
                        HttpSession session){

        R r = memberFeignService.login(vo);
        if (r.getCode() == 0){
            MemberResponseVo data = r.getData("data", new TypeReference<MemberResponseVo>() {
            });
            session.setAttribute(AuthServerConstant.LOGIN_USER,data);
            return "redirect:http://zkmall.com";
        }else {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errors);
            return   "redirect:http://auth.zkmall.com/login.html";
        }
    }
}
