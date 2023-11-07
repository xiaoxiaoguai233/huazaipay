package org.xxpay.pay.channel.pddpay;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MD5Util;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.util.HttpClientUtils;
import org.xxpay.pay.util.Util;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author: carter
 * @date: 2019-07-31
 * @description: 拼多多接口
 */
@Service
public class PinduoduoPaymentService extends BasePayment {

//    @Autowired
//    public BaseNotify4MchPay baseNotify4MchPay;

    private static final MyLog _log = MyLog.getLog(PinduoduoPaymentService.class);

    //http连接超时时间
    public static int connectTimeout = 300000;
    //http响应超时时间
    public static int readTimeout = 600000;

    @Override
    public String getChannelName() {
        return PinDuoDuoPayConfig.CHANNEL_NAME;
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
            case PinDuoDuoPayConfig.CHANNEL_NAME_UNIFORM_QR:
                retObj = doPay(payOrder,"h5");
                break;
            case PinDuoDuoPayConfig.CHANNEL_NAME_UNIFORM_SC:
                retObj = doPay(payOrder,"sc");
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
    public JSONObject doPay(PayOrder payOrder,String type) {
        //根据订单获取参数，然后给config的构造函数设置url、key、mch_id三个参数
        PinDuoDuoPayConfig PinDuoDuoPayConfig = new PinDuoDuoPayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        try {
            //从config类获取三个参数
            String payUrl = PinDuoDuoPayConfig.getReqUrl();//获取请求地址
            String key = PinDuoDuoPayConfig.getAPP_KEY();//密钥
            //String mch_id = PinDuoDuoPayConfig.getOUT_UID();//商户id
            String out_uid = PinDuoDuoPayConfig.getOUT_UID();
            String error_url = PinDuoDuoPayConfig.getError_url();
            String success_url = PinDuoDuoPayConfig.getSuccess_url();
            String version = PinDuoDuoPayConfig.getVersion();
            String appid = PinDuoDuoPayConfig.getAPP_ID();
            //从支付订单获取订单号、交易金额、回调地址、客户端ip
            String out_trade_no = payOrder.getPayOrderId();//20位订单号 时间戳+6位随机字符串组成
            Long payAmount = payOrder.getAmount() /100;
            String total_fee = payAmount.toString();//下单价格，单位分
            String notify_url = payConfig.getNotifyUrl(getChannelName());//通知地址
//            String appid=payOrder.getAppId();//
            //生成签名
            TreeMap<String, String> stringStringTreeMap = new TreeMap<>();
            stringStringTreeMap.put("appid",appid);
            stringStringTreeMap.put("amount",total_fee);
            stringStringTreeMap.put("callback_url",notify_url);
            stringStringTreeMap.put("error_url",error_url);
            stringStringTreeMap.put("success_url",success_url);
            stringStringTreeMap.put("out_uid",out_uid);
            stringStringTreeMap.put("out_trade_no",out_trade_no);
            stringStringTreeMap.put("version",version);
            StringBuilder stringBuilder = new StringBuilder();
            for(String s:stringStringTreeMap.keySet())
            {
                stringBuilder.append(s).append("=").append(stringStringTreeMap.get(s)).append("&");
            }
            stringBuilder.append("key="+key);
            String sign = MD5Util.string2MD5(stringBuilder.toString()).toUpperCase();
            //更新订单状态为支付中
            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(),
                    payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(),
                    payOrder.getPayOrderId(), updateCount);
            //将回调地址、订单号、价格、签名封装到map中

            stringStringTreeMap.put("sign", sign);

            String result;
            //用http客户端发送请求
            try {
                _log.info("[拼多多商户:{}]请求URL:{}, 报文：{}",appid, payUrl, stringStringTreeMap.toString());
                //result = HttpClientUtils.post(payUrl, stringStringTreeMap);
                result = HttpUtil.post(payUrl, stringStringTreeMap);
                result = URLDecoder.decode(result, "UTF-8");
            } catch (IOException e) {
                _log.error(e.getMessage());
                return null;
            }

            //解析返回结果，判断是否失败
            JSONObject resObj = JSONObject.parseObject(result);
            if (!resObj.getInteger("code").equals(200)) {
                _log.info("[拼多多商户]下单返回失败信息:{}", resObj.get("msg"));
                retObj.put("errDes", resObj.get("msg"));
                retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
                return retObj;
            }

            retObj.put("payOrderId", payOrder.getPayOrderId()); // 设置支付订单ID
            //创建个新的支付参数json对象
            JSONObject payParams = new JSONObject();
            if(type.equals("h5")) {
                //使用返回中支付地址拼接支付url
                payUrl = resObj.getJSONObject("data").getString("qrcode");
                StringBuffer payForm = new StringBuffer();
                payForm.append("<form style=\"display: none\" action=\"" + URLDecoder.decode(payUrl) + "\" method=\"post\"></form>");
                payForm.append("<script>document.forms[0].submit();</script>");
                payParams.put("payUrl", payForm);
                //设置跳转的状态为表单跳转
                payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);
            }
            else {
                //使用返回对象中的二维码地址拼接支付url
//                payUrl = resObj.getJSONObject("data").getString("qrcode");
//                StringBuffer scpayForm = new StringBuffer();
//                scpayForm.append("<form style=\"display: none\" action=\"" + URLDecoder.decode(payUrl) +"\" method=\"get\"></form>");
//                scpayForm.append("<script>document.forms[0].submit();</script>");
//                payParams.put("payUrl", scpayForm);
//                //设置跳转的状态为表单跳转
//                payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);

                //使用返回中支付地址拼接支付url
                payUrl = resObj.getJSONObject("data").getString("qrcode");
                StringBuffer payForm = new StringBuffer();
                payForm.append("<form style=\"display: none\" action=\"" + URLDecoder.decode(payUrl) + "\" method=\"post\"></form>");
                payForm.append("<script>document.forms[0].submit();</script>");
                payParams.put("payUrl", payForm);
                //设置跳转的状态为表单跳转
                payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);
            }
            //将二维码地址添加到返回结果中
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

//    public static void main(String[] args) {
//        TreeMap<String, String> stringStringTreeMap = new TreeMap<>();
//        stringStringTreeMap.put("appid","20190916642956");
//        stringStringTreeMap.put("amount","200");
//        stringStringTreeMap.put("callback_url","http://192.168.1.93:3020/notify/PinDuoDuo/notify_res.htm");
//        stringStringTreeMap.put("success_url","http://www.baidu.com");
//        stringStringTreeMap.put("error_url","http://www.baidu.com");
//        stringStringTreeMap.put("out_uid","15017399440");
//        stringStringTreeMap.put("out_trade_no","P012019091815083229600034");
//        stringStringTreeMap.put("version","v1.1");
//        StringBuilder stringBuilder = new StringBuilder();
//        for(String s:stringStringTreeMap.keySet())
//        {
//            stringBuilder.append(s).append("=").append(stringStringTreeMap.get(s)).append("&");
//        }
//        stringBuilder.append("key=37132d807d55417db5066befcc1d92d7");
//        String sign = MD5Util.string2MD5(stringBuilder.toString()).toUpperCase();
//        TreeMap<String, String> map1 = new TreeMap<>();
//        map1.put("appid","20190916642956");
//        map1.put("amount","200");
//        map1.put("callback_url","http://192.168.1.93:3020/notify/PinDuoDuo/notify_res.htm");
//        map1.put("success_url","http://www.baidu.com");
//        map1.put("error_url","http://www.baidu.com");
//        map1.put("out_uid","15017399440");
//        map1.put("out_trade_no","P012019091815083229600034");
//        map1.put("version","v1.1");
//        map1.put("sign",sign);
//        String result;
//        //用http客户端发送请求
//        try {
//            result = HttpClientUtils.post("http://api.redundant.cn/sh-alipay-gateway/gateway/api/unifiedorder.do?format=json", map1);
//            result = URLDecoder.decode(result, "UTF-8");
//            System.out.println(result);
//        } catch (IOException e) {
//            _log.error(e.getMessage());
//        }
//    }
}
