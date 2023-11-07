package org.xxpay.pay.channel.xunpay;

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
public class XunPayConfig {

    private static final MyLog _log = MyLog.getLog(XunPayConfig.class);

    public static final String CHANNEL_NAME = "Xun_";

    public static final String RETURN_VALUE_SUCCESS = "success";
    public static final String RETURN_VALUE_FAIL = "fail";

    public static final String CHANNEL_NAME_ALIPAY_QR = CHANNEL_NAME + "Pay_alipay_sc";
    public static final String CHANNEL_NAME_WXPAY_QR = CHANNEL_NAME + "Pay_wxpay_qr";

    //商户key
    private String appKey;

    //	请求地址
    private String reqUrl;

    // 商户密钥
    private String appSecret;

    public XunPayConfig() {
    }

    public XunPayConfig(String payParam) {
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
