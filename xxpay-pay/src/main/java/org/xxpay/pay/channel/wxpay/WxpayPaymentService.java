package org.xxpay.pay.channel.wxpay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.github.binarywang.wxpay.util.SignUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.SysUser;
import org.xxpay.core.entity.ZHxtAccount;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.ctrl.constant.CS;
import org.xxpay.pay.util.RedisUtil;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/12/24
 * @description:
 */
@Service
public class WxpayPaymentService extends BasePayment {

    private static final MyLog _log = MyLog.getLog(WxpayPaymentService.class);

    @Override
    public String getChannelName() {
        return PayConstant.CHANNEL_NAME_WXPAY;
    }

    @Override
    public JSONObject pay(PayOrder payOrder) {

        String channelId = payOrder.getChannelId();
        JSONObject retObj;
        switch (channelId) {
            case PayConstant.PAY_CHANNEL_WX_APP_IOS_HXT:
                retObj = doWXpayApp_Req_HXT(payOrder);
                break;
            default:
                retObj = buildRetObj(PayConstant.RETURN_VALUE_FAIL, "不支持的支付宝渠道[channelId=" + channelId + "]");
                break;
        }
        return retObj;
    }

    private JSONObject doWXpayApp_Req_HXT(PayOrder payOrder) {

        String logPrefix = "【8022-微信APP原生(IOS)-8022】";
        JSONObject retObj = new JSONObject();


        // 获取收款账号
        ZHxtAccount zHxtAccount = null;
        for (int i = 0 ; i < 20 ; i++){
            zHxtAccount = rpcCommonService.rpcZHxtAccountService.selectReceiveAccountByUpdateTime();

            Collection<String> keys = RedisUtil.keys(CS.CHANNEL_HXT + zHxtAccount.getPhone() + "*");

            if( keys.size() == 0) break;

            boolean flag = false;
            for (String key : keys){
                JSONObject params = RedisUtil.getObject(key, JSONObject.class);
                String amount = params.getString("amount");
                if( amount.equals(String.valueOf(payOrder.getAmount() / 100)) ){
                    flag = true;
                }
            }

            _log.info("=============================================================================={} 测试", zHxtAccount.getPhone());

            if (zHxtAccount == null || i == 19) {
                retObj.put(PayConstant.RESULT_PARAM_ERRCODE, PayConstant.RESULT_VALUE_FAIL);        // errCode -1
                retObj.put(PayConstant.RESULT_PARAM_ERRDES, "获取收款账号失败");
                return retObj;
            }

            if(flag) continue;
            else break;
        }



        // 给订单添加手机号, 和码商名称
        SysUser sysUser = rpcCommonService.rpcSysService.findByUserId(zHxtAccount.getParentId());

        int res_update_phone = rpcCommonService.rpcPayOrderService.updatePhoneAndChannelUserByOrderId(payOrder.getPayOrderId(), zHxtAccount.getPhone(), sysUser.getNickName());
        if(res_update_phone == 0){
            retObj.put(PayConstant.RESULT_PARAM_ERRCODE, PayConstant.RESULT_VALUE_FAIL);        // errCode -1
            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "订单更新收款账号失败");
            return retObj;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cardNumber", zHxtAccount.getNumberCard());
        jsonObject.put("token", zHxtAccount.getToken());
        jsonObject.put("amount", String.valueOf(payOrder.getAmount() / 100));


        // 获取支付链接
        String body = "";
        try {

//            body = Jsoup.connect("http://192.168.75.1:8010/hxt/pay")
            body = Jsoup.connect("http://py.pay.huazaipay.pro/hxt/pay")
                    .method(Connection.Method.POST)
                    .header("Content-Type", "*/*")
                    .timeout(20000)
                    .requestBody(jsonObject.toJSONString())
                    .ignoreContentType(true)
                    .execute().body();
        }catch (Exception e){
            // 释放当前号码
            e.printStackTrace();
            retObj.put(PayConstant.RESULT_PARAM_ERRCODE, PayConstant.RESULT_VALUE_FAIL);        // errCode -1
            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "获取支付链接失败，请联系相关技术人员1001");
            return retObj;
        }
        JSONObject json = JSONObject.parseObject(body);
        _log.info("=== 拉起支付回调结果： {} ===", json);

        String payLink = json.getString("payLink");

