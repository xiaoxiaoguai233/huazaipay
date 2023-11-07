package org.xxpay.pay.channel.ssfpay;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.xxpay.core.common.util.MyLog;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: carter
 * @date: 2019-07-31
 * @description: 随手付配置
 */
@Component
public class SsfpayConfig {

    private static final MyLog _log = MyLog.getLog(SsfpayConfig.class);

    public static final String CHANNEL_NAME = "ssf";

    public static final String CHANNEL_NAME_WXPAY_HF = CHANNEL_NAME + "pay_wxpay_hf";//微信话费
    public static final String CHANNEL_NAME_WXPAY_SC = CHANNEL_NAME + "pay_wxpay_SC";//微信扫码

    public static final String RETURN_VALUE_SUCCESS = "ok";
    public static final String RETURN_VALUE_FAIL = "fail";

    // 商户ID
    private String APP_ID;
    // 商户Key
    private String APP_KEY;
    // 请求地址
    private String reqUrl;

    // 商户ID
    private String merchantId;

    public SsfpayConfig(){}

    public SsfpayConfig(String payParam) {
        _log.info("payParam={}", payParam);
        Assert.notNull(payParam, "init ltxpay config error");
        JSONObject object = JSONObject.parseObject(payParam);
        this.APP_ID = object.getString("APP_ID");
        this.APP_KEY = object.getString("APP_KEY");
        this.reqUrl = object.getString("reqUrl");
        this.merchantId = object.getString("merchantId");
    }

    public String getAPP_ID() {
        return APP_ID;
    }

    public void setAPP_ID(String aPP_ID) {
        APP_ID = aPP_ID;
    }

    public String getAPP_KEY() {
        return APP_KEY;
    }

    public void setAPP_KEY(String aPP_KEY) {
        APP_KEY = aPP_KEY;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

}
