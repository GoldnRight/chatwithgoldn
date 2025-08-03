package com.jzy.chatgptdata.test;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class ApiTest {

    // 「沙箱环境」应用ID - 您的APPID，收款账号既是你的APPID对应支付宝账号。获取地址；https://open.alipay.com/develop/sandbox/app
    public static String app_id = "9021000150690829";
    // 「沙箱环境」商户私钥，你的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCshZ0AvcJKiPTd8NBJWUe+1+Pq0FCp2zeLjHMsW51jZ/U2IwjjbMV0joDvkmWjR6ERm/5LvSt3AeBJuOYjCXQ/IdmPnxXUIAzYf11y4Mch0N6rJi8qp6GH6WVNkpl09j03PO93fHR66oU1X/t9m/QFgUbovuicA/xkup3m5nkfIRvjSSQWN/T8jjCVS6uTHHY6y/suTXE5LE7SS5YCG2tW7qWQR3CtRxICBmERrYNsvPb6PIbGx0yadHDqKBKRYuEjdaPCx3S99ERD8DcZILn86VtDzBa+Ch7g/HQ8Ez4IUKDXwEgpJhGFKsd5cF+7xqvM2a023kDwMep7KkPl029/AgMBAAECggEBAIkTgLPeEkn2l9jgRh6BeagBEW65Jd5P98pBRiGwiVU6fHMPUzigbCM4Nuojf+5EM44yS9MR8z8Tlon9B54/HMYstxiOZmGtNYevayijwJT1yHZkWIWju4ypvXvtR1DPdcQ1/m8Jy+L+A+ls4f3js/fLx/OV/Gw6HohKXrvf080OyI2b8Vh66vBU1M+jJVBuhNjQKfgzIeOOu3Jh6R8i6W8tzgVa82dHmII0FbX3qhuwZWls7T/eoYy8BKT3cj3dJ0ZIr0v1E8cje69SyIV24BFnxsrJ4mJVCdy9N+omlCu2DBUlBnux5D4mHt419qwsJ7mrh+uCsVFit6AakMYRfkECgYEA8DwHCS647Ih0bVKzvBr2bmCDhBKk4omgC82XQ7qmXiv4fUG6Uiak/SRhjNOoKOuvEZt866lbiMAIh2mADIuNmtoWZzaERRWDOXhzwxLgIPClxmgec9BMk1nhovnZy8ddHOPJxvYnOjtoWWIVD5h5T9xKyhpdjXYTRoqmXa52vmMCgYEAt9gBaYDm90ARZkL8rMmA+bJ00OWiDsBMq3lKvfW/qG7ZYayLqQknufcHT+zvXhsjNR9qWDrmRYIZE3aHQ/ER22jgLDP769UBmx9smpsoxqFTeEq0N+4O0eFzXbW0hMRXpqpzC6NH/tCSWAuIrRCHHbDTySh8T8tbIA03KW2qdzUCgYBNVXWKXjEHa7HXE31qe1+DDqynknzjtBwCUK92DRrLS+pVIBiDo48PMmv4spX6fkxAJcaj8kPWFMNqG+stnqr6A2Sc2y7fMlqo4H6beHMQB4fGA/RxocdU0+VP2pBjCg2OZEGrO8hK9Z7Uzz8qYxICRE8LGkutYyo3ViDSHXx6MwKBgQCDUzLXCbRzRHnpYcqbJyoRtE/L9W1JOkbKas9gWM7644w3QIlXS2nsZrD7gBYRVDNXPES8F/7sAh4lkBLaDreh0w3npY68jSEjjVQIMPP0x8MXd8268jv14yWE+EqmWJcAQFwEITRJqvYPHNYaQmcyZ2EyHdCHM425nJcd6e/OTQKBgQDAzr26fKpSZHW508WEFtJGHjxrzsvXW/r3IHn4MQJUQ1/Sv1oqMnEhm7XguoHQkA5E2c32NiR0bW2rkyGuA7bB4yksim47GMMIhINZEaewDwnpQJocmU6oF4ChY405H0Xb490G10VnPgL5aSQwmMKCBGfaoKkMf3cTIjiG1uHo0Q==";
    // 「沙箱环境」支付宝公钥
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtp4wqIfnrJo4wPpbW+Pch2PvaQhf4Pngc2eEru1ekKW6YGQZUlDR6TBWtckv3kcxU7Lm3+nR91sK1hHw9Gl0k60kzNqaF43bMe13ZybTuDWazS4v6CLt7OAKfBCFEBOLR/NT9wnhgyVgQ2oZo0Y2whbdS0Iy2wQdgPamj7DtWGtiR20C5S376/KX+JP5NYm3JyOy80i2pZcKr4HsC8xyjIPZJILvWC2/l7ZWO+OjenxhjtIR9bmDz0eYEPd+9/+AtQmpR987kf65+yU3E9ahgzg0IKihjQ4c2OAmDhsMbhK8rmvqRYUkh9oZsr8w9oIlsCQNzAmYbwMLjUa7khjHYQIDAQAB";
    // 「沙箱环境」服务器异步通知回调地址
    public static String notify_url = "http://goldn.natapp1.cc/api/v1/alipay/alipay_notify_url";
    // 「沙箱环境」页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "https://gaga.plus";
    // 「沙箱环境」
    public static String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    // 签名方式
    public static String sign_type = "RSA2";
    // 字符编码格式
    public static String charset = "utf-8";

    private AlipayClient alipayClient;

    @Before
    public void init() {
        this.alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id,
                merchant_private_key,
                "json",
                charset,
                alipay_public_key,
                sign_type);
    }

    @Test
    public void test_aliPay_pageExecute() throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
        request.setNotifyUrl(notify_url);
        request.setReturnUrl(return_url);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "daniel82AAAA001132333361X05");  // 我们自己生成的订单编号
        bizContent.put("total_amount", "0.01"); // 订单的总金额
        bizContent.put("subject", "测试商品");   // 支付的名称
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
        request.setBizContent(bizContent.toString());

        String form = alipayClient.pageExecute(request).getBody();
        log.info("测试结果：{}", form);

        /**
         * 会生成一个form表单；
         * <form name="punchout_form" method="post" action="https://openapi-sandbox.dl.alipaydev.com/gateway.do?charset=utf-8&method=alipay.trade.page.pay&sign=CAAYYDIbvUNRDvY%2B%2BF5vghx2dL9wovodww8CK0%2FferNP1KtyXdytBVLdZKssaFJV%2B8QksVuKlU3qneWhWUuI7atLDgzpussJlJhxTMYQ3GpAfOP4PEBYQFE%2FORemzA2XPjEn88HU7esdJdUxCs602kiFoZO8nMac9iqN6P8deoGWYO4UAwE0RCV65PKeJTcy8mzhOTgkz7V018N9yIL0%2BEBf5iQJaP9tGXM4ODWwFRxJ4l1Egx46FNfjLAMzysy7D14LvTwBi5uDXV4Y%2Bp4VCnkxh3Jhkp%2BDP9SXx6Ay7QaoerxHA09kwYyLQrZ%2FdMZgoQ%2BxSEOgklIZtYj%2FLbfx1A%3D%3D&return_url=https%3A%2F%2Fgaga.plus&notify_url=http%3A%2F%2Fngrok.sscai.club%2Falipay%2FaliPayNotify_url&version=1.0&app_id=9021000132689924&sign_type=RSA2&timestamp=2023-12-13+11%3A36%3A29&alipay_sdk=alipay-sdk-java-4.38.157.ALL&format=json">
         * <input type="hidden" name="biz_content" value="{&quot;out_trade_no&quot;:&quot;100001001&quot;,&quot;total_amount&quot;:&quot;1.00&quot;,&quot;subject&quot;:&quot;测试&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}">
         * <input type="submit" value="立即支付" style="display:none" >
         * </form>
         * <script>document.forms[0].submit();</script>
         */
    }

    /**
     * 查询订单
     */
    @Test
    public void test_alipay_certificateExecute() throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
        bizModel.setOutTradeNo("daniel82AAAA000032333361X03");
        request.setBizModel(bizModel);

        String body = alipayClient.certificateExecute(request).getBody();
        log.info("测试结果：{}", body);
    }

    /**
     * 退款接口
     */
    @Test
    public void test_alipay_refund() throws AlipayApiException {
        AlipayTradeRefundRequest request =new AlipayTradeRefundRequest();
        AlipayTradeRefundModel refundModel =new AlipayTradeRefundModel();
        refundModel.setOutTradeNo("daniel82AAAA000032333361X03");
        refundModel.setRefundAmount("1.00");
        refundModel.setRefundReason("退款说明");
        request.setBizModel(refundModel);

        AlipayTradeRefundResponse execute = alipayClient.execute(request);
        log.info("测试结果：{}", execute.isSuccess());
    }

}
