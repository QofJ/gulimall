package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulimallThirdPartyApplication.class)
public class GulimallThirdPartyApplicationTests {

    @Autowired
    public OSS ossClient;

    @Test
    public void testUpload() throws FileNotFoundException {

        InputStream inputStream = new FileInputStream("E:\\AI\\stable-diffusion-webui\\outputs\\txt2img-images\\2023-06-27\\00011-3229542508.png");
        ossClient.putObject("gulimall-ihep", "aa.png", inputStream);
        ossClient.shutdown();

        System.out.println("上传成功");
    }

    @Test
    public void test() {
//        System.out.println(ossClient.getEndpoint());
    }

}
