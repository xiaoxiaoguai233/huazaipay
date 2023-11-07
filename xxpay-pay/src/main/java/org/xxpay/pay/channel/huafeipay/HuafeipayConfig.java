package org.xxpay.pay.channel.huafeipay;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.Assert;
import org.xxpay.core.common.util.MyLog;

public class HuafeipayConfig {

    private static final MyLog _log = MyLog.getLog(HuafeipayConfig.class);

    public static final String CHANNEL_NAME = "Huafeipay";

    public static final String CHANNEL_NAME_WXPAY_QR = CHANNEL_NAME + "_wxpay_qr";
    public static final String CHANNEL_NAME_ALIPAY_QR = CHANNEL_NAME + "_alipay_qr";
    public static final String CHANNEL_NAME_WXPAY_H5 = CHANNEL_NAME + "_wxpay_H5";
    public static final String CHANNEL_NAME_ALIPAY_H5 = CHANNEL_NAME + "_alipay_H5";

    public static final String RETURN_VALUE_SUCCESS = "SUCCESS";
    public static final String RETURN_VALUE_FAIL = "fail";

    public static final String PAY_PRODUCT_TYPE = "2";

    // 主商户ID
    private String APP_ID;

    // 子商户ID
    private String APP_CHILD_ID;

    // 商户Key
    private String APP_KEY;

    // 请求地址
    private String reqUrl;

    public HuafeipayConfig() {
    }

    public HuafeipayConfig(String payParam) {
        _log.info("payParam={}", payParam);
        Assert.notNull(payParam, "init huafei pay config error");
        JSONObject object = JSONObject.parseObject(payParam);
        this.APP_ID = object.getString("APP_ID");
        this.APP_CHILD_ID = object.getString("APP_CHILD_ID");
        this.APP_KEY = object.getString("APP_KEY");
        this.reqUrl = object.getString("reqUrl");
    }

    public String getAPP_ID() {
        return APP_ID;
    }

    public void setAPP_ID(String APP_ID) {
        this.APP_ID = APP_ID;
    }

    public String getAPP_CHILD_ID() {
        return APP_CHILD_ID;
    }

    public void setAPP_CHILD_ID(String APP_CHILD_ID) {
        this.APP_CHILD_ID = APP_CHILD_ID;
    }

    public String getAPP_KEY() {
        return APP_KEY;
    }

    public void setAPP_KEY(String aPP_KEY) {
        this.APP_KEY = aPP_KEY;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }


}