        // Redis添加查单信息
        JSONObject redis_param = new JSONObject();
        redis_param.put("payOrderId", payOrder.getPayOrderId());
        redis_param.put("phone", zHxtAccount.getPhone());
        redis_param.put("token", zHxtAccount.getToken());
        redis_param.put("payLink", payLink);
        redis_param.put("amount", String.valueOf(payOrder.getAmount() / 100));
        RedisUtil.setString(CS.CHANNEL_HXT + zHxtAccount.getPhone() + "_" + payOrder.getPayOrderId(), redis_param.toJSONString(), CS.CHANNEL_KEY_TIME);

        // 填充支付链接到相关的app
        retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);
        retObj.put("payLink", "https://payment.huazaipay.pro/api/orders/wxpay/" + payOrder.getPayOrderId());
//        retObj.put("payLink", "http://192.168.0.106:3020/api/orders/wxpay/" + payOrder.getPayOrderId());


        int result = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), null);
        _log.info("[{}]更新订单状态为支付中:payOrderId={},channelOrderNo={},result={}", getChannelName(), payOrder.getPayOrderId(), "", result);

        return retObj;
    }


    public JSONObject pay_yuansheng(PayOrder payOrder) {
            String logPrefix = "【微信支付统一下单】";
            JSONObject map = new JSONObject();
            map.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);
            try{
                String channelId = payOrder.getChannelId();
                String tradeType = channelId.substring(channelId.indexOf("_") + 1).toUpperCase();   // 转大写,与微信一致
                WxPayConfig wxPayConfig = WxPayUtil.getWxPayConfig(getPayParam(payOrder), tradeType, payConfig.getCertRootPath() + File.separator + getChannelName(), payConfig.getNotifyUrl(getChannelName()));
                WxPayService wxPayService = new WxPayServiceImpl();
                wxPayService.setConfig(wxPayConfig);
                WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = buildUnifiedOrderRequest(payOrder, wxPayConfig);
                String payOrderId = payOrder.getPayOrderId();
                WxPayUnifiedOrderResult wxPayUnifiedOrderResult;
                try {
                    wxPayUnifiedOrderResult = wxPayService.unifiedOrder(wxPayUnifiedOrderRequest);
                    _log.info("{} >>> 下单成功", logPrefix);
                    map.put("payOrderId", payOrderId);
                    int result = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrderId, null);
                    _log.info("更新第三方支付订单号:payOrderId={},prepayId={},result={}", payOrderId, wxPayUnifiedOrderResult.getPrepayId(), result);
                    switch (tradeType) {
                        case PayConstant.WxConstant.TRADE_TYPE_NATIVE : {
                            JSONObject payInfo = new JSONObject();
                            payInfo.put("prepayId", wxPayUnifiedOrderResult.getPrepayId());
                            payInfo.put("codeUrl", wxPayUnifiedOrderResult.getCodeURL()); // 二维码支付链接
                            payInfo.put("codeImgUrl", payConfig.getPayUrl() + "/qrcode_img_get?url=" + wxPayUnifiedOrderResult.getCodeURL() + "&widht=200&height=200");
                            payInfo.put("payMethod", PayConstant.PAY_METHOD_CODE_IMG);
                            map.put("payParams", payInfo);
                            break;
                        }
                        case PayConstant.WxConstant.TRADE_TYPE_APP : {
                            Map<String, String> payInfo = new HashMap<>();
                            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                            String nonceStr = String.valueOf(System.currentTimeMillis());
                            // APP支付绑定的是微信开放平台上的账号，APPID为开放平台上绑定APP后发放的参数
                            String wxAppId = wxPayConfig.getAppId();
                            Map<String, String> configMap = new HashMap<>();
                            // 此map用于参与调起sdk支付的二次签名,格式全小写，timestamp只能是10位,格式固定，切勿修改
                            String partnerId = wxPayConfig.getMchId();
                            configMap.put("prepayid", wxPayUnifiedOrderResult.getPrepayId());
                            configMap.put("partnerid", partnerId);
                            String packageValue = "Sign=WXPay";
                            configMap.put("package", packageValue);
                            configMap.put("timestamp", timestamp);
                            configMap.put("noncestr", nonceStr);
                            configMap.put("appid", wxAppId);
                            // 此map用于客户端与微信服务器交互
                            payInfo.put("sign", SignUtils.createSign(configMap, wxPayConfig.getMchKey(), null));
                            payInfo.put("prepayId", wxPayUnifiedOrderResult.getPrepayId());
                            payInfo.put("partnerId", partnerId);
                            payInfo.put("appId", wxAppId);
                            payInfo.put("package", packageValue);
                            payInfo.put("timeStamp", timestamp);
                            payInfo.put("nonceStr", nonceStr);
                            map.put("payParams", JSONObject.parseObject(JSON.toJSONString(payInfo)));
                            break;
                        }
                        case PayConstant.WxConstant.TRADE_TYPE_JSPAI : {
                            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                            String nonceStr = String.valueOf(System.currentTimeMillis());
                            Map<String, String>  payInfo = new HashMap<>(); // 如果用JsonObject会出现签名错误
                            payInfo.put("appId", wxPayUnifiedOrderResult.getAppid());
                            // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
                            payInfo.put("timeStamp", timestamp);
                            payInfo.put("nonceStr", nonceStr);
                            payInfo.put("package", "prepay_id=" + wxPayUnifiedOrderResult.getPrepayId());
                            payInfo.put("signType", WxPayConstants.SignType.MD5);
                            payInfo.put("paySign", SignUtils.createSign(payInfo, wxPayConfig.getMchKey(), null));
                            // 签名以后在增加prepayId参数
                            payInfo.put("prepayId", wxPayUnifiedOrderResult.getPrepayId());
                            map.put("payParams",  JSONObject.parseObject(JSON.toJSONString(payInfo)));
                            break;
                        }
                        case PayConstant.WxConstant.TRADE_TYPE_MWEB : {
                            JSONObject payInfo = new JSONObject();
                            payInfo.put("prepayId", wxPayUnifiedOrderResult.getPrepayId());
                            payInfo.put("payUrl", wxPayUnifiedOrderResult.getMwebUrl()); // h5支付链接地址
                            map.put("payParams", payInfo);
                            break;
                        }
                    }
                } catch (WxPayException e) {
                    _log.error(e, "下单失败");
                    //出现业务错误
                    _log.info("{}下单返回失败", logPrefix);
                    _log.info("err_code:{}", e.getErrCode());
                    _log.info("err_code_des:{}", e.getErrCodeDes());
                    map.put("errDes", e.getErrCodeDes());
                    map.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
                }
            }catch (Exception e) {
                _log.error(e, "微信支付统一下单异常");
                map.put("errDes", "微信支付统一下单异常");
                map.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            }
            return map;
    }

    @Override
    public JSONObject query(PayOrder payOrder) {
        return null;
    }

    @Override
    public JSONObject close(PayOrder payOrder) {
        return null;
    }

    /**
     * 构建微信统一下单请求数据
     * @param payOrder
     * @param wxPayConfig
     * @return
     */
    WxPayUnifiedOrderRequest buildUnifiedOrderRequest(PayOrder payOrder, WxPayConfig wxPayConfig) {
        String tradeType = wxPayConfig.getTradeType();
        String payOrderId = payOrder.getPayOrderId();
        Integer totalFee = payOrder.getAmount().intValue();// 支付金额,单位分
        String deviceInfo = payOrder.getDevice();
        String body = payOrder.getBody();
        String detail = null;
        String attach = null;
        String outTradeNo = payOrderId;
        String feeType = "CNY";
        String spBillCreateIP = payOrder.getClientIp();
        String timeStart = null;
        String timeExpire = null;
        String goodsTag = null;
        String notifyUrl = payConfig.getNotifyUrl(getChannelName());
        String productId = null;
        if(tradeType.equals(PayConstant.WxConstant.TRADE_TYPE_NATIVE)) productId = System.currentTimeMillis()+"";
        String limitPay = null;
        String openId = null;
        if(tradeType.equals(PayConstant.WxConstant.TRADE_TYPE_JSPAI)) openId = JSON.parseObject(payOrder.getExtra()).getString("openId");
        String sceneInfo = null;
        if(tradeType.equals(PayConstant.WxConstant.TRADE_TYPE_MWEB)) sceneInfo = JSON.parseObject(payOrder.getExtra()).getString("sceneInfo");
        // 微信统一下单请求对象
        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
        request.setDeviceInfo(deviceInfo);
        request.setBody(body);
        request.setDetail(detail);
        request.setAttach(attach);
        request.setOutTradeNo(outTradeNo);
        request.setFeeType(feeType);
        request.setTotalFee(totalFee);
        request.setSpbillCreateIp(spBillCreateIP);
        request.setTimeStart(timeStart);
        request.setTimeExpire(timeExpire);
        request.setGoodsTag(goodsTag);
        request.setNotifyURL(notifyUrl);
        request.setTradeType(tradeType);
        request.setProductId(productId);
        request.setLimitPay(limitPay);
        request.setOpenid(openId);
        request.setSceneInfo(sceneInfo);
        return request;
    }


}
