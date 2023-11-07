package org.xxpay.pay.channel.xcxpay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.HttpClientUtil;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.XXPayUtil;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.channel.sandpay.sdk.HttpClient;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.util.Util;

import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class XincxPaymentService  extends BasePayment {


    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    private static final MyLog _log = MyLog.getLog(XincxPaymentService.class);

    @Override
    public String getChannelName() {
        return XincxConfig.CHANNEL_NAME;
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
            case XincxConfig.CHANNEL_NAME_WXPAY_QR:
                retObj = doPayQrReq(payOrder, "wechat");
                break;
            case XincxConfig.CHANNEL_NAME_ALIPAY_QR:
                retObj = doPayQrReq(payOrder, "alipay");
                break;
            default:
                retObj = buildRetObj(PayConstant.RETURN_VALUE_FAIL, "不支持的渠道[channelId=" + channelId + "]");
                break;
        }
        return retObj;
    }

    /**
     * 查询订单
     *
     * @param payOrder
     * @return
     */
    @Override
    public JSONObject query(PayOrder payOrder) {

        String logPrefix = "【新诚信订单查询】";
        String payOrderId = payOrder.getPayOrderId();

        _log.info("{}开始查询新诚信订单,payOrderId={}", logPrefix, payOrderId);
        XincxConfig xincxConfig = new XincxConfig(getPayParam(payOrder));
        JSONObject paramMap = new JSONObject();
        paramMap.put("mch_id", xincxConfig.getAPP_ID());
        paramMap.put("order_sn", payOrderId);
//        paramMap.put("action", "orderquery");    // 支付中
//        String _sign = DigestUtils.md5Hex(xincxConfig.getAPP_ID()+payOrderId + "orderquery"+xincxConfig.getAPP_KEY());
//        paramMap.put("sign", _sign);
        JSONObject retObj = buildRetObj();
        Integer status = 0;
        String status_text = "";

        try {
            String reqData = XXPayUtil.genUrlParams(paramMap);
            _log.info("{}请求数据:{}", logPrefix, reqData);
            String url = "http://pay.longdaxi.com/api/orders/query.html";
            String result = XXPayUtil.call4Post(url + "?" + reqData);
            _log.info("{}返回数据:{}", logPrefix, result);
            JSONObject resObj = JSONObject.parseObject(result);
            status = resObj.getInteger("status");
            status_text = resObj.getString("status_text");
            if (status == 2) {
                retObj.put("status", 2);    // 成功
            } else if (0 == status) {
                retObj.put("status", 1);    // 支付中
            } else {
                retObj.put("message", status_text);
            }
        } catch (Exception e) {
            _log.error(e, "");
        }

        return retObj;
    }

    /**
     * @param payOrder
     * @return
     */
    public JSONObject doPayQrReq(PayOrder payOrder, String channel) {
        XincxConfig xincxConfig = new XincxConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        String merchantId = xincxConfig.getAPP_ID();
        String keyValue = xincxConfig.getAPP_KEY();

        //--------------------
        try {
            String pay_orderid = payOrder.getPayOrderId();//20位订单号 时间戳+6位随机字符串组成
            String pay_notifyurl = payConfig.getNotifyUrl(getChannelName());;//通知地址
            String pay_callbackurl = payConfig.getNotifyUrl(getChannelName());;//回调地址
            DecimalFormat df   = new DecimalFormat("######0.00");
            String pay_amount = df.format(payOrder.getAmount().doubleValue()/100);;
            String stringSignTemp = "extend="+"1"+"&mch_id="+merchantId+"&notifyurl="+pay_notifyurl+"&out_order_id="+pay_orderid+"&price="+pay_amount+"&returnurl="+pay_callbackurl+"&type="+channel+"&key="+keyValue;;
            String pay_md5sign = Util.md5(stringSignTemp);
            HashMap<String,String> param= new HashMap<>();
            param.put("mch_id",merchantId);
            param.put("out_order_id",pay_orderid);
            param.put("type",channel);
            param.put("notifyurl",pay_notifyurl);
            param.put("returnurl",pay_callbackurl);
            param.put("price",pay_amount);
            param.put("extend","1");
            param.put("sign",pay_md5sign);
//            String reqData = XXPayUtil.genUrlParams(param);
            String toPayUrl = xincxConfig.getReqUrl();

            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), payOrder.getPayOrderId(), updateCount);
            String result = HttpClient.doPost(toPayUrl, param, 2000, 20009);
            result = URLDecoder.decode(result, "UTF-8");

            JSONObject resObj = JSONObject.parseObject(result);
            Integer resCode = resObj.getInteger("code");
            _log.info("===============新诚信返回结果："+result);
            if(resCode != 1){
                retObj.put("errDes", resObj.getString("msg"));
                resObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
                return  resObj;
            }
            JSONObject data = resObj.getJSONObject("data");
            String payUrl = data.getString("pay_url");
            StringBuffer payForm = new StringBuffer();
            payForm.append("<form style=\"display: none\" action=\"" + payUrl + "\" method=\"post\"></form>");
            payForm.append("<script>document.forms[0].submit();</script>");
            // 支付链接地址
            retObj.put("payOrderId", payOrder.getPayOrderId()); // 设置支付订单ID
            JSONObject payParams = new JSONObject();
//            String payJumpUrl = toPayUrl + "?" + reqData;
            payParams.put("payUrl", payForm);
            payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);
            retObj.put("payParams", payParams);
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);
            return retObj;
//
        } catch (Exception e) {
            _log.error(e, "");
            retObj.put("errDes", "操作失败!");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
    }


    public static void main(String[] agrs) throws Exception {

//        String apiurl = "http://pay.longdaxi.com/api/orders/index.html"; // API下单地址
//        String key = "978HFryT6UI1hZpK"; // 商户密钥
//        Map<String, String> parameterMap = new HashMap<String, String>();
//        parameterMap.put("mch_id", "306"); // 商户号
//        parameterMap.put("type", "wechat"); // 支付类型
//        parameterMap.put("price", "100"); // 金额
//        parameterMap.put("out_order_id", Util.generateOrderId()); // 商户号
//        parameterMap.put("notifyurl", "www.baidu.com"); // 异步通知地址
//        parameterMap.put("returnurl","www.baidu.com"); // 同步通知地址
//        parameterMap.put("extend", "312|xxx"); // 附加数据
//        String stringSignTemp = "extend="+parameterMap.get("extend")+"&mch_id="+parameterMap.get("mch_id")+"&notifyurl="+parameterMap.get("notifyurl")+"&out_order_id="+parameterMap.get("out_order_id")+"&price="+parameterMap.get("price")+"&returnurl="+parameterMap.get("returnurl")+"&type="+parameterMap.get("type")+"&key="+key;
//        parameterMap.put("sign", Util.md5(stringSignTemp).toUpperCase()); // 附加数据
//        String jsonString = HttpClient.doPost(apiurl,parameterMap,2000,2000);
//        JSONObject res = JSONObject.parseObject(jsonString);
//        System.out.print(res);
//        if(res.getInt("code") == 1){
//            response.sendRedirect(res.getJSONObject("data").getString("payurl"));
//        }else{
//            PrintWriter pw=response.getWriter();
//            pw.write(res.getString("msg"));
//        }

    }


}