package com.pxl.zklimall.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserRegisterVo {

    /**
     * 用户名
     */
    @NotEmpty(message = "用户名必须填写")
    @Length(min = 2, max = 10, message = "用户名长度必须在2到10之间")
    private String userName;

    /**
     * 密码
     */
    @NotEmpty(message = "密码必须填写")
    @Length(min = 6, max = 18, message = "用户名长度必须在2到10之间")
    private String password;

    /**
     * 手机号
     */
    @NotEmpty(message = "手机号必须填写")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 短信验证码
     */
    @NotEmpty(message = "验证码必须填写")
    private String code;
}
