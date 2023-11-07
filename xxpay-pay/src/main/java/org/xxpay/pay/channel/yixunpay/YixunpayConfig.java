package org.xxpay.pay.channel.yixunpay;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.xxpay.core.common.util.MyLog;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: carter
 * @date: 2019-07-31
 * @description: 易迅配置
 */
@Component
public class YixunpayConfig {

    private static final MyLog _log = MyLog.getLog(YixunpayConfig.class);

    public static final String CHANNEL_NAME = "yixun";

    public static final String CHANNEL_NAME_YINLIAN_KJ = CHANNEL_NAME + "pay_kuaijie";//银联快捷

    public static final String VERSION = "0.0.1";
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

    public YixunpayConfig(){}

    public YixunpayConfig(String payParam) {
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
