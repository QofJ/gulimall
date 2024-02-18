package com.atguigu.gulimall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserRegistVo {
    @NotBlank(message = "用户名必须提交")
    @Length(min = 6, max = 18, message = "用户名必须是6-18位字符")
    @Pattern(regexp = "^[^\\s].+[^\\s]$", message = "用户名必须不以空格开头或结尾")
    private String userName;
    @NotBlank(message = "密码必须提交")
    @Length(min = 6, max = 18, message = "密码必须是6-18位字符")
    private String password;
    @NotEmpty(message = "手机号必须提交")
    @Pattern(regexp = "^1[3|4|5|7|8]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    @NotBlank(message = "验证码必须填写")
    private String code;
}
