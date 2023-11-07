package org.xxpay.pay.channel.wh;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.XXPayUtil;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.channel.xhpay.XhpayConfig;
import org.xxpay.pay.util.HttpClientUtils;
import org.xxpay.pay.util.Util;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WanhepayPaymentService extends BasePayment {
    private static final MyLog _log = MyLog.getLog(WanhepayPaymentService.class);

    @Override
    public String getChannelName() {
        return WanhepayConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject pay(PayOrder payOrder) {
        String channelId = payOrder.getChannelId();
        JSONObject retObj;
        switch (channelId) {
            case WanhepayConfig.CHANNEL_NAME_WXPAY_QR:
                retObj = doPayQrReq(payOrder, 10);
                break;
            case WanhepayConfig.CHANNEL_NAME_ALIPAY_QR:
                retObj = doPayQrReq(payOrder, 21);
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
    public JSONObject doPayQrReq(PayOrder payOrder, Integer channel) {
        WanhepayConfig wanHeConfig = new WanhepayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        String mchid = wanHeConfig.getAPP_ID();
        String apikey = wanHeConfig.getAPP_KEY();
        //--------------------
        try {
            Integer paytype = channel; // 支付类型
            Integer amount = Integer.parseInt(String.valueOf(payOrder.getAmount())); // 以分为单位的充值金额
            String orderid = payOrder.getPayOrderId(); // 20位订单号 时间戳+6位随机字符串组成
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            String ordertime = df.format(new Date());
            String callbackurl = payConfig.getReturnUrl(getChannelName());  // 回调地址
            String notifyurl = payConfig.getNotifyUrl(getChannelName());  // 通知地址
            String createip = payOrder.getClientIp();   // 	终端用户发起请求IP
            String body = payOrder.getBody();         // 商品描述
            String attach = payOrder.getParam1();     // 扩展参数

            String signTemp = "mchid="+mchid+"&paytype=" +paytype+"&amount="+amount+"&orderid="+orderid+"&ordertime="+ordertime+"&" + apikey;
            _log.info("签名加密前字符串：{}",signTemp);
            String pay_md5sign = Util.md5(signTemp);
            _log.info("签名加密后：{}",pay_md5sign);
            HashMap<String,String> param = new HashMap<String,String>();
            param.put("mchid", mchid);
            param.put("paytype", String.valueOf(paytype));
            param.put("amount", String.valueOf(amount));
            param.put("orderid", orderid);
            param.put("ordertime", ordertime);
            param.put("callbackurl", callbackurl);
            param.put("notifyurl", notifyurl);
            param.put("createip", createip);
            param.put("body", body);
            param.put("attach", attach);
            param.put("sign", pay_md5sign);
            String reqUrl = wanHeConfig.getReqUrl();
            _log.info("请求上游通道wanhe 参数：{}",param);
            String result = HttpClientUtils.post(reqUrl, param);
            _log.info("上游wanhe返回结果:{}",result);
            JSONObject res = JSONObject.parseObject(result);
            if (!"0".equals(String.valueOf(res.get("ResponseStatus")))) {
                _log.error(res.get("Response").toString(), "");
                retObj.put("errDes", "上游返回失败!");
                retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
                return retObj;
            }
            JSONObject resData = JSONObject.parseObject(res.get("Response").toString());
            JSONObject ressult= JSONObject.parseObject(resData.get("Result").toString());
            String payinfo = ressult.get("payinfo").toString().substring(0);
            String toPayUrl = payinfo.substring(0,payinfo.indexOf("?"));
            String Apikey=payinfo.substring(payinfo.indexOf("=")+1);
            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), payOrder.getPayOrderId(), updateCount);
            StringBuffer payForm = new StringBuffer();

            payForm.append("<form style=\"display: none\" action=\"" + toPayUrl + "\" method=\"get\">");
            payForm.append("<input name=\"Apikey\" value=\"" + Apikey + "\" >");
            payForm.append("</form>");
            payForm.append("<script>document.forms[0].submit();</script>");

            // 支付链接地址
            retObj.put("payOrderId", payOrder.getPayOrderId()); // 设置支付订单ID
            JSONObject payParams = new JSONObject();
            payParams.put("payUrl", payForm);
            String payJumpUrl = toPayUrl + "?Apikey=" + Apikey;
            payParams.put("payJumpUrl", payJumpUrl);
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



    public static void main(String[] args) throws Exception {
//        String reqUrl = "http://api.zsptv.com/pay/gateway";
//        String apikey = "vyHBVXTO5dAh9553hrD7Aj7dGTEnifGo";
//        String mchid = "2088424841777265";
//        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//        String ordertime = df.format(new Date().getTime());
//        int paytype = 21; // 支付类型
//        int amount = 200; // 支付金额
//        String orderid = SsfpayUtils.generateOrderId(); // 20位订单号 时间戳+6位随机字符串组成
//
//        String callbackurl = "www.baidu.com";  // 通知地址
//        String notifyurl = "www.baidu.com";  // 回调地址
//        String body = "11";  // 	订单说明
//        String createip = "14.155.18.58";   // 	终端用户发起请求IP
//        String attach = "attach";   // 	扩展参数
//        String signTemp = "mchid="+mchid+"&paytype=" +paytype+"&amount="+amount+"&orderid="+orderid+"&ordertime="+ordertime+"&" + apikey;
//
//       System.out.println("=====签名加密前字符串："+signTemp);
//        String pay_md5sign = Util.md5(signTemp);
//        System.out.println("-----签名md5加密后："+pay_md5sign);
//        HashMap<String,String> param = new HashMap<>();
//        param.put("mchid", mchid);
//        param.put("paytype", String.valueOf(paytype));
//        param.put("amount", String.valueOf(amount));
//        param.put("orderid", orderid);
//        param.put("ordertime", ordertime);
//        param.put("callbackurl", callbackurl);
//        param.put("notifyurl", notifyurl);
//        param.put("createip", createip);
//        param.put("body", body);
//        param.put("attach", attach);
//        param.put("sign", pay_md5sign);
//
//        System.out.println(param.toString());
////        String post = HttpClientUtils.post(AuthorizationURL, param);
////        String post = HttpClientUtils.postToJSON(reqUrl, JSONObject.toJSONString(param));
//        String post =  HttpClient.doPost(reqUrl,param, 20000, 2009);
//
//        System.out.println(post);

    }


}
