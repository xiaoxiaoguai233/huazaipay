package org.xxpay.pay.channel.jwwx;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.xxpay.core.common.util.MyLog;

/**
 * @author: dingzhiwei
 * @date: 18/3/1
 * @description: 个付通配置
 */
@Component
public class JwwxpayConfig {
	
    private static final MyLog _log = MyLog.getLog(JwwxpayConfig.class);


    public static final String CHANNEL_NAME = "jwwx";
    public static final String CHANNEL_NAME_WXPAY_QR = CHANNEL_NAME + "_wxpay_qr";
 
    public static final String RETURN_VALUE_SUCCESS = "success";
    public static final String RETURN_VALUE_FAIL = "fail";

    // 商户ID
    private String mchId;
    // 商户Key
    private String key;
    // 请求地址
    private String reqUrl;

    public JwwxpayConfig(){}

    public JwwxpayConfig(String payParam) {
    	_log.info("payParam={}", payParam);
        Assert.notNull(payParam, "init jwwxpay config error");
        JSONObject object = JSONObject.parseObject(payParam);
        this.mchId = object.getString("mchId");
    	_log.info("object.getString(\"key\")={}", object.getString("key"));
          this.key = object.getString("key");
        this.reqUrl = object.getString("reqUrl");
    }

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getReqUrl() {
		return reqUrl;
	}

	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}
 
}
