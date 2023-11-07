package org.xxpay.pay.channel.hssmpay;

import com.alibaba.dubbo.common.utils.Assert;
import com.alibaba.fastjson.JSONObject;
import org.xxpay.core.common.util.MyLog;

public class HssmConfig {


    private static final MyLog _log = MyLog.getLog(HssmConfig.class);


    public static final String CHANNEL_NAME = "Hssm";

    public static final String CHANNEL_NAME_WXPAY_QR_H5= CHANNEL_NAME + "_wxpay_qr_h5";
    public static final String CHANNEL_NAME_WXPAY_SC = CHANNEL_NAME + "_wxpay_sc";
    public static final String CHANNEL_NAME_ALIPAY_QR_H5= CHANNEL_NAME + "_alipay_qr_h5";
    public static final String CHANNEL_NAME_ALIPAY_SC= CHANNEL_NAME + "_alipay_sc";

    public static final String CHANNEL_NAME_SCAN_QR= "S0001" ;
    public static final String CHANNEL_NAME_H5_PAY= "H0001";


    public static final String RETURN_VALUE_SUCCESS = "success";
    public static final String RETURN_VALUE_FAIL = "fail";

    public static final String INTERFACE_VERSION = "100";

    // 商户ID
    private String MERCHANT_ID;


    // APPID
    private String APP_ID;

    //  应用KEY
    private String APP_KEY;

    //  应用秘钥
    private String APP_SECRET;

    // 请求地址
    private String reqUrl;


    public HssmConfig() {
    }

    public HssmConfig(String payParam) {
        _log.info("payParam={}", payParam);
        Assert.notNull(payParam, "init hssm config error");
        JSONObject object = JSONObject.parseObject(payParam);
        this.MERCHANT_ID = object.getString("MERCHANT_ID");
        this.APP_ID = object.getString("APP_ID");
        this.APP_KEY = object.getString("APP_KEY");
        this.APP_SECRET = object.getString("APP_SECRET");
        this.reqUrl = object.getString("reqUrl");

    }

    public String getMERCHANT_ID() {
        return MERCHANT_ID;
    }

    public void setMERCHANT_ID(String MERCHANT_ID) {
        this.MERCHANT_ID = MERCHANT_ID;
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

    public void setAPP_KEY(String APP_KEY) {
        this.APP_KEY = APP_KEY;
    }

    public String getAPP_SECRET() {
        return APP_SECRET;
    }

    public void setAPP_SECRET(String APP_SECRET) {
        this.APP_SECRET = APP_SECRET;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }
}
