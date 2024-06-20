package org.xxpay.pay.channel.alipay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.util.RedisUtil;
import org.xxpay.pay.util.mth.MD5Util;

@Service
public class SjyhPaymentService  extends BasePayment {

    private static final MyLog _log = MyLog.getLog(SjyhPaymentService.class);
    public final static String PAY_CHANNEL_SJYH_ALIPAY_SC = "sjyh_alipay_sc";                // 支付宝当面付之H5支付

    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    @Autowired
    AlipayAsynCreateOrders alipayAsynCreateOrders;

    @Override
    public String getChannelName() {
        return PayConstant.CHANNEL_NAME_ALIPAY;
    }

    @Override
    public JSONObject pay(PayOrder payOrder) throws Exception {
        String channelId = payOrder.getChannelId();
        JSONObject retObj = null;
        switch (channelId) {
            case PAY_CHANNEL_SJYH_ALIPAY_SC :                  // 支付宝APP - SLRJ
                retObj = doAliPaySCSJYHReq(payOrder);
                break;
        }
        return retObj;
    }

    /**
     * 支付宝APP支付-SLRJ
     * @param payOrder
     * @return
     */
    public JSONObject doAliPaySCSJYHReq(PayOrder payOrder) throws Exception {

        String logPrefix = "【8026-手机银行-8026】";

        _log.info("{} 生成订单： ", logPrefix, payOrder.getPayOrderId());

        JSONObject retObj = new JSONObject();
        // formJump payUrl
        // urlJump  payUrl
        // codeImg  codeImgUrl
        // 填充支付链接到相关的app
        retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);


        // 请求接口
        String ApiKey = "d76615785bb54889953fe2904a3980a9";
        String recvid = "c3a77761-ed66-4093-9b89-5a63c2a7d5c8";
        String paytypes = "数字人民币";
        String orderid = payOrder.getPayOrderId();
        String amount = String.valueOf(payOrder.getAmount() / 100);


        String sign = MD5Util.encrypt(recvid + orderid + amount + ApiKey);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("recvid", recvid);
        jsonObject.put("orderid", orderid);
        jsonObject.put("amount", amount);
        jsonObject.put("paytypes", paytypes);
        jsonObject.put("notifyurl", "http://pay.astropay99.top/api/pay_notify/sjyh");
        jsonObject.put("memuid", MD5Util.encrypt(String.valueOf(payOrder.getMchId())));
        jsonObject.put("sign", sign);


        // 获取支付链接
        String body = "";
        try {
            body = Jsoup.connect("https://fh14no2113fdl.pppay24.com/createpay")
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/json")
                    .timeout(20000)
                    .requestBody(jsonObject.toJSONString())
                    .ignoreContentType(true)
                    .execute().body();
        }catch (Exception e){
            // 释放当前号码
            e.printStackTrace();
            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "获取支付链接失败，请联系相关技术人员1001");
            return retObj;
        }
        JSONObject json = JSONObject.parseObject(body);
        json = JSON.parseObject(json.getString("data"));
        _log.info("=== 拉起支付返回结果： {} ===", json);
        String JumpUrl = json.getString("navurl");


        JSONObject payParams = new JSONObject();
        payParams.put("payMethod", "urlJump");
        payParams.put("payUrl", JumpUrl);

        retObj.put("payParams",payParams );


        // 修改状态为支付中
        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), null);

        return retObj;
    }
}
