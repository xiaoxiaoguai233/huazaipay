package org.xxpay.pay.channel.zhlmobliepay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.RSAUtil;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.channel.sandpay.sdk.HttpClient;
import org.xxpay.pay.channel.ssfpay.utils.SsfpayUtils;
import org.xxpay.pay.util.HttpClientUtils;
import org.xxpay.pay.util.Util;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

@Service
public class ZhlpayPaymentService extends BasePayment {
    private static final MyLog _log = MyLog.getLog(ZhlpayPaymentService.class);

    @Override
    public String getChannelName() {
        return ZhlpayConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject pay(PayOrder payOrder) {
        String channelId = payOrder.getChannelId();
        JSONObject retObj;
        switch (channelId) {
            case ZhlpayConfig.CHANNEL_NAME_WXPAY_QR:
                retObj = doPayQrReq(payOrder);
                break;
            case ZhlpayConfig.CHANNEL_NAME_ALIPAY_QR:
                retObj = doPayQrReq(payOrder);
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
    public JSONObject doPayQrReq(PayOrder payOrder) {
        ZhlpayConfig wanHeConfig = new ZhlpayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        String appid = wanHeConfig.getAPP_ID();
        String apikey = wanHeConfig.getAPP_KEY();
        String publicKey=wanHeConfig.getPublicKey();
        String reqUrl = wanHeConfig.getReqUrl(); // "http://trans.palmf.cn/sdk/api/v1.0/cli/order_h5/0"
        //--------------------
        try {
            Long amount = payOrder.getAmount(); // 支付金额
            String subject = "tongda";
            String mchntOrderNo = payOrder.getPayOrderId(); // 20位订单号 时间戳+6位随机字符串组成
            String version = "h5_NoEncrypt_js"; // 版本号
            String clientIp = payOrder.getClientIp(); // ip
            String notifyUrl = payConfig.getNotifyUrl(getChannelName());  // 通知地址
            String returnUrl = payConfig.getReturnUrl(getChannelName());  // 回调地址
            TreeMap<String,Object> param = new TreeMap<>();
            param.put("amount", amount);
            param.put("appid", appid);
            param.put("subject", subject);
            param.put("mchntOrderNo", mchntOrderNo);
            param.put("version", version);
            param.put("clientIp", clientIp);
            param.put("returnUrl", returnUrl);
            param.put("notifyUrl", notifyUrl);

            String signTemp = doSignString(param)+"key="+apikey;
            _log.info("签名加密前字符串：{}",signTemp);
            String pay_md5sign = Util.md5(signTemp);
            _log.info("签名加密后：{}",pay_md5sign);
            param.put("signature",pay_md5sign);
            _log.info("请求上游通道wanhe 参数：{}",param);
            String  jsonObject= JSONObject.toJSONString(param);
            _log.info("加密前reqParams={}",jsonObject);
            String orderInfo = Base64.encodeBase64String(RSAUtil.encryptByPublicKeyByPKCS1Padding(jsonObject.getBytes("utf-8"), publicKey));

            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), payOrder.getPayOrderId(), updateCount);
            StringBuffer payForm = new StringBuffer();

            payForm.append("<form style=\"display: none\" action=\"" + reqUrl + "\" method=\"post\">");
            payForm.append("<input type=\"text\"  name=\"orderInfo\" value=\"" + orderInfo + "\" >");
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


    public static String doSignString(TreeMap<String, Object> map) {
        Set keySet = map.keySet();
        Iterator it = keySet.iterator();
        StringBuilder strTemp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next().toString();
            if (StringUtils.isNotBlank(String.valueOf(map.get(key))))
                strTemp.append(key + "=" + map.get(key) + "&");
        }
        return strTemp.toString();
    }


    public static void main(String[] args) throws Exception {
//        String reqUrl = "http://trans.palmf.cn/sdk/api/v1.0/cli/order_h5/0";
//        String apikey = "04c9ef9f6ca184811714dcc456a0c01f";
//        int amount = 200; // 支付金额
//        String appid = "0000003947";
//        String Subject = "tongda";
//        String mchntOrderNo = SsfpayUtils.generateOrderId(); // 20位订单号 时间戳+6位随机字符串组成
//        String version = "h5_NoEncrypt_js"; // 版本号
//        String clientIp = "192.168.1.10"; // ip
//
//
//        String returnUrl = "www.baidu.com";  // 通知地址
//        String notifyUrl = "www.baidu.com";  // 回调地址
//
//        TreeMap<String,Object> param = new TreeMap<>();
//        param.put("amount", amount);
//        param.put("appid", appid);
//        param.put("Subject", Subject);
//        param.put("mchntOrderNo", mchntOrderNo);
//        param.put("version", version);
//        param.put("clientIp", clientIp);
//        param.put("returnUrl", returnUrl);
//        param.put("notifyUrl", notifyUrl);
//        String signTemp = doSignString(param)+"key="+apikey;
//
//        System.out.println("=====签名加密前字符串："+signTemp);
//        String pay_md5sign = Util.md5(signTemp);
//        System.out.println("-----签名md5加密后："+pay_md5sign);
//
//        param.put("signature", pay_md5sign);
//
//        System.out.println(param.toString());
//
//        String orderInfo = null;
//        StringBuffer payForm = new StringBuffer();
//        payForm.append("<form style=\"display: none\" action=\"" + reqUrl + "\" method=\"post\">");
//        payForm.append("<input type=\"text\"  name=\"orderInfo\" value=\"" + orderInfo + "\" >");
//        payForm.append("</form>");
//        payForm.append("<script>document.forms[0].submit();</script>");

    }


}
