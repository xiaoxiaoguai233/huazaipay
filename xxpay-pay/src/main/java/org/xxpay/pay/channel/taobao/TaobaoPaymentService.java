package org.xxpay.pay.channel.taobao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.channel.sandpay.sdk.HttpClient;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.util.Util;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * @author: carter
 * @date: 2019-07-31
 * @description: 淘宝接口
 */
@Service
public class TaobaoPaymentService extends BasePayment {

    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    private static final MyLog _log = MyLog.getLog(TaobaoPaymentService.class);

    //http连接超时时间
    public static int connectTimeout = 300000;
    //http响应超时时间
    public static int readTimeout = 600000;

    @Override
    public String getChannelName() {
        return TaoBaoPayConfig.CHANNEL_NAME;
    }

    /**
     * 支付
     *
     * @param payOrder
     * @return
     */
    @Override
    public JSONObject pay(PayOrder payOrder) {
        String channelId = payOrder.getChannelId();
        JSONObject retObj;
        switch (channelId) {
            case TaoBaoPayConfig.CHANNEL_NAME_UNIFORM_QR:
                retObj = doPay(payOrder,"h5");
                break;
            case TaoBaoPayConfig.CHANNEL_NAME_UNIFORM_SC:
                retObj = doPay(payOrder,"sc");
                break;
            default:
                retObj = buildRetObj(PayConstant.RETURN_VALUE_FAIL, "不支持的渠道[channelId=" + channelId + "]");
                break;
        }
        return retObj;
    }

    /**
     * 支付
     *
     * @param payOrder
     * @return
     */
    public JSONObject doPay(PayOrder payOrder,String type) {
        TaoBaoPayConfig taoBaoPayConfig = new TaoBaoPayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();

        try {

            String payUrl = taoBaoPayConfig.getReqUrl();
            String key = taoBaoPayConfig.getAPP_KEY();//密钥
            String mch_id = taoBaoPayConfig.getMerchantId();//商户id

            String out_trade_no = payOrder.getPayOrderId();//20位订单号 时间戳+6位随机字符串组成
            Long payAmount = payOrder.getAmount() / 100;
            String total_fee = payAmount.toString();//下单价格，单位分
            String notify_url = payConfig.getNotifyUrl(getChannelName());//通知地址
            String ip = payOrder.getClientIp();

            StringBuffer sb = new StringBuffer();
            sb.append("notify_url=" + notify_url);
            sb.append("&order_id=" + out_trade_no);
            sb.append("&price=" + total_fee);
            sb.append("&key=" + key);
            String sign = Util.md5(sb.toString()).toUpperCase();

            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(),
                    payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(),
                    payOrder.getPayOrderId(), updateCount);

            HashMap<String, String> mav = new HashMap<>();
            mav.put("notify_url", notify_url);
            mav.put("order_id", out_trade_no);
            mav.put("price", total_fee);
            mav.put("sign", sign);

            String result;
            try {
                _log.info("[淘宝商户:{}]请求URL:{}, 报文：\n{}", mch_id, payUrl, mav.toString());
                result = HttpClient.doPost(payUrl, mav, connectTimeout, readTimeout);
                result = URLDecoder.decode(result, "UTF-8");
            } catch (IOException e) {
                _log.error(e.getMessage());
                return null;
            }


            JSONObject resObj = JSONObject.parseObject(result);
            if (!resObj.getInteger("code").equals(1)) {
                _log.info("[淘宝商户]下单返回失败信息:{}", resObj.get("msg"));
                retObj.put("errDes", resObj.get("msg"));
                retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
                return retObj;
            }
            payUrl = resObj.getJSONObject("data").getString("pay_url");


            StringBuffer payForm = new StringBuffer();
            String aliPay="https://ds.alipay.com/?from=mobilecodec&scheme=alipays://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=";
            String qr_code = payUrl;
            String mclient_url="https://mclient.alipay.com/h5/peerpay.htm?"+qr_code.substring(qr_code.indexOf('?')+1);
            String alipay_url = "alipays://platformapi/startapp?appId=20000067&url=" + URLEncoder.encode(mclient_url);

            String aim = payUrl;
            _log.info(alipay_url+"*****************");
            payForm.append("<form style=\"display: none\" action=\"" + alipay_url + "\" method=\"get\"></form>");
            payForm.append("<script>document.forms[0].submit();</script>");

            // 支付链接地址
            retObj.put("payOrderId", payOrder.getPayOrderId()); // 设置支付订单ID
            JSONObject payParams = new JSONObject();
            if(type.equals("h5")) {
                payParams.put("payUrl", payForm);
                payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);
            }
            else {
                payUrl = resObj.getJSONObject("data").getString("qrcode_url");
                StringBuffer scpayForm = new StringBuffer();
                scpayForm.append("<form style=\"display: none\" action=\"" + payUrl + "\" method=\"get\"></form>");
                scpayForm.append("<script>document.forms[0].submit();</script>");
                payParams.put("payUrl", scpayForm);
                payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);
            }
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

}
