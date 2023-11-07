package org.xxpay.pay.channel.ltpay;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.util.HttpClientUtils;
import org.xxpay.pay.channel.ltpay.utils.LtUtils;
import org.xxpay.pay.mq.BaseNotify4MchPay;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: carter
 * @date: 2019-07-28
 * @description: 龙腾支付接口
 */
@Service
public class LtpayPaymentService extends BasePayment {

    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    private static final MyLog _log = MyLog.getLog(LtpayPaymentService.class);

    @Override
    public String getChannelName() {
        return LtpayConfig.CHANNEL_NAME;
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
            case LtpayConfig.CHANNEL_NAME_WXPAY_QR:
                retObj = doPay(payOrder, "wxpay");
                break;
            case LtpayConfig.CHANNEL_NAME_ALIPAY_QR:
                retObj = doPay(payOrder, "alipay");
                break;
            default:
                retObj = buildRetObj(PayConstant.RETURN_VALUE_FAIL, "不支持的渠道[channelId=" + channelId + "]");
                break;
        }
        return retObj;
    }

    /**
     * 扫码支付
     * 
     * @param payOrder
     * @return
     */
    public JSONObject doPay(PayOrder payOrder, String channel) {
        LtpayConfig ltpayConfig = new LtpayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();

        try {

            String payUrl = ltpayConfig.getReqUrl();
            String key = ltpayConfig.getAPP_KEY();
            String pay_bankcode = "";
            if(channel.equals("wxpay")){
                pay_bankcode = "902";
            }else if(channel.equals("alipay")){
                pay_bankcode = "903";
            }
            String pay_memberid = ltpayConfig.getMerchantId();//商户id
            String pay_orderid = payOrder.getPayOrderId();//20位订单号 时间戳+6位随机字符串组成
            String pay_applydate = LtUtils.generateTime();//yyyy-MM-dd HH:mm:ss
            String pay_notifyurl = payConfig.getNotifyUrl(getChannelName());//通知地址
            String pay_callbackurl = payConfig.getNotifyUrl(getChannelName());//回调地址
            DecimalFormat df = new DecimalFormat("######0.00");
            String pay_amount = df.format(payOrder.getAmount().doubleValue() / 100);
            String pay_productname = payOrder.getSubject();//标题

            StringBuffer sb = new StringBuffer();
            sb.append("pay_amount=" + pay_amount);
            sb.append("&pay_applydate=" + pay_applydate);
            sb.append("&pay_bankcode=" + pay_bankcode);
            sb.append("&pay_callbackurl=" + pay_callbackurl);
            sb.append("&pay_memberid=" + pay_memberid);
            sb.append("&pay_notifyurl=" + pay_notifyurl);
            sb.append("&pay_orderid=" + pay_orderid);
            sb.append("&key=" + key);
            String pay_md5sign = LtUtils.md5(sb.toString()).toUpperCase();
            
            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(),
                    payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(),
                    payOrder.getPayOrderId(), updateCount);
            
            StringBuffer payForm = new StringBuffer();
            payForm.append("<form style=\"display: none\" action=\"" + payUrl + "\" method=\"post\">");
            payForm.append("<input name=\"pay_amount\" value=\"" + pay_amount + "\" >");
            payForm.append("<input name=\"pay_memberid\" value=\"" + pay_memberid + "\" >");
            payForm.append("<input name=\"pay_notifyurl\" value=\"" + pay_notifyurl + "\" >");
            payForm.append("<input name=\"pay_orderid\" value=\"" + pay_orderid + "\" >");
            payForm.append("<input name=\"pay_callbackurl\" value=\"" + pay_callbackurl + "\" >");
            payForm.append("<input name=\"pay_applydate\" value=\"" + pay_applydate + "\" >");
            payForm.append("<input name=\"pay_md5sign\" value=\"" + pay_md5sign + "\" >");
            payForm.append("<input name=\"pay_bankcode\" value=\"" + pay_bankcode + "\" >");
            payForm.append("<input name=\"pay_productname\" value=\"" + pay_productname + "\" >");
            payForm.append("<input name=\"key\" value=\"" + key + "\" >");
            payForm.append("<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >");
            payForm.append("</form>");
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
        String AuthorizationURL = "http://www.gaugefun.com/Pay_Index.html";
        String key = "jgci8w5lq5p6hd9k9iw0hwik0dvqvx1e";
        String payType = "alipay";
        String pay_bankcode = "903";
        String pay_memberid = "10230";//商户id
        String pay_orderid = LtUtils.generateOrderId();//20位订单号 时间戳+6位随机字符串组成
        String pay_applydate = LtUtils.generateTime();//yyyy-MM-dd HH:mm:ss
        String pay_notifyurl = "www.baidu.com";//通知地址
        String pay_callbackurl = "www.baidu.com";//回调地址
        String pay_amount = "100";
        String pay_productname = "测试商品";
        
        StringBuffer sb = new StringBuffer();
        sb.append("pay_amount=" + pay_amount);
        sb.append("&pay_applydate=" + pay_applydate);
        sb.append("&pay_bankcode=" + pay_bankcode);
        sb.append("&pay_callbackurl=" + pay_callbackurl);
        sb.append("&pay_memberid=" + pay_memberid);
        sb.append("&pay_notifyurl=" + pay_notifyurl);
        sb.append("&pay_orderid=" + pay_orderid);
        sb.append("&key=" + key);
        String pay_md5sign = LtUtils.md5(sb.toString()).toUpperCase();
        System.out.println( "----------------------------: "+sb.toString());
        System.out.println( "----------------------------: "+pay_md5sign);
        
        HashMap<String,String> mav= new HashMap<>();
        mav.put("pay_amount", pay_amount);
        mav.put("pay_memberid", pay_memberid);
        mav.put("pay_notifyurl", pay_notifyurl);
        mav.put("pay_orderid", pay_orderid);
        mav.put("pay_callbackurl", pay_callbackurl);
        mav.put("pay_applydate", pay_applydate);
        mav.put("pay_md5sign", pay_md5sign);
        mav.put("pay_bankcode", pay_bankcode);
        mav.put("pay_productname", pay_productname);
        System.out.println(mav.toString());
        HttpClientUtils.post(AuthorizationURL,mav);
        

    }

}
