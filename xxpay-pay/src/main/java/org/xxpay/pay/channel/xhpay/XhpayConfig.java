package org.xxpay.pay.channel.xhpay;

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
public class XhpayConfig {

    private static final MyLog _log = MyLog.getLog(XhpayConfig.class);

    public static final String CHANNEL_NAME = "xh";

    public static final String RETURN_VALUE_SUCCESS = "success";

//    public static final String PAY_URL = "https://s.starfireotc.com/payLink/web.html";  //入金接口地址
//
//    public static final String APP_KEY = "9f82f328feb44232a0bac2d2eb20bbaa";            //商户号（商户后台 -> 安全设置 获取APPKey）
//
//    public static final String MD5_KEY = "afcb115f651fb3562e940233628fc4b4";            //密钥（商户后台 -> 安全设置 获取MD5Key）
//
//    public static final String SIGN_TYPE = "MD5";                                       //签名类型
//
//    public static final String PICKUP_URL = "http://localhost:8193/x_mgr/start/index.html";    //跳转页面
//
//    public static final String RECEIVE_URL = "http://127.0.0.1:8080/receive";           //回调通知接口地址

    // 商户KEY
    private String APPKey;
    //	请求地址
    private String reqUrl;
    //MD5_KEY
    private String md5Key;


    public XhpayConfig() {
    }

    public XhpayConfig(String payParam) {
        _log.info("payParam={}", payParam);
        Assert.notNull(payParam, "init XinHuo pay config error");
        JSONObject object = JSONObject.parseObject(payParam);
        this.APPKey = object.getString("APP_KEY");
        this.reqUrl = object.getString("reqUrl");
        this.md5Key = object.getString("MD5_KEY");
    }

    public String getAPPKey() {
        return APPKey;
    }

    public void setAPPKey(String APPKey) {
        this.APPKey = APPKey;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public String getMd5Key() {
        return md5Key;
    }

    public void setMd5Key(String md5Key) {
        this.md5Key = md5Key;
    }
}
