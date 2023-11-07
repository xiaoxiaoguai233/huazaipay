package org.xxpay.pay.channel.tlpay;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.Assert;
import org.xxpay.core.common.util.MyLog;

public class AllinpayConfig {

    private static final MyLog _log = MyLog.getLog(AllinpayConfig.class);

    public static final String CHANNEL_NAME = "Allinpay";

    public static final String CHANNEL_NAME_WXPAY_QR = CHANNEL_NAME + "_wxpay_qr";
    public static final String CHANNEL_NAME_ALIPAY_QR = CHANNEL_NAME + "_alipay_qr";
    public static final String CHANNEL_NAME_QQPAY_QR = CHANNEL_NAME + "_qqpay_qr";


    public static final String RETURN_VALUE_SUCCESS = "success";
    public static final String RETURN_VALUE_FAIL = "fail";

    // 商户号
    private String CUS_ID;

    // AppID
    private String APP_ID;

    // 商户Key
    private String APP_KEY;

    // 请求地址
    private String reqUrl;

    public AllinpayConfig() {
    }

    public AllinpayConfig(String payParam) {
        _log.info("payParam={}", payParam);
        Assert.notNull(payParam, "init jiujiuliu pay config error");
        JSONObject object = JSONObject.parseObject(payParam);
        this.CUS_ID = object.getString("CUS_ID");
        this.APP_ID = object.getString("APP_ID");
        this.APP_KEY = object.getString("APP_KEY");
        this.reqUrl = object.getString("reqUrl");
    }

    public String getCUS_ID() {
        return CUS_ID;
    }

    public void setCUS_ID(String CUS_ID) {
        this.CUS_ID = CUS_ID;
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


}
