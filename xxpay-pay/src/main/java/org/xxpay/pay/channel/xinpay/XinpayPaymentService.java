package org.xxpay.pay.channel.xinpay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.XXPayUtil;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.channel.sandpay.sdk.HttpClient;
import org.xxpay.pay.channel.ssfpay.utils.SsfpayUtils;
import org.xxpay.pay.channel.swiftpay.util.SignUtils;
import org.xxpay.pay.channel.swiftpay.util.XmlUtils;
import org.xxpay.pay.mq.BaseNotify4MchPay;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 18/3/1
 * @description: 新支付接口
 */
@Service
public class XinpayPaymentService extends BasePayment {

    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    @Autowired

    private static final MyLog _log = MyLog.getLog(XinpayPaymentService.class);

    @Override
    public String getChannelName() {
        return XinPayConfig.CHANNEL_NAME;
    }

    /**
     * 新支付
     *
     * @param payOrder
     * @return
     */
    @Override
    public JSONObject pay(PayOrder payOrder) {

        String channelId = payOrder.getChannelId();
        JSONObject retObj;
        switch (channelId) {
            case XinPayConfig.CHANNEL_NAME_ALIPAY_QR:
                retObj = doPayQrReq(payOrder, "alipay");
                break;
            case XinPayConfig.CHANNEL_NAME_WXPAY_QR:
                retObj = doPayQrReq(payOrder, "wechat");
                break;
            case XinPayConfig.CHANNEL_NAME_YUNPAY_QR:
                retObj = doPayQrReq(payOrder, "unionpay");
                break;
            case XinPayConfig.CHANNEL_NAME_BANK:
                retObj = doPayQrReq(payOrder, "bank");
                break;
            default:
                retObj = buildRetObj(PayConstant.RETURN_VALUE_FAIL, "不支持的渠道[channelId=" + channelId + "]");
                break;
        }
        return retObj;
    }

    /**
     * @param payOrder
     * @return
     */
    public JSONObject doPayQrReq(PayOrder payOrder, String channel) {
        XinPayConfig xinpayConfig = new XinPayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        HashMap<String, String> paramMap = new HashMap<>();

        try {
            String app_key = xinpayConfig.getAppKey(); // 商户app key
            payOrder.getPayOrderId();
            String notify_url = payConfig.getNotifyUrl(getChannelName()); // 通知地址
            String out_trade_no = payOrder.getPayOrderId(); // 商户 20位订单号 时间戳+6位随机字符串组成
            String type = channel;
            DecimalFormat df = new DecimalFormat("######0.00");
            String money = df.format(payOrder.getAmount().doubleValue() / 100);
            String app_secret = xinpayConfig.getAppSecret();
            String reqUrl =xinpayConfig.getReqUrl();

            String stringSignTemp = app_key + out_trade_no + money + type + notify_url + app_secret;
            System.out.println("===" + stringSignTemp);
            String pay_md5sign = DigestUtils.md5Hex(stringSignTemp).toLowerCase();
            paramMap.put("app_key", app_key);
            paramMap.put("notify_url", notify_url);
            paramMap.put("out_trade_no", out_trade_no);
            paramMap.put("type", type);
            paramMap.put("money", money);
            paramMap.put("sign", pay_md5sign);

            String result = HttpClient.doPost(xinpayConfig.getReqUrl(), paramMap, 2000, 20009);
            result = URLDecoder.decode(result, "UTF-8");
            JSONObject resObj = JSONObject.parseObject(result);

            if("false".equals(String.valueOf(resObj.get("success")))){
                _log.info("[新支付]下单返回失败信息:{}", resObj.get("message"));
                retObj.put("errDes", resObj.get("message"));
                retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
                return retObj;
            }
            JSONObject resData= JSONObject.parseObject(resObj.getString("data")); //下单返回的验签参数
            String resSign= resData.getString("sign"); //下单返回的验签参数

            //下单返回参数验签
            StringBuffer sbRes = new StringBuffer();
            String payUrl = resData.getString("pay_url");
            sbRes.append(resData.getString("app_key"));
            sbRes.append(resData.getString("order_no"));
            sbRes.append(resData.getString("out_trade_no"));
            sbRes.append(resData.getString("money"));
            sbRes.append(resData.getString("type"));
            sbRes.append(resData.getString("pay_url"));
            sbRes.append(app_secret);
            _log.info("============="+sbRes.toString());
            String validSign = SsfpayUtils.md5(sbRes.toString()).toLowerCase();
            if (!validSign.equals(resSign)) {
                _log.info("[新支付商户:{}]下单返回参数，验签失败", validSign, resSign);
                retObj.put("retMsg", "新支付商户下单返回参数验签失败!");
                return retObj;
            }

            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), payOrder.getPayOrderId(), updateCount);

            StringBuffer payForm = new StringBuffer();
            payForm.append("<form style=\"display: none\" action=\"" + payUrl + "\" method=\"get\"></form>");
            payForm.append("<script>document.forms[0].submit();</script>");

            // 支付链接地址
            retObj.put("payOrderId", payOrder.getPayOrderId()); // 设置支付订单ID
            JSONObject payParams = new JSONObject();
            payParams.put("payUrl", payForm);
            payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);
            retObj.put("payParams", payParams);
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);
            return retObj;
        } catch (Exception e) {
            _log.error(e, "");
            retObj.put("errDes", "操作失败!");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
    }


    public static void main(String[] args) throws Exception {
//        String app_key = "537620";
//        String notify_url = "http://www.baidu.com";
//        String out_trade_no = "85463311568";
//        String type = "alipay";
//        String money = "200.00";
//        String reqUrl = "http://api.xzf688.com/api/v1/orders";
//        String app_secret = "1e999a6bb9fe2ba2b9da8237c417b2adb51f27a4958bf03511a2a67a0204adb7";
//
//        String stringSignTemp = app_key + out_trade_no + money + type + notify_url + app_secret;
//        System.out.printf("++++++签名加密前：" + stringSignTemp);
//
//        String sign = DigestUtils.md5Hex(stringSignTemp).toLowerCase();
//        System.out.printf("======签名加密后：" + sign);
//
//        HashMap<String,String> paramObj = new HashMap<>();
//        paramObj.put("app_key", app_key);
//        paramObj.put("notify_url", notify_url);
//        paramObj.put("out_trade_no", out_trade_no);
//        paramObj.put("type", type);
//        paramObj.put("money", money);
//        paramObj.put("app_secret", app_secret);
//        paramObj.put("sign", sign);
//        System.out.println(paramObj.toString());
//
//        String result = HttpClient.doPost(reqUrl, paramObj, 2000, 20009);
//        result = URLDecoder.decode(result, "UTF-8");
//        JSONObject resObj = JSONObject.parseObject(result);
//        System.out.println(String.valueOf(resObj.get("success")));

//        String orderSn ="P01201908162152484880000";
//        String payTime="1565978146";
//        String totalFee="1.00";
//        String paySign="270C41CB3F28B82843959578F7420BA120AAF1B8";
//        String appId="774583846";
//        String payRemark="111111";
//        String stringSignTemp ="appId="+appId+"&orderSn="+orderSn+"&payRemark="+payRemark+"&payTime="+payTime+"&totalFee="+totalFee+"&key="+"452a78ddd69f97f0bca39d9b1dc448ba53022a2d";;
//        String pay_md5sign = DigestUtils.shaHex(stringSignTemp).toUpperCase();
//        _log.info(pay_md5sign,"----",String.valueOf(pay_md5sign==paySign));
//        _log.info(String.valueOf(pay_md5sign.equals(paySign)));
    }

}
