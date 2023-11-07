package org.xxpay.pay.channel.runscore;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.channel.sandpay.sdk.HttpClient;
import org.xxpay.pay.mq.BaseNotify4MchPay;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.HashMap;

@Service
public class RunscorePaymentService extends BasePayment {


    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    private static final MyLog _log = MyLog.getLog(RunscorePaymentService.class);

    @Override
    public String getChannelName() {
        return RunScoreConfig.CHANNEL_NAME;
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
            case RunScoreConfig.CHANNEL_NAME_WXPAY_QR:
                retObj = doPayQrReq(payOrder, "wechat");
                break;
            case RunScoreConfig.CHANNEL_NAME_ALIPAY_QR:
                retObj = doPayQrReq(payOrder, "alipay");
                break;

            case RunScoreConfig.CHANNEL_NAME_POS_QR:
                retObj = doPayQrReq(payOrder, "ys-pos");
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
        RunScoreConfig runScoreConfig = new RunScoreConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        JSONObject paramMap = new JSONObject();


        String AuthorizationURL = runScoreConfig.getReqUrl();
        String merchantId = runScoreConfig.getAPP_ID();
        String keyValue = runScoreConfig.getAPP_KEY();

        try {
            String pay_memberid = merchantId;//商户id
            String pay_orderid = payOrder.getPayOrderId();//20位订单号 时间戳+6位随机字符串组成
            String pay_applydate = RunScorePayUtils.generateTime();//yyyy-MM-dd HH:mm:ss
            String pay_notifyurl = payConfig.getNotifyUrl(getChannelName());
            ;//通知地址
            String pay_callbackurl = payConfig.getReturnUrl(getChannelName());
            ;//回调地址
            DecimalFormat df = new DecimalFormat("######0.00");
            String pay_amount = df.format(payOrder.getAmount().doubleValue() / 100);
            ;
            String pay_attach = "";

            String stringSignTemp = merchantId + pay_orderid+pay_amount+pay_notifyurl+runScoreConfig.getAPP_KEY();
            System.out.println("===" + stringSignTemp);
            String pay_md5sign = RunScorePayUtils.md5(stringSignTemp).toLowerCase();
            HashMap<String, String> param = new HashMap<>();
            param.put("payType", channel);
            param.put("amount", pay_amount);
            param.put("orderNo", pay_orderid);
            param.put("attch", "attach");
            param.put("merchantNum", merchantId);
            param.put("notifyUrl", pay_notifyurl);
            param.put("returnUrl", pay_callbackurl);
            param.put("ip", payOrder.getClientIp());
            param.put("sign", pay_md5sign);
            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), payOrder.getPayOrderId(), updateCount);
            StringBuffer payForm = new StringBuffer();
            String toPayUrl = runScoreConfig.getReqUrl();

            String result = HttpClient.doPost(toPayUrl, param, 10000, 10000);
            result = URLDecoder.decode(result, "UTF-8");
            JSONObject resObj = JSONObject.parseObject(result);
            if (!resObj.getBooleanValue("success")) {
                _log.info("[跑分商户]下单返回失败信息:{}", resObj.get("message"));
//                retObj.put("errDes", resObj.get("message"));
                String payUrl = resObj.getJSONObject("data").getString("payErrorPage");
                String orderNO = payUrl.split("=")[1];
                payForm.append("<form style=\"display: none\" action=\"" + payUrl.split("\\?")[0] + "\" method=\"get\">");
                payForm.append("<input name=\"region\" value=\"" + orderNO + "\" > </form>");
                payForm.append("<script>document.forms[0].submit();</script>");


                // 支付链接地址
                retObj.put("payOrderId", payOrder.getPayOrderId()); // 设置支付订单ID
                JSONObject payParams = new JSONObject();
                payParams.put("payUrl", payForm);
                payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);
                retObj.put("payParams", payParams);
                retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);
                return retObj;
            }
            String payUrl = resObj.getJSONObject("data").getString("payUrl");
            String orderNO = payUrl.split("=")[1];
            payForm.append("<form style=\"display: none\" action=\"" + payUrl.split("\\?")[0] + "\" method=\"get\">");
            payForm.append("<input name=\"orderNo\" value=\"" + orderNO + "\" > </form>");
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
}

/**
 * {
 *     "success": true,
 *     "msg": "success",
 *     "code": 200,
 *     "timestamp": 1567024752964,
 *     "data": {
 *         "payUrl": "http://localhost:8080/pay?orderNo=1166812500834910208"
 *     }
 * }
 *
 *
 * {
 *     "success": false,
 *     "msg": "签名不正确",
 *     "code": 500,
 *     "timestamp": 1567028413410,
 *     "data": null
 * }
 */
