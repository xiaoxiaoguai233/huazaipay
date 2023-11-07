package org.xxpay.pay.channel.zhlmobliepay;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.Assert;
import org.xxpay.core.common.util.MyLog;

public class ZhlpayConfig {

    private static final MyLog _log = MyLog.getLog(ZhlpayConfig.class);

    public static final String CHANNEL_NAME = "Zhlpay";

    public static final String CHANNEL_NAME_WXPAY_QR = CHANNEL_NAME + "_wxpay_H5";
    public static final String CHANNEL_NAME_ALIPAY_QR = CHANNEL_NAME + "_alipay_H5";


    public static final String RETURN_VALUE_SUCCESS ="{\"success\": \"true\"}";
    public static final String RETURN_VALUE_FAIL = "{\"success\": \"false\"}";

    // 商户ID
    private String APP_ID;
    // 商户Key
    private String APP_KEY;
    // 请求地址
    private String reqUrl;
    //公钥
    private String publicKey;

    public ZhlpayConfig() {
    }

    public ZhlpayConfig(String payParam) {
        _log.info("payParam={}", payParam);
        Assert.notNull(payParam, "init jiujiuliu pay config error");
        JSONObject object = JSONObject.parseObject(payParam);
        this.APP_ID = object.getString("APP_ID");
        this.APP_KEY = object.getString("APP_KEY");
        this.reqUrl = object.getString("reqUrl");
        this.publicKey = object.getString("PUBLIC_KEY");
    }

    public String getAPP_ID() {
        return APP_ID;
    }

    public void setAPP_ID(String APP_ID) {
        this.APP_ID = APP_ID;
    }

    public String getAPP_KEY() {
        return APP_KEY;
    }

    public void setAPP_KEY(String aPP_KEY) {
        this.APP_KEY = aPP_KEY;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }


}
