package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.auth.vo.SocialUser;
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
 *
 */
@Slf4j
@Controller
public class OAuth2Controller {
    @Autowired
    private MemberFeignService memberFeignService;
    @GetMapping("/oauth2.0/gitee/success")
    public String gitee(@RequestParam("code") String code, HttpSession session) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "authorization_code");
        map.put("client_id", "de6c2d0e00ed6477d4c7b8ede1e7f942862fc2adadb0f6403b6f3fa1acd7f664");
        map.put("client_secret", "40c68b42f2f1e88fead9ad5af301b988df5f2770ee148bd7bcad3dd6cc9c1917");
        map.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/gitee/success");
        map.put("code", code);
        HttpResponse response = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", new HashMap<>(), null, map);
        if (response.getStatusLine().getStatusCode()==200) {
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
            HttpResponse responseUser = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get", new HashMap<>(), new HashMap<String, String>(){{
                put("access_token", socialUser.getAccess_token());
            }});
            if (responseUser.getStatusLine().getStatusCode()==200) {
                String jsonUser = EntityUtils.toString(responseUser.getEntity());
                socialUser.setUid(JSON.parseObject(jsonUser).getString("id"));
            }
            R oauthlogin = memberFeignService.oauthlogin(socialUser);
            if (oauthlogin.getCode() == 0) {
                MemberRespVo data = oauthlogin.getData("data", new TypeReference<MemberRespVo>() {
                });
                log.info("登录成功：用户信息：{}", data.toString());
                session.setAttribute(AuthServerConstant.LOGIN_USER, data);
                return "redirect:http://gulimall.com";
            } else {
                return "redirect:http://auth.gulimall.com/login.html";
            }

        } else {
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
