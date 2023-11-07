package org.xxpay.pay.channel.huafeipay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.channel.ssfpay.utils.SsfpayUtils;
import org.xxpay.pay.util.HttpClientUtils;
import org.xxpay.pay.util.Util;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class HuafeipayPaymentService extends BasePayment {
    private static final MyLog _log = MyLog.getLog(HuafeipayPaymentService.class);

    @Override
    public String getChannelName() {
        return HuafeipayConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject pay(PayOrder payOrder) {
        String channelId = payOrder.getChannelId();
        JSONObject retObj;
        switch (channelId) {
            case HuafeipayConfig.CHANNEL_NAME_WXPAY_QR:
                retObj = doPayQrReq(payOrder, "1025");
                break;
            case HuafeipayConfig.CHANNEL_NAME_ALIPAY_QR:
                retObj = doPayQrReq(payOrder, "1024" );
                break;
            case HuafeipayConfig.CHANNEL_NAME_ALIPAY_H5:
                retObj = doPayQrReq(payOrder, "1024");
                break;
            case HuafeipayConfig.CHANNEL_NAME_WXPAY_H5:
                retObj = doPayQrReq(payOrder, "1025");
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
    public JSONObject doPayQrReq(PayOrder payOrder,String ProductId ) {
        HuafeipayConfig huafeipayConfig = new HuafeipayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        String AuthorizationURL = huafeipayConfig.getReqUrl();  // "http://slbapi.isarmo.com/api/TransReq/UnifiedOrder"
        String MerchantId = huafeipayConfig.getAPP_ID();  // 主商户号
        String ShopNo = huafeipayConfig.getAPP_CHILD_ID(); // 商户号
        String keyValue = huafeipayConfig.getAPP_KEY();  //  key

        //--------------------
        try {
            String timestamp = String.valueOf(new Date().getTime() / 1000);
            Random random = new Random();
            int nonceTemp = Math.abs(random.nextInt());
            String RequestNo = String.valueOf(nonceTemp); // 请求流水号
//            Integer paytype = channel; // 支付类型
            String OrderCode = payOrder.getPayOrderId(); // 20位订单号 时间戳+6位随机字符串组成
            String CommodityName = "tongda"; // 商品名称
            DecimalFormat df = new DecimalFormat("######0.00");
            String Amount = df.format(payOrder.getAmount().doubleValue() / 100);
            String NotifyCallBackUrl = payConfig.getNotifyUrl(getChannelName());  // 通知地址
            String WebCallBackUrl = payConfig.getReturnUrl(getChannelName());  // 回调地址
            String PayProductType = HuafeipayConfig.PAY_PRODUCT_TYPE;   // 支付类型
            TreeMap<String, Object> map = new TreeMap<String, Object>();
            map.put("Amount", Amount);
            map.put("RequestNo", RequestNo);
            map.put("Version", "1.0");
            map.put("CommodityName", CommodityName);
            map.put("MerchantId", MerchantId);
            map.put("NotifyCallBackUrl", NotifyCallBackUrl);
            map.put("OrderCode", OrderCode);
            map.put("PayProductType", PayProductType);
            map.put("ProductId", ProductId);
            map.put("ShopNo", ShopNo);
            map.put("WebCallBackUrl", WebCallBackUrl);
            String signTemp = doSignString(map) + keyValue;
            _log.info("加密前字符串：{}",signTemp);
            String Signature = Util.md5(signTemp);
            _log.info("加密后：{}",Signature);

            JSONObject param = new JSONObject();
            param.put("RequestNo", RequestNo);
            param.put("Version", "1.0");
            param.put("MerchantId", MerchantId);
            param.put("ShopNo", ShopNo);
            param.put("ProductId", ProductId);
            param.put("PayProductType", PayProductType);
            param.put("ProductId", ProductId);
            param.put("CommodityName", CommodityName);
            param.put("Amount", Amount);
            param.put("OrderCode", OrderCode);
            param.put("WebCallBackUrl", WebCallBackUrl);
            param.put("NotifyCallBackUrl", NotifyCallBackUrl);
            param.put("Signature", Signature);

            String reqUrl = huafeipayConfig.getReqUrl();
            _log.info("请求上游通道话费充值渠道参数{}",param.toJSONString());
            String result = HttpClientUtils.postToJSON(reqUrl, param.toJSONString());
            String rest = JSONObject.parse(result).toString();
            String[] resArr = rest.substring(1,rest.length()-1).split(",");
            Map<String, Object> res = getKeyAndValue(resArr);
            _log.info("上游通道话费充值渠道响应数据{}",res);
//            JSONObject res = (JSONObject) JSONObject.toJSON(rest);
            if (!"0000".equals(res.get("RespCode").toString())) {
                _log.error(res.get("RespDesc").toString(), "");
                retObj.put("errDes", "上游返回失败!");
                retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
                return retObj;
            }
            String reqData = JSONObject.toJSONString(param);
            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), payOrder.getPayOrderId(), updateCount);

            // 支付链接地址
            JSONObject payParams = new JSONObject();
            String payUrl=null;
            if(res.get("CodeUrl")!=null){
                payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);
                payUrl= res.get("CodeUrl").toString();
            }else{
                payParams.put("payMethod", PayConstant.PAY_METHOD_CODE_IMG);
                payUrl= res.get("ImgUrl").toString();
            }
//            payParams.put("codeUrl", CodeUrl); // 二维码支付链接
//            payParams.put("codeImgUrl", ImgUrl);
            StringBuffer payForm = new StringBuffer();
            payForm.append("<form style=\"display: none\" action=\"" + payUrl + "\" method=\"post\"></form>");
            payForm.append("<script>document.forms[0].submit();</script>");
            payParams.put("payUrl", payForm);
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

    private static String doSignString(TreeMap<String, Object> map) {
        Set keySet = map.keySet();
        Iterator it = keySet.iterator();
        StringBuilder strTemp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next().toString();
            if (StringUtils.isNotBlank(String.valueOf(map.get(key))))
                strTemp.append(key + "=" + map.get(key) + "&");
        }
        String resStr=  strTemp.toString().substring(0, strTemp.toString().length()-1);
        return resStr;
    }


    public static void main(String[] args) throws Exception {
//        String WebCallBackUrl = "www.baidu.com";
//        String NotifyCallBackUrl = "www.baidu.com";
//
//        String keyValue = "45ae89285cb8474bb37c6eb8e3f2a7b1";
//
//        Random random = new Random();
//        int nonceTemp = Math.abs(random.nextInt());
//        String RequestNo = String.valueOf(nonceTemp); // 请求流水号
//        String Version= "V1.0";  // 版本号
//        String MerchantId = "10032700001"; // 主商户号
//        String ShopNo = "102019083000001"; // 商户号
//
//        Integer ProductId = 1024; // 产品类型
//        Integer PayProductType = 2; // 产品类型
//        String CommodityName = "aaaaa"; // 商品名称
//
//        DecimalFormat df = new DecimalFormat("######0.00");
//        String Amount = df.format(20000 / 100);
//
//        String OrderCode = SsfpayUtils.generateOrderId();  // 20位订单号 时间戳+6位随机字符串组成
//
//
//        TreeMap<String, Object> map = new TreeMap<String, Object>();
//        map.put("Amount", Amount);
//        map.put("RequestNo", RequestNo);
//        map.put("Version", Version);
//        map.put("CommodityName", CommodityName);
//        map.put("MerchantId", MerchantId);
//        map.put("NotifyCallBackUrl", NotifyCallBackUrl);
//        map.put("OrderCode", OrderCode);
//        map.put("PayProductType", PayProductType);
//        map.put("ProductId", ProductId);
//        map.put("ShopNo", ShopNo);
//        map.put("WebCallBackUrl", WebCallBackUrl);
//
//        String signTemp = doSignString(map) + keyValue;
//        System.out.println(signTemp + "-----");
//        String Signature = Util.md5(signTemp);
        JSONObject param = new JSONObject();

//
//        param.put("RequestNo", RequestNo);
//        param.put("Version", Version);
//        param.put("MerchantId", MerchantId);
//        param.put("ShopNo", ShopNo);
//        param.put("ProductId", ProductId);
//        param.put("PayProductType", PayProductType);
//        param.put("ProductId", ProductId);
//        param.put("CommodityName", CommodityName);
//        param.put("Amount", Amount);
//        param.put("OrderCode", OrderCode);
//        param.put("WebCallBackUrl", WebCallBackUrl);
//        param.put("NotifyCallBackUrl", NotifyCallBackUrl);
//        param.put("Signature", Signature);
//
//
//
//        System.out.println(param.toString());
//        String AuthorizationURL = "http://slbapi.isarmo.com/api/TransReq/UnifiedOrder";
////        String post = HttpClientUtils.post(AuthorizationURL, param);
//        String post = HttpClientUtils.postToJSON(AuthorizationURL, JSONObject.toJSONString(param));
//
//
//
//
//        System.out.println(post);

    }





    public static Map<String, Object> getKeyAndValue(String[] resArr) {
        Map<String, Object> map = new HashMap<>();
        int resArrLen= resArr.length;
        for(int i=0;i<resArrLen;i++){
           String element =  resArr[i];
           String key = element.substring(0,element.indexOf(":"));
           String value = element.substring(element.indexOf(":")+1);
           String mapValue= "";
           if(value.contains("\"")){
               mapValue = value.substring(1,value.length()-1);
           }else {
               mapValue = value;
           }
            map.put(key.substring(1,key.length()-1),mapValue);
        }
        return map;
    }



}
