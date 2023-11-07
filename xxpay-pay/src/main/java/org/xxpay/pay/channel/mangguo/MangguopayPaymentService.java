package org.xxpay.pay.channel.mangguo;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.PayDigestUtil;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.util.RedisUtil;

@Service
public class MangguopayPaymentService extends BasePayment {

    private static final MyLog _log = MyLog.getLog(MangguopayPaymentService.class);

    @Override
    public String getChannelName() {
        return PayConstant.CHANNEL_NAME_MangGuoPay;
    }

    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    @Override
    public JSONObject pay(PayOrder payOrder) {
        String channelId = payOrder.getChannelId();
        JSONObject retObj;
        switch (channelId) {
            case PayConstant.PAY_CHANNEL_MangGuoPay_AliPay_H5:
                retObj = doMangGuoPayH5_Req_Alipay(payOrder);
                break;
            default:
                retObj = buildRetObj(PayConstant.RETURN_VALUE_FAIL, "不支持的支付宝渠道[channelId=" + channelId + "]");
                break;
        }
        return retObj;
    }

    private JSONObject doMangGuoPayH5_Req_Alipay(PayOrder payOrder) {
        String logPrefix = "【8023-支付宝H5(GMM)-8023】";

        JSONObject retObj = new JSONObject();

        JSONObject request_params = new JSONObject();
        request_params.put("amount", String.valueOf(payOrder.getAmount() / 100));
        request_params.put("mer_no", "M0311209417");
        request_params.put("trade_no", payOrder.getPayOrderId());
        request_params.put("pay_type", "1028");
        request_params.put("return_type", "json");
        request_params.put("notify_url", "http://payment.huazaipay.pro/api/mangguo/notify.htm");
        request_params.put("return_url", "http://payment.huazaipay.pro/api/mangguo/return.htm");
        request_params.put("remark", "");

        request_params.put("sign", PayDigestUtil.getSignToLowerCase(request_params, "fcbdddc7a9fecfcef3dff1f31cf9cf8c"));

        System.out.println(request_params);

        // 获取支付链接
        String body = "";
        try {
            body = Jsoup.connect("http://mgnowg.chxsw.xyz/api/gateway/suborder")
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/json")
                    .timeout(20000)
                    .requestBody(request_params.toJSONString())
                    .ignoreContentType(true)
                    .execute().body();
        }catch (Exception e){
            // 释放当前号码
            e.printStackTrace();
            retObj.put(PayConstant.RESULT_PARAM_ERRCODE, PayConstant.RESULT_VALUE_FAIL);        // errCode -1
            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "获取支付链接失败，请联系相关技术人员1001");
            return retObj;
        }
        JSONObject json = JSONObject.parseObject(body);
        _log.info("=== 拉起支付回调结果： {} ===", json);

        if(!json.getString("msg").equals("success")){
            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "获取支付链接失败，请联系相关技术人员1001");
            return retObj;
        }

        JSONObject data = json.getJSONObject("data");
        if(data.getString("trade_no").equals(payOrder.getPayOrderId())){
            RedisUtil.setString(payOrder.getPayOrderId(), data.toString(), data.getLong("expire_time") - System.currentTimeMillis() / 1000 );
            retObj.put("payLink", data.getString("pay_url"));
        }


        // 将订单更改为支付中
        int result = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), null);
        _log.info("[{}]更新订单状态为支付中:payOrderId={},channelOrderNo={},result={}", getChannelName(), payOrder.getPayOrderId(), "", result);

        retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);
        return retObj;
    }
}
