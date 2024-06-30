package org.xxpay.pay.channel.alipay;

import com.alibaba.fastjson.JSON;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.util.BfjSignUtil;
import org.xxpay.pay.util.JfSignUtil;
import org.xxpay.pay.util.mth.MD5Util;

@Service
public class JfdfPaymentService  extends BasePayment {

    private static final MyLog _log = MyLog.getLog(SsskmPaymentService.class);
    public final static String PAY_CHANNEL_JFDF_ALIPAY_SC = "jfdf_alipay_sc";	 // 见福电费

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
            case PAY_CHANNEL_JFDF_ALIPAY_SC :                  // 支付宝APP - SLRJ
                retObj = doAliPaySCSSSKMReq(payOrder);
                break;
        }
        return retObj;
    }

    /**
     * 支付宝APP支付-SLRJ
     * @param payOrder
     * @return
     */
    public JSONObject doAliPaySCSSSKMReq(PayOrder payOrder) throws Exception {

        String logPrefix = "【8028-见福电费-8028】";


        _log.info("{} 生成订单： ", logPrefix, payOrder.getPayOrderId());

        JSONObject retObj = new JSONObject();
        // formJump payUrl
        // urlJump  payUrl
        // codeImg  codeImgUrl
        // 填充支付链接到相关的app
        retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);


        // 请求接口
        String merchantId = "10071";
        String orderId = payOrder.getPayOrderId();
        String orderAmount = String.valueOf(payOrder.getAmount() / 100);
        String channelType = "888";
        String ApiKey = "1e26bf2e77ad4525a22568cc59cac162";


        JSONObject param_json = new JSONObject();
        param_json.put("merchantId", merchantId);
        param_json.put("orderId", orderId);
        param_json.put("orderAmount", orderAmount);
        param_json.put("channelType", channelType);
        param_json.put("notifyUrl", "http://pay.astro88.cc/api/pay_notify/jfdf");

        String sign = JfSignUtil.getSign(param_json, ApiKey);

        param_json.put("sign", sign);


        // 获取支付链接
        String body = "";
        try {
            body = Jsoup.connect("http://jianfu.8888888.life/api/newOrder")
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/json")
                    .timeout(20000)
                    .requestBody(param_json.toJSONString())
                    .ignoreContentType(true)
                    .execute().body();
        }catch (Exception e){
            // 释放当前号码
            e.printStackTrace();
            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "获取支付链接失败，请联系相关技术人员1001");
            return retObj;
        }
        JSONObject json = JSONObject.parseObject(body);
        _log.info("=== 拉起支付返回结果： {} ===", json);
        json = JSON.parseObject(json.getString("data"));
        String JumpUrl = json.getString("payUrl");


        JSONObject payParams = new JSONObject();
        payParams.put("payMethod", "urlJump");
        payParams.put("payUrl", JumpUrl);

        retObj.put("payParams",payParams );


        // 修改状态为支付中
        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), null);

        return retObj;
    }
}
