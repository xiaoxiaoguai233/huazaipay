package org.xxpay.pay.channel.alipay;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.util.RedisUtil;

@Service
public class SsskmPaymentService extends BasePayment {

    private static final MyLog _log = MyLog.getLog(SsskmPaymentService.class);
    public final static String PAY_CHANNEL_SSSKM_ALIPAY_SC = "ssskm_alipay_sc";	            // 支付宝当面付之H5支付

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
            case PAY_CHANNEL_SSSKM_ALIPAY_SC :                  // 支付宝APP - SLRJ
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

        String logPrefix = "【8025-实时收款码-8025】";

        _log.info("{} 生成订单： ", logPrefix, payOrder.getPayOrderId());

        JSONObject retObj = new JSONObject();

        // 填充支付链接到相关的app
        retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);

        // Redis参数设置
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "0");   // 代表订单生成了
        jsonObject.put("orderId", payOrder.getPayOrderId());
        jsonObject.put("time", System.currentTimeMillis());
        jsonObject.put("amount", payOrder.getAmount() / 100);
        jsonObject.put("interval", 5);  // 5 秒钟再请求


        RedisUtil.setString(payOrder.getPayOrderId(), jsonObject.toJSONString(), 1800);     // 一个小时

        // formJump payUrl
        // urlJump  payUrl
        // codeImg  codeImgUrl

        JSONObject payParams = new JSONObject();
        payParams.put("payMethod", "urlJump");


        payParams.put("payUrl", "http://pay.astro88.cc/api/ssskm/" + payOrder.getPayOrderId());

//        payParams.put("payUrl", "http://192.168.1.3:3020/api/ssskm/" + payOrder.getPayOrderId());

        retObj.put("payParams",payParams );


        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), null);

        return retObj;
    }
}
