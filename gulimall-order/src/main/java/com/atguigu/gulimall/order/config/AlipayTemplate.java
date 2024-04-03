package com.atguigu.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "9021000135674993";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCQElUiY2DLYHU4TQLSVwQ36R3OReYUpyeO5ATc4pPYW9eAJFEV1ux7zMEV7kil1FB7kgAwFtVoLt+KAEJlii0lW4Con6jU7jwAhnVN6TzIFO28Tg3hS6KeUjNoYawvNSfUe/MEaNlSwfGxwgMW0yq9OJ4U1eT7MW8nTJB46BU3Ua1fEQ/QIbitUCFp2wu/a8Ik4CiNOYiSAPIAl0PJIYJXKlrsEHcZTqrnOrGSP3THMDIBiH7Y/kbZY9yiT85PDhAu1hb3EQp+AziZ6teHoBFfcO1+0DwgFk0je6fkrmBv9w1kJrCofeokUI2I9gkfKtArPSCUlnLs1gExsQr+uQvhAgMBAAECggEAQ/fCHjUQByJ8apE6UY0nulDJ9PRY9UleijTcbnCeGyZI5nU7o7f/XPBd70C5GTKuvX1FhWpgR4RFhU9WA51KayNd8S9RcRLrT7wfPyBXwnTNK1cPiUgrcdGJ2yapGqRTJBVNsrjcPgStR5Ul6/C3awl42o+GNVM/AyVFyySCTdQO8Nkc1qQb87hl3l3m/fXa4gxTzXBdHOFAJKY1YNMptvSwHUuwPti9eV/LFJrMs91q62iE+S+SU73p9VQG6wjl0fRW1MFBlHO69Ve5g+d5dE+XAGeVpbzy0PUfrPmla1tYXwgRaaztp2+xscptqkpeqYaAXF/q3DL6Ak4RK+M1GQKBgQDOUZeXr47RIQ08brNSu4ytZMhDaaSjokcHZW/hSfQ6BJwAmFNkVjXzJvsUzgPK9ttj7t9PfCG2HpkFCHwJr0/Dj/srhoJh4T32F9DTqHG9FjjN6jbovpC+GyVo/e0SBCg7F9XWL9f+L94vDuuj7Hch3uAVgeOEw0ma/7795+9zpwKBgQCyw4zuBPoml8z5fhRzx3hnCZfFqRYfmRTnoEjV6TSlr7gKuUCpjWrrKypcudnZe/2Tyqm2NnRf0TqkCh7IUxsC436oZWDq1oVPaoO+QcVHQU++VzCZqrpuFD513OyS/Mzi3xl07Yq4IsUxRboND2VT8rtA72yKVosKILj5LzGVNwKBgDrXdFeTfzTPq1m37LbEvItbOe5X+9ebWYyXdoAXOmstk1xsFSSBneNaE7PMSbKiEtH+d3vQ2EB7m4Ke99k/QY1xy92PADJwLd64zhr/aGjYJJ09lils6BT/4vx9pxZDT0IDOz5Z6M0a09Ax/iyDLTrguKIJqFzd4WkuVUdO1bDzAoGANEXyg/d4nqhtC9fOazy8/IRVmTskeQDul/zKz6Jt/rUvgBND0VDFHxd0P8Eo7uvO7tSoY9vwk8Q+/ZDoB3QlxeZ4Nw3OFV29hceD1rKs2XVDuEkOsSC3e1D4IscDdsftsbnDW+nKUqX4O7xD92W1H6MvFoHsn1dHOSLwdA1U1mkCgYBV8ZyMyEcduQVdixmqFlmmW0XbUuByVC4cuwsI7gdA2ncXHrlmiydJvcIqiNGNkJfC0ViIbBD3YS+pA+kosDZP4AYbxMudA6zScV3DkCs2h9Z7V7NY5L0631ukBJipVXPl3ssvdnvk5jh4OKI/UQGVNY08wwYTbMScD0I+MXRKhg==";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi7qsulBlDo6AFmbOYaMbiWMNY9XM+VRpykbe0NGo3FvinijCjVb+kHcBaj53s6eNuBls8DCm58DZQ0oX6lBJvvqJ22fQhs5yxdBjcCp2kUhJIuzuNsm8QUCjl0zQNPxp6EKQcPOoXqU144cm8ugCcGQdMy3GcDhz+adWKR+VsIGRUyoHNpbT8Fhn2lAS0hWY1RocdXcBH3D8w9n1gIkEVI4bIt0pVZNSiAzw1ebpsfUXm+F3toiv/2d4fmmE1SE7Pr6Va52e++eldMIda34AoNWQbVlekT60B0raZEd1U3bOswHdNbZ7DqzUZp/F8gGcBD8V6aol/0fH/ItgUzCB3wIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://39.101.76.75:8004/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = "http://member.gulimall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    private  String timeout = "30m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+ timeout+ "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
