package org.xxpay.pay.channel.antpay;

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
public class AntPayConfig {

    private static final MyLog _log = MyLog.getLog(AntPayConfig.class);

    public static final String CHANNEL_NAME = "Ant";

    public static final String RETURN_VALUE_SUCCESS = "SUCCESS";
    public static final String RETURN_VALUE_FAIL = "fail";

    public static final String CHANNEL_NAME_ALIPAY_QR = CHANNEL_NAME + "Pay_alipay_sc";

    //	请求地址
    private String reqUrl;

    //  商户私钥
    private String privateKey;

    // 商户公钥
    private String publicKey;

    //   支付方式
    private String payType;


    public AntPayConfig() {
    }

    public AntPayConfig(String payParam) {
        _log.info("payParam={}", payParam);
        Assert.notNull(payParam, "init two one pay config error");
        JSONObject object = JSONObject.parseObject(payParam);
        this.reqUrl = object.getString("reqUrl");
        this.privateKey = object.getString("privateKey");
        this.publicKey = object.getString("publicKey");
        this.payType = object.getString("payType");
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
