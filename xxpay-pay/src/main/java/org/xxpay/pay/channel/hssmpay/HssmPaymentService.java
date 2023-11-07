package org.xxpay.pay.channel.hssmpay;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.XXPayUtil;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.channel.sandpay.sdk.HttpClient;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.util.Util;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HssmPaymentService extends BasePayment {


    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    private static final MyLog _log = MyLog.getLog(HssmPaymentService.class);

    @Override
    public String getChannelName() {
        return HssmConfig.CHANNEL_NAME;
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
            case HssmConfig.CHANNEL_NAME_WXPAY_QR_H5:
                retObj = doPayQrReq(payOrder, "WEIXIN_H5PAY",HssmConfig.CHANNEL_NAME_H5_PAY);
                break;
            case HssmConfig.CHANNEL_NAME_WXPAY_SC:
                retObj = doPayQrReq(payOrder, "WEIXIN_SCANPAY",HssmConfig.CHANNEL_NAME_SCAN_QR);
                break;
            case HssmConfig.CHANNEL_NAME_ALIPAY_QR_H5:
                retObj = doPayQrReq(payOrder, "ALIPAY_H5PAY",HssmConfig.CHANNEL_NAME_H5_PAY);
                break;
            case HssmConfig.CHANNEL_NAME_ALIPAY_SC:
                retObj = doPayQrReq(payOrder, "ALIPAY_SCANPAY",HssmConfig.CHANNEL_NAME_SCAN_QR);
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

        String logPrefix = "【弘顺商贸订单查询】";
        String payOrderId = payOrder.getPayOrderId();

        _log.info("{}开始查询弘顺商贸订单,payOrderId={}", logPrefix, payOrderId);
        HssmConfig hssmConfig = new HssmConfig(getPayParam(payOrder));
        JSONObject paramMap = new JSONObject();
        paramMap.put("version", hssmConfig.getMERCHANT_ID());
        paramMap.put("appId", hssmConfig.getAPP_ID());
        paramMap.put("appSecret", hssmConfig.getAPP_SECRET());
        paramMap.put("merId", hssmConfig.getMERCHANT_ID());
        paramMap.put("bizCode", "S0001");
//        paramMap.put("action", "orderquery");    // 支付中
//        String _sign = DigestUtils.md5Hex(hssmConfig.getAPP_ID()+payOrderId + "orderquery"+hssmConfig.getAPP_KEY());
//        paramMap.put("sign", _sign);
        JSONObject retObj = buildRetObj();
        String status = "";
        String message = "";

        try {
            String reqData = XXPayUtil.genUrlParams(paramMap);
            _log.info("{}请求数据:{}", logPrefix, reqData);
            String url = "http://pay.longdaxi.com/api/orders/query.html";
            String result = XXPayUtil.call4Post(url + "?" + reqData);
            _log.info("{}返回数据:{}", logPrefix, result);
            JSONObject resObj = JSONObject.parseObject(result);
            status = resObj.getString("status");
            message = resObj.getString("message");
            if ("2".equals(status)) {
                retObj.put("status", 2);    // 成功
            } else if ("0".equals(status)) {
                retObj.put("status", 1);    // 支付中
            } else {
                retObj.put("message", message);
            }
        } catch (Exception e) {
            _log.error(e, "");
        }

        return retObj;
    }

    /**支付
     * @param payOrder
     * @return
     */
    public JSONObject doPayQrReq(PayOrder payOrder, String serviceType,String bizCode) {
        HssmConfig hssmConfig = new HssmConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        String merchantId = hssmConfig.getMERCHANT_ID();
        String appKey = hssmConfig.getAPP_KEY();

        //--------------------
        try {
            String version = HssmConfig.INTERFACE_VERSION;
            String appId = hssmConfig.getAPP_ID();
            String appSecret = hssmConfig.getAPP_SECRET();
            String merId = hssmConfig.getMERCHANT_ID();
            String orderNo = payOrder.getPayOrderId(); // 订单号
            String notifyurl = payConfig.getNotifyUrl(getChannelName());// 通知地址
            String callbackurl = payConfig.getReturnUrl(getChannelName());// 返回页面
            DecimalFormat df   = new DecimalFormat("######0.00");
            String orderPrice = df.format(payOrder.getAmount().doubleValue()/100);
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            String payTime = sf.format(new Date());
            Map<String, String> param = new TreeMap<String, String>();
            param.put("version",version);
            param.put("appId",appId);
            param.put("appSecret",appSecret);
            param.put("merId",merId);
            param.put("bizCode",bizCode);
            param.put("serviceType",serviceType);
            param.put("orderNo",orderNo);
            param.put("orderPrice",orderPrice);

            String[] keyArr =getAscKey(param);
            StringBuilder keyBuilder = new StringBuilder();
            for(String key : keyArr){
                keyBuilder.append(key+"="+param.get(key)+"&");
            }
            String keyStr= keyBuilder.toString();
            String prestr = keyStr+"key="+appKey; // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
            String sign = Util.md5(prestr);
            param.put("sign", sign);
            _log.info("====加密前签名串："+prestr);
            _log.info("====加密后签名:"+sign);

            param.put("goodsName", "blueMoon");
            param.put("goodsTag", "fee charge");
            param.put("orderTime", payTime);
            param.put("terminalIp", "10.70.92.1");
            param.put("notifyUrl", notifyurl);
            String toPayUrl = hssmConfig.getReqUrl();

            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), payOrder.getPayOrderId(), updateCount);
            String result = HttpClient.doPost(toPayUrl,param, 2000, 20009);
            result = URLDecoder.decode(result, "UTF-8");
            JSONObject resObj = JSONObject.parseObject(result);

            String resCode = resObj.getString("status");
            _log.info("===============弘顺商贸返回结果："+result);
            if(!"01".equals(resCode)){
                retObj.put("errDes", resObj.getString("msg"));
                resObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
                return  resObj;
            }
            JSONObject data = resObj.getJSONObject("bodyMap");
            String payUrl = data.getString("pay_url");
            StringBuffer payForm = new StringBuffer();
            payForm.append("<form style=\"display: none\" action=\"" + payUrl + "\" method=\"get\"></form>");
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

    public static String[] getAscKey(Map<String,String> signKeyMap ){
        Set keySet = signKeyMap.keySet();
        Iterator it = keySet.iterator();
        String[] keyArr = new String[keySet.size()];
        int cd =0;
        while (it.hasNext()){
            keyArr[cd]= (String)it.next();
            cd++;
        }
        return keyArr;
    }


    public static void main(String[] agrs) throws Exception {
//        String currentDate = DateUtils.getTimeStrDefault(new Date());
//        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
//        Date dt = new Date();
//        String orderNo = "00" + sf.format(dt);// 商户订单号
//        Map<String, String> sParaTemp = new TreeMap<String, String>();
//        String version = "100";
//        String appId = "ceshi21";
//        String appSecret = "UbzclMSZnyvd3PS3lYQvAA%3D%3D";
//        String merId = "88882019073010150250";
//        String bizCode = "S0001";
//        String serviceType = "WEIXIN_SCANPAY";
//        String appKey="ec94028dafef42cb92ea6b14849baa4d";
//
////        String orderNo = "100";
//
//        String orderPrice = "100.00";
//        String goodsName = "gooodMoon";
//        String goodsTag = "goodMoon";
//        String orderTime = "100";
//        String terminalIp = "10.70.90.1";
////        String notifyUrl = "http://pay.pay188.vip/notify/Hssm/notify_res.htm";
//        String notifyUrl = "www.baidu.com";
//
//
//        String authCode = "100";
//        String bankCode = "100";
//        String bankAccType = "100";
//        String singType = "100";
//        String returnUrl = "100";
//        String currency = "100";
//        String settleCycle = "100";
//        String settleType = "100";
//        String storeId = "100";
//        String operatorId = "100";
//        sParaTemp.put("version", version);
//        sParaTemp.put("appId", appId);
//        sParaTemp.put("appSecret", appSecret);
//        sParaTemp.put("merId", merId);
//        sParaTemp.put("bizCode", bizCode);
//        sParaTemp.put("serviceType", serviceType);
//        sParaTemp.put("orderNo", orderNo);
//        sParaTemp.put("orderPrice", orderPrice);
//
//        String[] keyArr =getAscKey(sParaTemp);
//        StringBuilder keyBuilder = new StringBuilder();
//        for(String keyValue : keyArr){
//            keyBuilder.append(keyValue+"="+sParaTemp.get(keyValue)+"&");
//        }
//        String keyStr= keyBuilder.toString();
//        String prestr = keyStr+"key="+appKey; // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
//        System.out.println(prestr);
//        String sign = Util.md5(prestr);
//
//        _log.info("====加密前签名串："+prestr);
//        _log.info("====加密后签名:"+sign);
//
//
//        sParaTemp.put("goodsName", goodsName);
//        sParaTemp.put("goodsTag", goodsTag);
//        sParaTemp.put("orderTime", orderTime);
//        sParaTemp.put("terminalIp", terminalIp);
//        sParaTemp.put("notifyUrl", notifyUrl);
//        sParaTemp.put("sign", sign);
////        String dataParam = XXPayUtil.genUrlParams2(sParaTemp);
//        String toPayUrl ="http://47.104.201.164:8083/gateway/payapi/1.0/doPay";
//        String str= HttpClient.doPost(toPayUrl,sParaTemp,2000,2000);
//        System.out.println("======"+str);









//        String[] sysA = getAscKey(sParaTemp);
//        sParaTemp.put("", "100");//单位元
//        //以上参数参与签名
//        Map<String, String> sPara = Utils.paraFilter(sParaTemp);
//        String prestr = Utils.createLinkString(sPara)+"&key="+KEY; // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
//        System.out.print("md5签名明文----："+prestr);
//        String sign = MD5Encoder.encode(prestr,CHARSET).toUpperCase();
//        System.out.print("md5签名值----："+sign);
//        sParaTemp.put(AppConstants.goodsName, "tag");
//        sParaTemp.put(AppConstants.goodsTag, "tag");
//        sParaTemp.put(AppConstants.orderTime, currentDate);
//        sParaTemp.put(AppConstants.terminalIp, "127.0.0.1");
//        sParaTemp.put(AppConstants.notifyUrl, "www.baidu.com");
//        sParaTemp.put(AppConstants.sign, sign);
//        System.out.print("请求参数----："+sParaTemp.toString());
//        System.out.print("请求地址---："+PAYURL);
//        String dataParam = XXPayUtil.genUrlParams2(sParaTemp);
//        String str= HttpClient.doPost(PAYURL, sParaTemp);
//        System.out.print("返回结果----："+str);

    }


}