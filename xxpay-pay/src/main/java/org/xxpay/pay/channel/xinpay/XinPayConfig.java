package org.xxpay.pay.channel.xinpay;

import com.alibaba.dubbo.common.utils.Assert;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.xxpay.core.common.util.MyLog;

/**
 * @author: dingzhiwei
 * @date: 18/3/1
 * @description: 个付通配置
 */
@Component
public class XinPayConfig {

    private static final MyLog _log = MyLog.getLog(XinPayConfig.class);

    public static final String CHANNEL_NAME = "Xin";

    public static final String RETURN_VALUE_SUCCESS = "success";
    public static final String RETURN_VALUE_FAIL = "fail";

    public static final String CHANNEL_NAME_ALIPAY_QR = CHANNEL_NAME + "Pay_alipay_sc";
    public static final String CHANNEL_NAME_WXPAY_QR = CHANNEL_NAME + "Pay_wxpay_qr";
    public static final String CHANNEL_NAME_YUNPAY_QR = CHANNEL_NAME + "Pay_unionpay_qr";
    public static final String CHANNEL_NAME_BANK = CHANNEL_NAME + "Pay_bank";

    //商户key
    private String appKey;

    //	请求地址
    private String reqUrl;

    // 商户密钥
    private String appSecret;

    public XinPayConfig() {
    }

    public XinPayConfig(String payParam) {
        _log.info("payParam={}", payParam);
        Assert.notNull(payParam, "init two one pay config error");
        JSONObject object = JSONObject.parseObject(payParam);
        this.appKey = object.getString("appKey");
        this.reqUrl = object.getString("reqUrl");
        this.appSecret = object.getString("appSecret");

    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
