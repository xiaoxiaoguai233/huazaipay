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
import org.xxpay.pay.util.BfjSignUtil;
import org.xxpay.pay.util.mth.MD5Util;
@Service
public class BfjPaymentService extends BasePayment {

    private static final MyLog _log = MyLog.getLog(SjyhPaymentService.class);
    public final static String PAY_CHANNEL_BFJ_ALIPAY_SC = "bfj_alipay_sc";                // 支付宝当面付之备付金

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
            case PAY_CHANNEL_BFJ_ALIPAY_SC :                  // 支付宝APP - SLRJ
                retObj = doAliPaySCBFJReq(payOrder);
                break;
        }
        return retObj;
    }

    /**
     * 支付宝APP支付-备付金
     * @param payOrder
     * @return
     */
    public JSONObject doAliPaySCBFJReq(PayOrder payOrder) throws Exception {

        String logPrefix = "【8027-备付金-8027】";

        _log.info("{} 生成订单： ", logPrefix, payOrder.getPayOrderId());

        JSONObject retObj = new JSONObject();
        // formJump payUrl
        // urlJump  payUrl
        // codeImg  codeImgUrl
        // 填充支付链接到相关的app
        retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);


        // 请求接口
        String ApiKey = "k1bPDN33v8E674985u71V35UFNQ7NVi95s0Zt9wKBO4u";
        String MerchantId = "92672";
        String Amount = String.valueOf(payOrder.getAmount() / 100);

        String BankCardBankName = "中国建设银行";
        String BankCardNumber = "622212345678901234";
        String BankCardRealName = "张三";


        String ClientRealName = "name";
        String Ip = payOrder.getClientIp();
        String MerchantUniqueOrderId = payOrder.getPayOrderId();
        String NotifyUrl = "http://pay.dev6688.com/api/pay_notify/bfj";
        String PayTypeId = "zfb";
        String PayTypeIdFormat = "URL";
        String Remark = "Remark";

        JSONObject param_json = new JSONObject();
        param_json.put("Amount", Amount);

        param_json.put("BankCardBankName", BankCardBankName);
        param_json.put("BankCardNumber", BankCardNumber);
        param_json.put("BankCardRealName", BankCardRealName);

        param_json.put("MerchantId", MerchantId);
        param_json.put("MerchantUniqueOrderId", MerchantUniqueOrderId);
        param_json.put("NotifyUrl", NotifyUrl);
        param_json.put("WithdrawTypeId", "0");
        param_json.put("Remark", Remark);

        String sign = BfjSignUtil.getSign(param_json, ApiKey);

        param_json.put("Sign", sign);


        // 获取支付链接
        String body = "";
        try {
            body = Jsoup.connect("http://api6fftk65nc1epf3p3.zxwee.xyz/InterfaceV9/CreateWithdrawOrder/")
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .timeout(20000)
                    .data("Amount", Amount)
                    .data("BankCardBankName", BankCardBankName)
                    .data("BankCardNumber", BankCardNumber)
                    .data("BankCardRealName", BankCardRealName)
                    .data("MerchantId", MerchantId)
                    .data("MerchantUniqueOrderId", MerchantUniqueOrderId)
                    .data("NotifyUrl", NotifyUrl)
                    .data("WithdrawTypeId", "0")
                    .data("Remark", Remark)
                    .data("Sign", sign)
                    .ignoreContentType(true)
                    .execute().body();
        }catch (Exception e){
            // 释放当前号码
            e.printStackTrace();
            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "获取支付链接失败，请联系相关技术人员1001");
            return retObj;
        }
        JSONObject json = JSONObject.parseObject(body);
