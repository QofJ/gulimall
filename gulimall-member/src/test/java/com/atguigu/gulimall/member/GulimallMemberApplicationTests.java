package com.atguigu.gulimall.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class GulimallMemberApplicationTests {

    @Test
    public void contextLoads() {
        String s = DigestUtils.sha256Hex("123456_");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456_");
        System.out.println(encode);
    }

}
