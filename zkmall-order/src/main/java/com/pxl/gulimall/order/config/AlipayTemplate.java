package com.pxl.zkmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.pxl.zkmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "9021000139601427";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDTok/o3jqW7ozEtYHEaRsKhUfXMzua++Rjpe1sqt8skJzTf9rTGoaO2wn149u4PE0q+ZvQkVDihXHSNC9MKJpv7gNUoTJp6oXeddk73tem0+MIvWSdjeZOZbAtl1qxmcxbu8+C1aaqrkWyPvJnI0gSQDctDwkfbzY6DymTsoryTR82nf4jblSwl1F71pfDHHZKyimTVWcI7iBP8FEggTe1WHuYdrMoit68b2/tX8TdyiC9YLFuj+hSSAWsgdIuUoOOnC9T/c19T/SA3rQvVlgs0x9TXTIoGHDrOpyJWKuLp1mp1qJ/kLUQMwUMOszHQF/umnZsAHI+auLgy8skMr13AgMBAAECggEAMPIIEy5aVI/lRJVJ5tf8Jgm/HLk/ns+E+brlV02JgfAMJSobvNkUp7Nm8VR6e3iOzFKgQ7NoBougUGI7UhzN4vckFA2X8EEKJvIQhLU8INw+VMYgoAOycQ05msG3ev278rdT/clV24Gkot3GCC5zu6zYVfZ1QKzi2Liq//q6AeGCnSQhJ4UEydH9kWNhpEqaMfh14t5k5wBLTK7GgVTRewD3zCb0HCqz6eHCTSf/UOxGBw6lmaM/RkJZEwZu3CPsZTV/vFyQA38kBAWNJm0Qo1s5RnG4HR+oRvS20708tJMbOiKhF5+e8PETFnAzcHFA7/Cg8QiFQ8j8dtlTLqJ2iQKBgQDpQnAXY8mKkIrt2bnHLp0YAhQ/5G/rxH6r4udzadLsfsvMlgeZM3CLY4UsA6bbeTDURPWFoXbo081z5ynj7dFDQN0tky+TrtjOvApsbO8+ssX8jzzbjmLB28pWktoRfX5cJChKMtN+LjymTJvV2IN1nJY+Qd44+Gu2upAiSHd25QKBgQDoRChBUW8tBKgf0F/zmSwCLH5QUUVi2tHQ//EMU+/ltTdTVEUKiPFzYKf8fLFr5weKMoRTb9nmHez50rqE1R9mJtVnXXRH5ESFF7cXeLm09TtJAqtvF+owDg3CHN4Fqfj1di21LEgXIxbqmuFtqFiZkQo79CEAq5XPI7hSxXFhKwKBgQCv9bxVCKOacZs7/ciVJua+m6Lm584+DoZ3570tZSEF4kie1nZ47ULzA2oMX9zZJzzchACqg7kNvej4i+Mf2+DGWQIARU3YnEdU/KjoTezUYUvVT8Ba57g8AS9Ly4/RLdfKbkQuZNUCFKMP2C6eTKwuZqc347xdQZkPyQC9u+jSuQKBgBTHXC6TwXVukDfgZWUek+BR6K74xlNUriabbw4iSDjtoFh1FoQdwjQHNqAQ67JyhasoX8wzGnRKwxOKdaNCib+Sw86ufqOb2UFq4LdZ1otI0Rf0BDA5HHtWFocCHB63mQ0kmGw33O8s9XThjD9KKzr5zceVTIqJzyor0OaewUjnAoGBAJb3cUns31Y9azgmKLbU4wOQlyYG+Lb87zp9FNANwUdfSjiQa1bRXkeSMVazYOhPiZG5k3d7QmXLHizV4zsl7CDzYg1dnRMA2rWzQRr68YUjNdUDPwPNMbNJYQnsTGDvZu5rwSAI4CDRZ0Ko2JM4HpPtvPSmmOd7NKlDBtckiZ14";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqtJ7hrWoywwjBG0y54TbA6e3RDCqhzWHKHFbuNqSYyEcWI2Gf/7FV5gC5ZIgPtlFaRd93u0QOQaIVOxOARucU0gJM8uyHPmArhMVa7sWRuPZFevU5HAZlUUJTQBxcItTOX5b4brVbdmGe8KOcfkeLzbwsmBdr1o63xDabYt2coXXPOayOWybOy4DKIdy2oXj6GaFQzvO57p245Mbqq/K2tjhAV9h5wg+vaZ+GmUemURotLVlLNZxQcfevLSPUykNSh2sUn3UE+ODq3FpnY1c/RR+6mIO4EbtJwp+o1nQPS2H9TGjk69rWQ2rk37uSFEI0gK6OyWvbYPPIhePUKePHwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url;

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    String timeout = "5m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

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
                + "\"timeout_express\":\""+timeout+"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
//        System.out.println("支付宝的响应："+result);

        return result;

    }
}
