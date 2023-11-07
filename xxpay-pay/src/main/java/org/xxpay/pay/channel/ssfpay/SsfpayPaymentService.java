package org.xxpay.pay.channel.ssfpay;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.channel.sandpay.sdk.HttpClient;
import org.xxpay.pay.channel.ssfpay.utils.SsfpayUtils;
import org.xxpay.pay.mq.BaseNotify4MchPay;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: carter
 * @date: 2019-07-31
 * @description: 随手付接口
 */
@Service
public class SsfpayPaymentService extends BasePayment {

    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    private static final MyLog _log = MyLog.getLog(SsfpayPaymentService.class);
    
    //http连接超时时间
    public static int connectTimeout = 300000;
    //http响应超时时间
    public static int readTimeout = 600000;

    @Override
    public String getChannelName() {
        return SsfpayConfig.CHANNEL_NAME;
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
            case SsfpayConfig.CHANNEL_NAME_WXPAY_HF:
                retObj = doPay(payOrder, "wxpay_hf");
                break;
            case SsfpayConfig.CHANNEL_NAME_WXPAY_SC:
                retObj = doPay(payOrder, "wxpay_sc");
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
        SsfpayConfig ssfpayConfig = new SsfpayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();

        try {
            String payUrl = ssfpayConfig.getReqUrl();
            String key = ssfpayConfig.getAPP_KEY();//密钥
            String mch_id = ssfpayConfig.getMerchantId();//商户id
            String pay_type = "";//支付类型
            if(channel.equals("wxpay_hf")){
                pay_type = "101";
            }else if(channel.equals("wxpay_sc")){
                pay_type = "102";
            }
            String out_trade_no = payOrder.getPayOrderId();//20位订单号 时间戳+6位随机字符串组成
            String total_fee = payOrder.getAmount().toString();//下单价格，单位分
            String notify_url = payConfig.getNotifyUrl(getChannelName());//通知地址
            String ip = payOrder.getClientIp();
            String nonce_str = SsfpayUtils.generateOrderId();//随机字符串

            StringBuffer sb = new StringBuffer();
            sb.append("ip=" + ip);
            sb.append("&mch_id=" + mch_id);
            sb.append("&nonce_str=" + nonce_str);
            sb.append("&notify_url=" + notify_url);
            sb.append("&out_trade_no=" + out_trade_no);
            sb.append("&pay_type=" + pay_type);
            sb.append("&total_fee=" + total_fee);
            sb.append("&key=" + key);
            String sign = SsfpayUtils.md5(sb.toString()).toUpperCase();
            
            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(),
                    payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(),
                    payOrder.getPayOrderId(), updateCount);
            
            HashMap<String,String> mav= new HashMap<>();
            mav.put("ip", ip);
            mav.put("mch_id", mch_id);
            mav.put("nonce_str", nonce_str);
            mav.put("notify_url", notify_url);
            mav.put("out_trade_no", out_trade_no);
            mav.put("pay_type", pay_type);
            mav.put("total_fee", total_fee);
            mav.put("sign", sign);
            
            String result;
            try {
                _log.info("[随手付商户:{}]请求URL:{}, 报文：\n{}", mch_id, payUrl, mav.toString());
                result = HttpClient.doPost(payUrl, mav, connectTimeout, readTimeout);
                result = URLDecoder.decode(result, "UTF-8");
            } catch (IOException e) {
                _log.error(e.getMessage());
                return null;
            }


            JSONObject resObj = JSONObject.parseObject(result);
            if(!resObj.get("status").equals("200")) {
                _log.info("[随手付商户]下单返回失败信息:{}", resObj.get("message"));
                retObj.put("errDes", resObj.get("message"));
                retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
                return retObj;
            }
                String resSign = resObj.getString("sign");//下单返回的验签参数
                payUrl = resObj.getString("pay_url");

                //下单返回参数验签
                StringBuffer sbRes = new StringBuffer();
                sbRes.append("channel_id=" + resObj.getString("channel_id"));
                sbRes.append("&message=" + resObj.getString("message"));
                sbRes.append("&order_id=" + resObj.getString("order_id"));
                sbRes.append("&pay_url=" + resObj.getString("pay_url"));
                sbRes.append("&status=" + resObj.getString("status"));
                sbRes.append("&key=" + key);
                String validSign = SsfpayUtils.md5(sbRes.toString()).toUpperCase();
                if (!validSign.equals(resSign)) {
                    _log.info("[随手付商户:{}]下单返回参数，验签失败", validSign, resSign);
                    retObj.put("retMsg", "随手付商户下单返回参数验签失败!");
                    return retObj;
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
        /*
        String AuthorizationURL = "http://39.108.230.242/lh_pay/pay";
        String key = "6217a447791d5120ad06720b7fd94ef5";
        String pay_type = "101";
        String mch_id = "S10003101";//商户id
        String out_trade_no = LtUtils.generateOrderId();//20位订单号 时间戳+6位随机字符串组成
        String notify_url = "http://www.baidu.com";//通知地址
        String total_fee = "100";
        String nonce_str = SsfpayUtils.generateOrderId();//随机字符串
        String ip = "192.168.1.1";
        
        StringBuffer sb = new StringBuffer();
        sb.append("ip=" + ip);
        sb.append("&mch_id=" + mch_id);
        sb.append("&nonce_str=" + nonce_str);
        sb.append("&notify_url=" + notify_url);
        sb.append("&out_trade_no=" + out_trade_no);
        sb.append("&pay_type=" + pay_type);
        sb.append("&total_fee=" + total_fee);
        sb.append("&key=" + key);
        String sign = SsfpayUtils.md5(sb.toString()).toUpperCase();
        
        System.out.println( "----------------------------: "+sb.toString());
        System.out.println( "----------------------------: "+sign);
        
        HashMap<String,String> mav= new HashMap<>();
        mav.put("ip", ip);
        mav.put("mch_id", mch_id);
        mav.put("nonce_str", nonce_str);
        mav.put("notify_url", notify_url);
        mav.put("out_trade_no", out_trade_no);
        mav.put("pay_type", pay_type);
        mav.put("total_fee", total_fee);
        mav.put("sign", sign);
        
        System.out.println(mav.toString());
        HttpClientUtils.post(AuthorizationURL,mav);
        */
        
        
        //下单返回参数验签
        StringBuffer sb = new StringBuffer();
        sb.append("channel_id=" + "101008");
        sb.append("&message=" + "success");
        sb.append("&order_id=" + "08021751018645815");
        sb.append("&pay_url=" + "http://39.108.230.242/urlProject/order_way.html?b3JkZXJVcmw9aHR0cDovL3d3dy44Nmh6Zi5jb20vUGF5X0luZGV4Lmh0bWwmcGF5X2Ftb3VudD0xMC4wMCZwYXlfYXBwbHlkYXRlPTIwMTktMDgtMDIgMTc6NTE6MDEmcGF5X2Jhbmtjb2RlPTkwMSZwYXlfbWQ1c2lnbj0zNjMzNjYxMTBGOUU3RkNENUI5QkYwNTc0RDY0RjY2NCZwYXlfbWVtYmVyaWQ9MTkwNzc5NDE4JnBheV9ub3RpZnl1cmw9aHR0cDovLzM5LjEwOC4yMzAuMjQyL2xoX25vdGljZS9iYWNrdXJsNDcmcGF5X29yZGVyaWQ9MDgwMjE3NTEwMTg2NDU4MTU=");
        sb.append("&status=" + "200");
        sb.append("&key=" + "6217a447791d5120ad06720b7fd94ef5");
        String sign = SsfpayUtils.md5(sb.toString()).toUpperCase();
        System.out.println(sign);
    }

}
