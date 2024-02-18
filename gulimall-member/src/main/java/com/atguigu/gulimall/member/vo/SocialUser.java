package com.atguigu.gulimall.member.vo;

import lombok.Data;

@Data
public class SocialUser {
    private String access_token;
    private String token_type;
    private long expires_in;
    private String refresh_token;
    private String scope;
    private String created_at;
    private String uid;
}
