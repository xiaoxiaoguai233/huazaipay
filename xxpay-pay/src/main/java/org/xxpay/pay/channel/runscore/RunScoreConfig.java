package org.xxpay.pay.channel.runscore;

import com.alibaba.dubbo.common.utils.Assert;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.xxpay.core.common.util.MyLog;

@Component
public class RunScoreConfig {


    private static final MyLog _log = MyLog.getLog(RunScoreConfig.class);


    public static final String CHANNEL_NAME = "RunScore";

    public static final String CHANNEL_NAME_WXPAY_QR = CHANNEL_NAME + "_Pay_wxpay_qr";
    public static final String CHANNEL_NAME_ALIPAY_QR = CHANNEL_NAME + "_Pay_alipay_sc";
    public static final String CHANNEL_NAME_POS_QR = CHANNEL_NAME + "_Pay_pos_sc";


    public static final String RETURN_VALUE_SUCCESS = "success";
    public static final String RETURN_VALUE_FAIL = "fail";

    // 商户ID
    private String APP_ID;
    // 商户Key
    private String APP_KEY;
    // 请求地址
    private String reqUrl;

    public RunScoreConfig(){}

    public RunScoreConfig(String payParam) {
        _log.info("payParam={}", payParam);
        Assert.notNull(payParam, "init jwwxpay config error");
        JSONObject object = JSONObject.parseObject(payParam);
        this.APP_ID = object.getString("APP_ID");
        this.APP_KEY = object.getString("APP_KEY");
        this.reqUrl = object.getString("reqUrl");

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
