package com.pxl.zkmall.thirdparty.controller;

import com.pxl.common.utils.R;
import com.pxl.zkmall.thirdparty.component.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsSendController {
    @Autowired
    SMSUtils smsUtils;

    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone , @RequestParam("code") String code){
        smsUtils.sendCode(phone,code);
        return R.ok();
    }
}
