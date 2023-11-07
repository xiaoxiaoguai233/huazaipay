package org.xxpay.pay.channel.yixunpay;

import java.security.NoSuchAlgorithmException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.channel.yixunpay.utils.RequestUtil;
import org.xxpay.pay.channel.yixunpay.utils.SignUtils;
import org.xxpay.pay.channel.yixunpay.utils.YixunpayUtils;
import org.xxpay.pay.mq.BaseNotify4MchPay;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: carter
 * @date: 2019-07-31
 * @description: 易迅接口
 */
@Service
public class YixunpayPaymentService extends BasePayment {

    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    private static final MyLog _log = MyLog.getLog(YixunpayPaymentService.class);

    // http连接超时时间
    public static int connectTimeout = 300000;
    // http响应超时时间
    public static int readTimeout = 600000;

    @Override
    public String getChannelName() {
        return YixunpayConfig.CHANNEL_NAME;
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
            case YixunpayConfig.CHANNEL_NAME_YINLIAN_KJ:
                retObj = doPay(payOrder, "pay_kuaijie");
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
    public JSONObject doPay(PayOrder payOrder, String channel) {
        YixunpayConfig yixunpayConfig = new YixunpayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();

        try {

            String payUrl = yixunpayConfig.getReqUrl();
            String key = yixunpayConfig.getAPP_KEY();// 密钥
            String merchantId = yixunpayConfig.getMerchantId();// 商户id
            String bizCode = "";// 支付类型
            if (channel.equals("pay_kuaijie")) {
                bizCode = "5001";
            }
            String orderId = payOrder.getPayOrderId();// 20位订单号 时间戳+6位随机字符串组成
            String version = YixunpayConfig.VERSION;// 固定
            String orderAmt = payOrder.getAmount().toString();// 下单价格，单位分
            String returnUrl = payOrder.getReturnUrl();
            String bgUrl = payConfig.getNotifyUrl(getChannelName());// 通知地址
            String terminalIp = payOrder.getClientIp();
            String productName = payOrder.getSubject();// 商品标题
            String productDes = payOrder.getSubject();// 商品描述

            SortedMap<String, String> sortMap = new TreeMap<String, String>();
            sortMap.put("merchantId", merchantId);
            sortMap.put("orderId", orderId);
            sortMap.put("version", version);
            sortMap.put("orderAmt", orderAmt);
            sortMap.put("bizCode", bizCode);
            sortMap.put("returnUrl", returnUrl);
            sortMap.put("bgUrl", bgUrl);
            sortMap.put("terminalIp", terminalIp);
            sortMap.put("productName", productName);
            sortMap.put("productDes", productDes);
            String sign = RequestUtil.mapToStringAndTrim(sortMap) + "&key=" + key;
            try {
                sign = SignUtils.md5Encode(sign, "utf-8").toUpperCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(),
                    payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(),
                    payOrder.getPayOrderId(), updateCount);
            
            sortMap.put("sign", sign);
            String retMsg = RequestUtil.sendPost(payUrl, RequestUtil.mapToStringAndTrim(sortMap), "utf-8");
            System.out.println(retMsg);
            JSONObject retJson = JSONObject.parseObject(retMsg);
            
            SortedMap<String, String> retMap = new TreeMap<String, String>();
            YixunpayUtils.JsonToMap(retJson, retMap);

            if ("00".equals(retMap.get("rspCode"))) {
                String verSign = retMap.get("sign");
                retMap.remove("sign");
                try {
                    if (verSign.equals(SignUtils.md5Encode(RequestUtil.mapToStringAndTrim(retMap) + "&key=" + key, "utf-8")
                            .toUpperCase())) {
                        System.out.println("验证签名成功");
                        System.out.println("用url进行操作:" + retMap.get("payUrl"));
                        payUrl = retMap.get("payUrl");
                    } else {
                        System.out.println("验证签名失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            StringBuffer payForm = new StringBuffer();
            payForm.append("<form style=\"display: none\" action=\"" + payUrl + "\" method=\"post\"></form>");
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

    public static void main(String[] agrs) throws NoSuchAlgorithmException {
        SortedMap<String, String> map = new TreeMap<String, String>();
        String url = "http://47.94.4.210/trade_api/order/pay.do";
        map.put("merchantId", "010190729142939807942");
        map.put("orderId", RequestUtil.getRandomString(10));
        map.put("version", "0.0.1");
        map.put("orderAmt", "100");
        map.put("bizCode", "5001");
        map.put("returnUrl", "http://www.baidu.com");
        map.put("bgUrl", "http://www.baidu.com");
        // map.put("accountNo","卡号");
        // map.put("bankCode","ABC");
        // map.put("cardType","0");
        map.put("terminalIp", "127.0.0.1");
        map.put("productName", "商品名称");
        map.put("productDes", "商品描述");
        String key = "jDTxfB3Bs2x8khzySIjy";

        String sign = RequestUtil.mapToStringAndTrim(map) + "&key=" + key;
        try {
            sign = SignUtils.md5Encode(sign, "utf-8").toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("sign", sign);
        String retMsg = RequestUtil.sendPost(url, RequestUtil.mapToStringAndTrim(map), "utf-8");
        System.out.println(retMsg);
        JSONObject json = JSONObject.parseObject(retMsg);
        // SortedMap<String, String> retMap = new TreeMap<String, String>();
        // retMap.putAll(json);

        SortedMap<String, String> retMap = new TreeMap<String, String>();
        YixunpayUtils.JsonToMap(json, retMap);

        if ("00".equals(retMap.get("rspCode"))) {
            String verSign = retMap.get("sign");
            retMap.remove("sign");
            try {
                if (verSign.equals(SignUtils.md5Encode(RequestUtil.mapToStringAndTrim(retMap) + "&key=" + key, "utf-8")
                        .toUpperCase())) {
                    System.out.println("验证签名成功");
                    System.out.println("用url进行操作:" + retMap.get("payUrl"));
                } else {
                    System.out.println("验证签名失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
