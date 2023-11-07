package org.xxpay.pay.channel.xhpay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.XXPayUtil;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.channel.bolepay.util.BolePayUtils;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.util.HttpClientUtils;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * @author: dingzhiwei
 * @date: 18/3/1
 * @description: 星火支付接口
 */
@Service
public class XhPaymentService extends BasePayment {

    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    @Autowired

    private static final MyLog _log = MyLog.getLog(XhPaymentService.class);

    @Override
    public String getChannelName() {
        return XhpayConfig.CHANNEL_NAME;
    }

    /**
     * 支付
     *
     * @param payOrder
     * @return
     */
    @Override
    public JSONObject pay(PayOrder payOrder) {
        JSONObject retObj;
        retObj = doPayQrReq(payOrder);
        return retObj;
    }

    /**
     * @param payOrder
     * @return
     */
    public JSONObject doPayQrReq(PayOrder payOrder) {
        XhpayConfig xhpayConfig = new XhpayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        JSONObject paramMap = new JSONObject();
        String keyValue = xhpayConfig.getMd5Key();

        try {
            String stringSignTemp = payOrder.getPayOrderId() + "" + payConfig.getReturnUrl(XhpayConfig.CHANNEL_NAME) + payConfig.getNotifyUrl(XhpayConfig.CHANNEL_NAME) + payOrder.getAmount() / 100 + "MD5" + keyValue;
            System.out.println("===" + stringSignTemp);
            String pay_md5sign = DigestUtils.md5Hex(stringSignTemp.getBytes());
            HashMap<String, Object> param = new HashMap<>();
            param.put("outOrderId", payOrder.getPayOrderId());
            param.put("customerAmount", "");
            param.put("customerAmountCny", payOrder.getAmount() / 100);
            param.put("APPKey", xhpayConfig.getAPPKey());
            param.put("pickupUrl", payConfig.getReturnUrl(XhpayConfig.CHANNEL_NAME));
            param.put("receiveUrl", payConfig.getNotifyUrl(XhpayConfig.CHANNEL_NAME));
            param.put("signType", "MD5");
            param.put("sign", pay_md5sign);
            String reqData = XXPayUtil.genUrlParams(paramMap);


            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), payOrder.getPayOrderId(), updateCount);
            StringBuffer payForm = new StringBuffer();
            String toPayUrl = xhpayConfig.getReqUrl();
            payForm.append("<form style=\"display: none\" action=\"" + toPayUrl + "\" method=\"get\">");
            payForm.append("<input name=\"outOrderId\" value=\"" + payOrder.getPayOrderId() + "\" >");
            payForm.append("<input name=\"customerAmountCny\" value=\"" + payOrder.getAmount() / 100 + "\" >");
            payForm.append("<input name=\"APPKey\" value=\"" + xhpayConfig.getAPPKey() + "\" >");
            payForm.append("<input name=\"pickupUrl\" value=\"" + payConfig.getReturnUrl(XhpayConfig.CHANNEL_NAME) + "\" >");
            payForm.append("<input name=\"receiveUrl\" value=\"" + payConfig.getNotifyUrl(XhpayConfig.CHANNEL_NAME) + "\" >");
            payForm.append("<input name=\"signType\" value=\"MD5" + "\" >");
            payForm.append("<input name=\"sign\" value=\"" + pay_md5sign + "\" >");
            payForm.append("<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >");
            payForm.append("</form>");
            payForm.append("<script>document.forms[0].submit();</script>");

            // 支付链接地址
            retObj.put("payOrderId", payOrder.getPayOrderId()); // 设置支付订单ID
            JSONObject payParams = new JSONObject();
            payParams.put("payUrl", payForm);
            String payJumpUrl = toPayUrl + "?" + reqData;
            payParams.put("payJumpUrl", payJumpUrl);
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
//        String outOrderId = "1x00x";
//        String customerAmount = "100.00";
//        String customerAmountCny = "";
//
//        String stringSignTemp = outOrderId+customerAmount + XhpayConfig.PICKUP_URL + XhpayConfig.RECEIVE_URL +customerAmountCny+XhpayConfig.SIGN_TYPE+XhpayConfig.MD5_KEY;
//        System.out.println(stringSignTemp+"-----");
//        String sign = Util.md5(stringSignTemp);
//        HashMap<String,Object> param= new HashMap<>();
//        param.put("outOrderId",outOrderId);
//        param.put("customerAmount",customerAmount);
//        param.put("customerAmountCny",customerAmountCny);
//        param.put("APPKey",XhpayConfig.APP_KEY);
//        param.put("pickupUrl",XhpayConfig.PICKUP_URL);
//        param.put("receiveUrl",XhpayConfig.RECEIVE_URL);
//        param.put("signType",XhpayConfig.SIGN_TYPE);
//        param.put("sign",sign);
//
//        System.out.println(param.toString());
//        String s = XXPayUtil.genUrlParams(param);
//        String url_get = XhpayConfig.PAY_URL + "?" + s;
//        System.out.println(url_get);
////        HttpClientUtils.Get(url_get,"","","");
//
////        System.out.println(post);
//        HttpClientUtils  httpClientUtils = new HttpClientUtils();
//        String s1 = httpClientUtils.get(url_get);
//
//        System.out.printf(s1);

    }

}