//        json = JSON.parseObject(json.getString("data"));
        _log.info("=== 拉起支付返回结果： {} ===", json);

        // {"Message":"提交成功，请等待处理完成后的回调或调用查询接口查询状态","WithdrawOrderStatus":"0","Code":"0"}
        String JumpUrl = json.getString("Url");


        JSONObject payParams = new JSONObject();
        payParams.put("payMethod", "urlJump");
        payParams.put("payUrl", JumpUrl);

        retObj.put("payParams",payParams);


        // 修改状态为支付中
        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), null);

        return retObj;
    }

//    /**
//     * 支付宝APP支付-备付金
//     * @param payOrder
//     * @return
//     */
//    public JSONObject doAliPaySCBFJReq(PayOrder payOrder) throws Exception {
//
//        String logPrefix = "【8027-备付金-8027】";
//
//        _log.info("{} 生成订单： ", logPrefix, payOrder.getPayOrderId());
//
//        JSONObject retObj = new JSONObject();
//        // formJump payUrl
//        // urlJump  payUrl
//        // codeImg  codeImgUrl
//        // 填充支付链接到相关的app
//        retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);
//
//
//        // 请求接口
//        String ApiKey = "k1bPDN33v8E674985u71V35UFNQ7NVi95s0Zt9wKBO4u";
//        String MerchantId = "92672";
//        String Amount = String.valueOf(payOrder.getAmount() / 100);
//        String ClientRealName = "name";
//        String Ip = payOrder.getClientIp();
//        String MerchantUniqueOrderId = payOrder.getPayOrderId();
//        String NotifyUrl = "http://pay.dev6688.com/api/pay_notify/bfj";
//        String PayTypeId = "zfb";
//        String PayTypeIdFormat = "URL";
//        String Remark = "remark";
//
//        JSONObject param_json = new JSONObject();
//        param_json.put("Amount", Amount);
//        param_json.put("ClientRealName", ClientRealName);
//        param_json.put("Ip", Ip);
//        param_json.put("MerchantId", MerchantId);
//        param_json.put("MerchantUniqueOrderId", MerchantUniqueOrderId);
//        param_json.put("NotifyUrl", NotifyUrl);
//        param_json.put("PayTypeId", PayTypeId);
//        param_json.put("PayTypeIdFormat", PayTypeIdFormat);
//        param_json.put("Remark", Remark);
//
//        String sign = BfjSignUtil.getSign(param_json, ApiKey);
//
//        param_json.put("Sign", sign);
//
//
//        // 获取支付链接
//        String body = "";
//        try {
//            body = Jsoup.connect("http://api6fftk65nc1epf3p3.zxwee.xyz/InterfaceV9/CreateWithdrawOrder/")
//                    .method(Connection.Method.POST)
//                    .header("Content-Type", "application/x-www-form-urlencoded")
//                    .timeout(20000)
//                    .data("Amount", Amount)
//                    .data("ClientRealName", ClientRealName)
//                    .data("Ip", Ip)
//                    .data("MerchantId", MerchantId)
//                    .data("MerchantUniqueOrderId", MerchantUniqueOrderId)
//                    .data("NotifyUrl", NotifyUrl)
//                    .data("PayTypeId", PayTypeId)
//                    .data("PayTypeIdFormat", PayTypeIdFormat)
//                    .data("Remark", Remark)
//                    .data("Sign", sign)
//                    .ignoreContentType(true)
//                    .execute().body();
//        }catch (Exception e){
//            // 释放当前号码
//            e.printStackTrace();
//            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "获取支付链接失败，请联系相关技术人员1001");
//            return retObj;
//        }
//        JSONObject json = JSONObject.parseObject(body);
////        json = JSON.parseObject(json.getString("data"));
//        _log.info("=== 拉起支付返回结果： {} ===", json);
//        String JumpUrl = json.getString("Url");
//
//
//        JSONObject payParams = new JSONObject();
//        payParams.put("payMethod", "urlJump");
//        payParams.put("payUrl", JumpUrl);
//
//        retObj.put("payParams",payParams );
//
//
//        // 修改状态为支付中
//        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), null);
//
//        return retObj;
//    }
}

