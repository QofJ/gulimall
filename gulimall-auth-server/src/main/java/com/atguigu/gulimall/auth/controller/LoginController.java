package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.feign.ThirdPartFeignService;
import com.atguigu.gulimall.auth.vo.UserLoginVo;
import com.atguigu.gulimall.auth.vo.UserRegistVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {
    @Autowired
    private ThirdPartFeignService thirdPartFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MemberFeignService memberFeignService;
    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone){
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                return R.error(BizCodeEnume.VALID_SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.VALID_SMS_CODE_EXCEPTION.getMsg());
            }
        }
        Random random = new Random();
        int code = random.nextInt(1000000);
        String verificationCode = String.format("%06d", code);
        thirdPartFeignService.sendCode(phone, verificationCode);
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, verificationCode + "_" + System.currentTimeMillis(), 10, TimeUnit.MINUTES);
        return R.ok();
    }

    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", errors);
//            return "reg";
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        String code = vo.getCode();
        String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (!StringUtils.isEmpty(s)) {
            if (code.equals(s.split("_")[0])) {
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                R regist = memberFeignService.regist(vo);
                if (regist.getCode() == 0) {
                    return "redirect:http://auth.gulimall.com/login.html";
                } else {
                    HashMap<String, String> errors = new HashMap<>();
                    errors.put("msg", regist.getData("msg", new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            } else {
                HashMap<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        } else {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("code", "验证码过期");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
    }
    @GetMapping("/login.html")
    public String loginPage(HttpSession session) {
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null) {
            return "login";
        } else {
            return "redirect:http://gulimall.com";
        }
    }
    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes,
                        HttpSession session) {
        R login = memberFeignService.login(vo);
        if (login.getCode() == 0) {
            MemberRespVo data = login.getData("data", new TypeReference<MemberRespVo>() {
            });
            session.setAttribute(AuthServerConstant.LOGIN_USER, data);
            return "redirect:http://gulimall.com";
        } else {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("msg", login.getData("msg", new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
    @GetMapping("/index.html")
    public String index() {
        return "login";
    }
}
