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
import org.xxpay.pay.util.mth.MD5Util;

import java.util.Date;
import java.util.Random;

// 圣鑫话费
@Service
public class    SxhfPaymentService  extends BasePayment {

    private static final MyLog _log = MyLog.getLog(SjyhPaymentService.class);
    public final static String PAY_CHANNEL_SXHF_ALIPAY_SC = "sxhf_alipay_sc";                // 支付宝当面付之H5支付

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
            case PAY_CHANNEL_SXHF_ALIPAY_SC :                  // 支付宝APP - SLRJ
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

        String logPrefix = "【8027-圣鑫话费-8027】";

        _log.info("{} 生成订单： ", logPrefix, payOrder.getPayOrderId());

        JSONObject retObj = new JSONObject();
        // formJump payUrl
        // urlJump  payUrl
        // codeImg  codeImgUrl
        // 填充支付链接到相关的app
        retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);

        // 请求接口
        String ApiKey = "47c80c89c7a59279ca4faa431e8028b1";
        String mchid = "9000041";
        String mch_order_id = payOrder.getPayOrderId();
        String price = String.valueOf(payOrder.getAmount() / 100);
        String paytype = "5";
        String notify = "http://pay.astropay99.top/api/pay_notify/sxhf";
        String time = String.valueOf(System.currentTimeMillis());
        String rand = String.valueOf(new Random().nextInt(900000) + 100000);

        String sign = MD5Util.encrypt(mchid + mch_order_id + price + paytype + notify + time + rand + ApiKey);

        // 获取支付链接
        String body = "";
        try {
            body = Jsoup.connect("http://45.207.52.119/api/pay")
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .timeout(20000)
                    .data("mchid", mchid)
                    .data("mch_order_id", mch_order_id)
                    .data("price", price)
                    .data("paytype", paytype)
                    .data("notify", notify)
                    .data("time", time)
                    .data("rand", rand)
                    .data("sign", sign)
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

        if (json.getInteger("code") != 0){
            retObj = buildRetObj(PayConstant.RETURN_VALUE_FAIL, "暂无可用订单");
            return retObj;
        }

        json = JSON.parseObject(json.getString("data"));
        String JumpUrl = json.getString("url");

        JSONObject payParams = new JSONObject();
        payParams.put("payMethod", "urlJump");
        payParams.put("payUrl", JumpUrl);

        retObj.put("payParams",payParams );

        // 修改状态为支付中
        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), null);

        return retObj;
    }
}
