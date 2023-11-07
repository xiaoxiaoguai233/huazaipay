package org.xxpay.pay.channel.xunpay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.channel.sandpay.sdk.HttpClient;
import org.xxpay.pay.channel.ssfpay.utils.SsfpayUtils;
import org.xxpay.pay.mq.BaseNotify4MchPay;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * @author: dingzhiwei
 * @date: 18/3/1
 * @description: 讯付接口
 */
@Service
public class XunPaymentService extends BasePayment {

    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    @Autowired

    private static final MyLog _log = MyLog.getLog(XunPaymentService.class);

    @Override
    public String getChannelName() {
        return XunPayConfig.CHANNEL_NAME;
    }

    /**
     * 讯付
     *
     * @param payOrder
     * @return
     */
    @Override
    public JSONObject pay(PayOrder payOrder) {

        String channelId = payOrder.getChannelId();
        JSONObject retObj;
        switch (channelId) {
            case XunPayConfig.CHANNEL_NAME_ALIPAY_QR:
                retObj = doPayQrReq(payOrder, "ALP");
                break;
            case XunPayConfig.CHANNEL_NAME_WXPAY_QR:
                retObj = doPayQrReq(payOrder, "wechat");
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
        XunPayConfig xinpayConfig = new XunPayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        HashMap<String, String> paramMap = new HashMap<>();

        try {
            String app_key = xinpayConfig.getAppKey(); // 商户app key
            payOrder.getPayOrderId();
            String notify_url = payConfig.getNotifyUrl(getChannelName()); // 通知地址
            String out_trade_no = payOrder.getPayOrderId(); // 商户 20位订单号 时间戳+6位随机字符串组成
            String type = channel;
            DecimalFormat df = new DecimalFormat("000000000000");
            String money = df.format(payOrder.getAmount().doubleValue() );
            String app_secret = xinpayConfig.getAppSecret();
            String reqUrl =xinpayConfig.getReqUrl();

            String stringSignTemp = money +notify_url + type + out_trade_no +app_key +   app_secret;
            System.out.println("===" + stringSignTemp);
            String pay_md5sign = DigestUtils.md5Hex(stringSignTemp);
            paramMap.put("xfmcode", app_key);
            paramMap.put("ordernum", out_trade_no);
            paramMap.put("allfee", money);
            paramMap.put("chcd", type);
            paramMap.put("backurl", notify_url);
            paramMap.put("sign", pay_md5sign);

            String result = HttpClient.doPost(reqUrl, paramMap, 2000, 20009);
            JSONObject resObj = JSONObject.parseObject(result);

            if(!"200".equals(String.valueOf(resObj.get("code")))){
                _log.info("[讯付]下单返回失败信息:{}", resObj.get("message"));
                retObj.put("errDes", resObj.get("message"));
                retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
                return retObj;
            }
            JSONObject resData= resObj; //下单返回的验签参数
            String resSign= resData.getString("sign"); //下单返回的验签参数

            //下单返回参数验签
            StringBuffer sbRes = new StringBuffer();
            String payUrl = resData.getString("qrcode");
            sbRes.append(payUrl);
            sbRes.append(resData.getString("ordernum"));
            sbRes.append(resData.getString("xfmcode"));
            sbRes.append(app_secret);
            _log.info("============="+sbRes.toString());
            String validSign = SsfpayUtils.md5(sbRes.toString()).toLowerCase();
            if (!validSign.equals(resSign)) {
                _log.info("[讯付商户:{}]下单返回参数，验签失败", validSign, resSign);
                retObj.put("retMsg", "讯付商户下单返回参数验签失败!");
                return retObj;
            }

            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), payOrder.getPayOrderId(), updateCount);



            // 支付链接地址
            retObj.put("payOrderId", payOrder.getPayOrderId()); // 设置支付订单ID
            JSONObject payParams = new JSONObject();
            payParams.put("payUrl", payUrl);
            payParams.put("payMethod", PayConstant.PAY_METHOD_URL_JUMP);
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

    }

}
