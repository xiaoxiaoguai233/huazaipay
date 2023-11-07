package org.xxpay.pay.channel.pddpay;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.xxpay.core.common.util.MyLog;

/**
 * @author: carter
 * @date: 2019-07-31
 * @description: 随手付配置
 */
@Component
public class PinDuoDuoPayConfig {

    private static final MyLog _log = MyLog.getLog(PinDuoDuoPayConfig.class);

    public static final String CHANNEL_NAME = "PinDuoDuo";

    public static final String CHANNEL_NAME_UNIFORM_QR = CHANNEL_NAME + "_pay_uniform_qr";
    public static final String CHANNEL_NAME_UNIFORM_SC = CHANNEL_NAME + "_pay_uniform_sc";

    public static final String RETURN_VALUE_SUCCESS = "success";
    public static final String RETURN_VALUE_FAIL = "fail";

    // appID
    private String APP_ID;
    // 商户Key
    private String APP_KEY;
    // 请求地址
    private String reqUrl;
    // 商户ID
    private String merchantId;
    //用户ID
    private String out_uid;
    //支付失败时，或支付超时后网页自动跳转地址
    private String error_url;
    //支付成功后网页自动跳转地址
    private String success_url;
    //接口版本号
    private String version;


    public PinDuoDuoPayConfig(){}

    public PinDuoDuoPayConfig(String payParam) {
        _log.info("payParam={}", payParam);
        Assert.notNull(payParam, "init ltxpay config error");
        JSONObject object = JSONObject.parseObject(payParam);
        this.APP_ID = object.getString("APP_ID");
        this.APP_KEY = object.getString("APP_KEY");
        this.reqUrl = object.getString("reqUrl");
        this.merchantId = object.getString("merchantId");
        this.out_uid = object.getString("out_uid");
        this.error_url = object.getString("error_url");
        this.success_url = object.getString("success_url");
        this.version = object.getString("version");
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

    public void setMerchantId(String out_uid) {
        this.merchantId = out_uid;
    }

    public String getOUT_UID() {
        return out_uid;
    }

    public void setOUT_UID(String out_uid) {
        this.out_uid = out_uid;
    }

    public String getError_url() {
        return error_url;
    }

    public void setError_url(String error_url) {
        this.error_url = error_url;
    }

    public String getSuccess_url() {
        return success_url;
    }

    public void setSuccess_url(String success_url) {
        this.success_url = success_url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
