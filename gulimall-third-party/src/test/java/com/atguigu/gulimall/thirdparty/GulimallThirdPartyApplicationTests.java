package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import com.atguigu.gulimall.thirdparty.component.SmsComponent;
import com.atguigu.gulimall.thirdparty.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulimallThirdPartyApplication.class)
public class GulimallThirdPartyApplicationTests {

    @Autowired
    public OSS ossClient;
    @Autowired
    public SmsComponent smsComponent;

    @Test
    public void testSendCode() {
        smsComponent.sendSmsCode("15542387089", "467613");
    }

    @Test
    public void sendSms() {
            String host = "https://dfsns.market.alicloudapi.com";
            String path = "/data/send_sms";
            String method = "POST";
            String appcode = "be6fd7fb1b25435899330f08105b187e";
            Map<String, String> headers = new HashMap<String, String>();
            //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
            headers.put("Authorization", "APPCODE " + appcode);
            //根据API的要求，定义相对应的Content-Type
            headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            Map<String, String> querys = new HashMap<String, String>();
            Map<String, String> bodys = new HashMap<String, String>();
            bodys.put("content", "code:64996");
            bodys.put("template_id", "CST_ptdie100");  //该模板为调试接口专用，短信下发有受限制，调试成功后请联系客服报备专属模板
            bodys.put("phone_number", "15542387089");

            try {
                HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
                System.out.println(response.toString());
                //获取response的body
                //System.out.println(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
        }
    }

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
