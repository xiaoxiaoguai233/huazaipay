package org.xxpay.pay.channel.alipay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.*;
import com.alipay.api.response.AlipayTradeQueryResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.AmountUtil;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.SysUser;
import org.xxpay.core.entity.ZHshAccount;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.ctrl.constant.CS;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.util.RedisUtil;

/**
 * @author: dingzhiwei
 * @date: 17/12/24
 * @description:
 */
@Service
public class AlipayPaymentService extends BasePayment {

    private static final MyLog _log = MyLog.getLog(AlipayPaymentService.class);
    public final static String PAY_CHANNEL_ALIPAY_QR_H5 = "alipay_qr_h5";	            // 支付宝当面付之H5支付
    public final static String PAY_CHANNEL_ALIPAY_QR_PC = "alipay_qr_pc";	            // 支付宝当面付之PC支付
    public final static String PAY_CHANNEL_ALIPAY_COUNPON_PAGE = "alipay_coupon_page";	        // 支付宝红包页面支付
    public final static String PAY_CHANNEL_ALIPAY_COUNPON_APP = "alipay_coupon_app";	        // 支付宝红包无线支付

    public final static String PAY_CHANNEL_ALIPAY_APP_SLRJ = "alipay_app_slrj";	        // 支付宝APP支付-SLRJ

    @Value("payment")
    private String request_str;



    @Autowired public BaseNotify4MchPay baseNotify4MchPay;

    @Autowired AlipayAsynCreateOrders alipayAsynCreateOrders;

    @Override
    public String getChannelName() {
        return PayConstant.CHANNEL_NAME_ALIPAY;
    }

    @Override
    public JSONObject pay(PayOrder payOrder) throws Exception {
        String channelId = payOrder.getChannelId();
        JSONObject retObj;
        switch (channelId) {
            case PAY_CHANNEL_ALIPAY_APP_SLRJ :                  // 支付宝APP - SLRJ
                retObj = doAliPayAPPSLRJReq(payOrder);
                break;
            case PayConstant.PAY_CHANNEL_ALIPAY_MOBILE :
                retObj = doAliPayMobileReq(payOrder);
                break;
            case PayConstant.PAY_CHANNEL_ALIPAY_PC :
                retObj = doAliPayPcReq(payOrder);
                break;
            case PayConstant.PAY_CHANNEL_ALIPAY_WAP :           // 支付宝H5
                retObj = doAliPayWapReq(payOrder);
                break;
            case PayConstant.PAY_CHANNEL_ALIPAY_H5 :           // 支付宝H5 - HSH
                retObj = doAliPayH5Req(payOrder);
                break;
            case PayConstant.PAY_CHANNEL_ALIPAY_QR :
                retObj = doAliPayQrReq(payOrder);
                break;
            case PAY_CHANNEL_ALIPAY_QR_H5 :
                retObj = doAliPayQrH5Req(payOrder,"wap");
                break;
            case PAY_CHANNEL_ALIPAY_QR_PC :
                retObj = doAliPayQrPcReq(payOrder,"pc");
                break;
            case PAY_CHANNEL_ALIPAY_COUNPON_APP :
                retObj = doAliPayCouponAppReq(payOrder);
                break;
            case PAY_CHANNEL_ALIPAY_COUNPON_PAGE :
                retObj = doAliPayCouponPageReq(payOrder);
                break;
            default:
                retObj = buildRetObj(PayConstant.RETURN_VALUE_FAIL, "不支持的支付宝渠道[channelId="+channelId+"]");
                break;
        }
        return retObj;
    }

    @Override
    public JSONObject query(PayOrder payOrder) {
        String logPrefix = "【支付宝订单查询】";
        String payOrderId = payOrder.getPayOrderId();
        String channelOrderNo = payOrder.getChannelOrderNo();
        _log.info("{}开始查询支付宝通道订单,payOrderId={}", logPrefix, payOrderId);
        AlipayConfig alipayConfig = new AlipayConfig(getPayParam(payOrder));
        AlipayClient client = new DefaultAlipayClient(alipayConfig.getReqUrl(), alipayConfig.getAppId(), alipayConfig.getPrivateKey(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getAlipayPublicKey(), AlipayConfig.SIGNTYPE);
        AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
        // 商户订单号，商户网站订单系统中唯一订单号，必填
        AlipayTradeQueryModel model=new AlipayTradeQueryModel();
        model.setOutTradeNo(payOrderId);
        model.setTradeNo(channelOrderNo);
        alipay_request.setBizModel(model);

        AlipayTradeQueryResponse alipay_response;
        String result = "";
        try {
            alipay_response = client.execute(alipay_request);
            // 交易状态：
            // WAIT_BUYER_PAY（交易创建，等待买家付款）、
            // TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、
            // TRADE_SUCCESS（交易支付成功）、
            // TRADE_FINISHED（交易结束，不可退款）
            result = alipay_response.getTradeStatus();
            channelOrderNo = alipay_response.getTradeNo();
            _log.info("{}payOrderId={}返回结果:{}", logPrefix, payOrderId, result);

        } catch (AlipayApiException e) {
            _log.error(e, "");
        }

        JSONObject retObj = buildRetObj();
        retObj.put("channelOrderNo", channelOrderNo);
        retObj.put("status", 1);    // 支付中
        if("TRADE_SUCCESS".equals(result)) {
            retObj.put("status", 2);    // 成功
        }else if("WAIT_BUYER_PAY".equals(result)) {
            retObj.put("status", 1);    // 支付中
        }
        return retObj;
    }

    @Override
    public JSONObject close(PayOrder payOrder) {
        return null;
    }


    /**
     * 支付宝APP支付-SLRJ
     * @param payOrder
     * @return
     */
    public JSONObject doAliPayAPPSLRJReq(PayOrder payOrder) throws Exception {

        String logPrefix = "【8024-支付宝原生任额-8024】";

        JSONObject retObj = new JSONObject();

//        String amount = String.valueOf(payOrder.getAmount() / 100);

        double amount = Double.valueOf(payOrder.getAmount() / 100);

        if(amount >= 50.0 && amount <= 1000.0){

            // 填充支付链接到相关的app
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);

            alipayAsynCreateOrders.Alipay_SLRJ_App(payOrder);

            // Redis参数设置
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", "0");

            RedisUtil.setString(payOrder.getPayOrderId(), jsonObject.toJSONString(), 60);

            // formJump payUrl
            // urlJump  payUrl
            // codeImg  codeImgUrl

            JSONObject payParams = new JSONObject();
            payParams.put("payMethod", "urlJump");

//            payParams.put("payUrl", "http://192.168.1.3:3020/api/waiting/" + payOrder.getPayOrderId());

            payParams.put("payUrl", "http://pay.huazaipay.info/api/waiting/" + payOrder.getPayOrderId());

//            payParams.put("payUrl", "https://payment.wuye6688.com/api/waiting/" + payOrder.getPayOrderId());

            retObj.put("payParams", payParams);
        }else{
            retObj.put(PayConstant.RESULT_PARAM_ERRCODE, PayConstant.RESULT_VALUE_FAIL);        // errCode -1
            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "金额错误，请重试！");
        }
        return retObj;
    }

    /**
     * 支付宝H5支付
     * @param payOrder
     * @return
     */
    public JSONObject doAliPayH5Req(PayOrder payOrder) {

        String logPrefix = "【8020-支付宝小额-8020】";
        JSONObject retObj = new JSONObject();


        // 获取收款账号
        ZHshAccount zHshAccount = rpcCommonService.rpcZHshAccountService.selectReceiveAccountByUpdateTime();
        if(zHshAccount == null){
            retObj.put(PayConstant.RESULT_PARAM_ERRCODE, PayConstant.RESULT_VALUE_FAIL);        // errCode -1
            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "获取收款账号失败");
            return retObj;
        }

        // 给订单添加手机号, 和码商名称
        SysUser sysUser = rpcCommonService.rpcSysService.findByUserId(zHshAccount.getParentId());

        int res_update_phone = rpcCommonService.rpcPayOrderService.updatePhoneAndChannelUserByOrderId(payOrder.getPayOrderId(), zHshAccount.getPhone(), sysUser.getNickName());
        if(res_update_phone == 0){
            retObj.put(PayConstant.RESULT_PARAM_ERRCODE, PayConstant.RESULT_VALUE_FAIL);        // errCode -1
            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "订单更新收款账号失败");
            return retObj;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", zHshAccount.getRemark());
        jsonObject.put("token", zHshAccount.getToken());
        jsonObject.put("amount", String.valueOf(payOrder.getAmount() / 100));

        // 获取支付链接
        String body = "";
        try {
            body = Jsoup.connect("http://127.0.0.1:8010/sxsh/pay")
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

        String payBody = null;
        String orderSn = null;      // 三喜生活平台订单
        // 获取支付链接（过期时间24H）成功
        if (json.getString("message").equals("SUCCESS")){
            payBody = json.getJSONObject("data").getString("payBody");
            orderSn = json.getJSONObject("data").getString("orderSn");
        }else{
            retObj.put(PayConstant.RESULT_PARAM_ERRCODE, PayConstant.RESULT_VALUE_FAIL);        // errCode -1
            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "获取支付链接失败，请联系相关技术人员1002");
            return retObj;
        }

        // Redis添加查单信息
        JSONObject redis_param = new JSONObject();
        redis_param.put("payOrderId", payOrder.getPayOrderId());
        redis_param.put("phone", zHshAccount.getPhone());
        redis_param.put("token", zHshAccount.getToken());
        redis_param.put("orderSn", orderSn);
        redis_param.put("payLink", payBody);
        redis_param.put("amount", String.valueOf(payOrder.getAmount() / 100));
        RedisUtil.setString(CS.CHANNEL_SXSH + zHshAccount.getPhone() + "_" + payOrder.getPayOrderId(), redis_param.toJSONString(), CS.CHANNEL_KEY_TIME);

        // 填充支付链接到相关的app
        retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);
        retObj.put("payLink", "https://payment.huazaipay.pro/api/orders/h5/pay/" + payOrder.getPayOrderId());

//        retObj.put("payLink", "http://192.168.0.113:3020/api/orders/h5/pay/" + payOrder.getPayOrderId());


        // 将订单更改为支付中
        int result = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), null);
        _log.info("[{}]更新订单状态为支付中:payOrderId={},channelOrderNo={},result={}", getChannelName(), payOrder.getPayOrderId(), "", result);


        return retObj;
    }

    /**
     * 支付宝wap支付
     * @param payOrder
     * @return
     */
    public JSONObject doAliPayWapReq(PayOrder payOrder) {
        String logPrefix = "【支付宝WAP支付下单】";
        String payOrderId = payOrder.getPayOrderId();
        AlipayConfig alipayConfig = new AlipayConfig(getPayParam(payOrder));
        AlipayClient client = new DefaultAlipayClient(alipayConfig.getReqUrl(), alipayConfig.getAppId(), alipayConfig.getPrivateKey(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getAlipayPublicKey(), AlipayConfig.SIGNTYPE);
        AlipayTradeWapPayRequest alipay_request = new AlipayTradeWapPayRequest();
        // 封装请求支付信息
        AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
        model.setOutTradeNo(payOrderId);
        model.setSubject(payOrder.getSubject());
        model.setTotalAmount(AmountUtil.convertCent2Dollar(payOrder.getAmount().toString()));
        model.setBody(payOrder.getBody());
        model.setProductCode("QUICK_WAP_PAY");
        // 获取objParams参数
        String objParams = payOrder.getExtra();
        if (StringUtils.isNotEmpty(objParams)) {
            try {
                JSONObject objParamsJson = JSON.parseObject(objParams);
                if(StringUtils.isNotBlank(objParamsJson.getString("quit_url"))) {
                    model.setQuitUrl(objParamsJson.getString("quit_url"));
                }
            } catch (Exception e) {
                _log.error("{}objParams参数格式错误！", logPrefix);
            }
        }
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(payConfig.getNotifyUrl(getChannelName()));
        // 设置同步跳转地址
        alipay_request.setReturnUrl(payConfig.getReturnUrl(getChannelName()));
        String payUrl = null;
        JSONObject retObj = buildRetObj();
        try {
            String body = client.pageExecute(alipay_request).getBody();
            //payUrl = buildWapUrl(body);
            payUrl = body;
        } catch (AlipayApiException e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[" + e.getErrMsg() + "]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        } catch (Exception e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[调取通道异常]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
        _log.info("{}生成跳转路径：payUrl={}", logPrefix, payUrl);

        if(StringUtils.isBlank(payUrl)) {
            retObj.put("errDes", "调用支付宝异常!");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrderId, null);
        _log.info("{}生成请求支付宝数据,req={}", logPrefix, alipay_request.getBizModel());
        _log.info("###### 商户统一下单处理完成 ######");

        retObj.put("payOrderId", payOrderId);
        JSONObject payParams = new JSONObject();
        payParams.put("payUrl", payUrl);
        payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);
        retObj.put("payParams", payParams);
        return retObj;
    }

    /**
     * 支付宝pc支付
     * @param payOrder
     * @return
     */
    public JSONObject doAliPayPcReq(PayOrder payOrder) {
        String logPrefix = "【支付宝PC支付下单】";
        String payOrderId = payOrder.getPayOrderId();
        AlipayConfig alipayConfig = new AlipayConfig(getPayParam(payOrder));
        AlipayClient client = new DefaultAlipayClient(alipayConfig.getReqUrl(), alipayConfig.getAppId(), alipayConfig.getPrivateKey(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getAlipayPublicKey(), AlipayConfig.SIGNTYPE);
        AlipayTradePagePayRequest alipay_request = new AlipayTradePagePayRequest();
        // 封装请求支付信息
        AlipayTradePagePayModel model=new AlipayTradePagePayModel();
        model.setOutTradeNo(payOrderId);
        model.setSubject(payOrder.getSubject());
        model.setTotalAmount(AmountUtil.convertCent2Dollar(payOrder.getAmount().toString()));
        model.setBody(payOrder.getBody());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        // 获取objParams参数
        String objParams = payOrder.getExtra();
        String qr_pay_mode = "2";
        String qrcode_width = "200";
        if (StringUtils.isNotEmpty(objParams)) {
            try {
                JSONObject objParamsJson = JSON.parseObject(objParams);
                qr_pay_mode = ObjectUtils.toString(objParamsJson.getString("qr_pay_mode"), "2");
                qrcode_width = ObjectUtils.toString(objParamsJson.getString("qrcode_width"), "200");
            } catch (Exception e) {
                _log.error("{}objParams参数格式错误！", logPrefix);
            }
        }
        model.setQrPayMode(qr_pay_mode);
        model.setQrcodeWidth(Long.parseLong(qrcode_width));
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(payConfig.getNotifyUrl(getChannelName()));
        // 设置同步跳转地址
        alipay_request.setReturnUrl(payConfig.getReturnUrl(getChannelName()));
        String payUrl = null;
        JSONObject retObj = buildRetObj();
        try {
            payUrl = client.pageExecute(alipay_request).getBody();
        } catch (AlipayApiException e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[" + e.getErrMsg() + "]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        } catch (Exception e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[调取通道异常]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
        _log.info("{}生成跳转路径：payUrl={}", logPrefix, payUrl);

        if(StringUtils.isBlank(payUrl)) {
            retObj.put("errDes", "调用支付宝异常!");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrderId, null);
        _log.info("{}生成请求支付宝数据,req={}", logPrefix, alipay_request.getBizModel());
        _log.info("###### 商户统一下单处理完成 ######");
        retObj.put("payOrderId", payOrderId);
        JSONObject payParams = new JSONObject();
        payParams.put("payUrl", payUrl);
        payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);
        retObj.put("payParams", payParams);
        return retObj;
    }

    /**
     * 支付宝手机支付
     * TODO 待测试
     * @param payOrder
     * @return
     */
    public JSONObject doAliPayMobileReq(PayOrder payOrder) {
        String logPrefix = "【支付宝APP支付下单】";
        String payOrderId = payOrder.getPayOrderId();
        AlipayConfig alipayConfig = new AlipayConfig(getPayParam(payOrder));
        AlipayClient client = new DefaultAlipayClient(alipayConfig.getReqUrl(), alipayConfig.getAppId(), alipayConfig.getPrivateKey(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getAlipayPublicKey(), AlipayConfig.SIGNTYPE);
        AlipayTradeAppPayRequest alipay_request = new AlipayTradeAppPayRequest();
        // 封装请求支付信息
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setOutTradeNo(payOrderId);
        model.setSubject(payOrder.getSubject());
        model.setTotalAmount(AmountUtil.convertCent2Dollar(payOrder.getAmount().toString()));
        model.setBody(payOrder.getBody());
        model.setProductCode("QUICK_MSECURITY_PAY");
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(payConfig.getNotifyUrl(getChannelName()));
        // 设置同步跳转地址
        alipay_request.setReturnUrl(payConfig.getReturnUrl(getChannelName()));
        String payParams = null;
        JSONObject retObj = buildRetObj();
        try {
            payParams = client.sdkExecute(alipay_request).getBody();
        } catch (AlipayApiException e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[" + e.getErrMsg() + "]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        } catch (Exception e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[调取通道异常]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }

        if(StringUtils.isBlank(payParams)) {
            retObj.put("errDes", "调用支付宝异常!");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrderId, null);
        _log.info("{}生成请求支付宝数据,payParams={}", logPrefix, payParams);
        _log.info("###### 商户统一下单处理完成 ######");
        retObj.put("payOrderId", payOrderId);
        retObj.put("payParams", payParams);
        return retObj;
    }

    /**
     * 支付宝当面付(扫码)支付
     * 收银员通过收银台或商户后台调用支付宝接口，生成二维码后，展示给用户，由用户扫描二维码完成订单支付。
     * @param payOrder
     * @return
     */
    public JSONObject doAliPayQrReq(PayOrder payOrder) {
        String logPrefix = "【支付宝当面付之扫码支付下单】";
        String payOrderId = payOrder.getPayOrderId();
        AlipayConfig alipayConfig = new AlipayConfig(getPayParam(payOrder));
        AlipayClient client = new DefaultAlipayClient(alipayConfig.getReqUrl(), alipayConfig.getAppId(), alipayConfig.getPrivateKey(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getAlipayPublicKey(), AlipayConfig.SIGNTYPE);
        AlipayTradePrecreateRequest alipay_request = new AlipayTradePrecreateRequest();
        // 封装请求支付信息
        AlipayTradePrecreateModel model=new AlipayTradePrecreateModel();
        model.setOutTradeNo(payOrderId);
        model.setSubject(payOrder.getSubject());
        model.setTotalAmount(AmountUtil.convertCent2Dollar(payOrder.getAmount().toString()));
        model.setBody(payOrder.getBody());
        // 获取objParams参数
        String objParams = payOrder.getExtra();
        if (StringUtils.isNotEmpty(objParams)) {
            try {
                JSONObject objParamsJson = JSON.parseObject(objParams);
                if(StringUtils.isNotBlank(objParamsJson.getString("discountable_amount"))) {
                    //可打折金额
                    model.setDiscountableAmount(objParamsJson.getString("discountable_amount"));
                }
                if(StringUtils.isNotBlank(objParamsJson.getString("undiscountable_amount"))) {
                    //不可打折金额
                    model.setUndiscountableAmount(objParamsJson.getString("undiscountable_amount"));
                }
            } catch (Exception e) {
                _log.error("{}objParams参数格式错误！", logPrefix);
            }
        }
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(payConfig.getNotifyUrl(getChannelName()));
        // 设置同步跳转地址
        alipay_request.setReturnUrl(payConfig.getReturnUrl(getChannelName()));
        String aliResult;
        String codeUrl = "";
        JSONObject retObj = buildRetObj();
        try {
            aliResult = client.execute(alipay_request).getBody();
            JSONObject aliObj = JSONObject.parseObject(aliResult);
            JSONObject aliResObj = aliObj.getJSONObject("alipay_trade_precreate_response");
            codeUrl = aliResObj.getString("qr_code");
        } catch (AlipayApiException e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[" + e.getErrMsg() + "]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        } catch (Exception e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[调取通道异常]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
        _log.info("{}生成支付宝二维码：codeUrl={}", logPrefix, codeUrl);

        if(StringUtils.isBlank(codeUrl)) {
            retObj.put("errDes", "调用支付宝异常!");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrderId, null);
        retObj.put("payOrderId", payOrderId);
        JSONObject payInfo = new JSONObject();
        payInfo.put("codeUrl", codeUrl); // 二维码支付链接
        payInfo.put("codeImgUrl", payConfig.getPayUrl() + "/qrcode_img_get?url=" + codeUrl + "&widht=200&height=200");
        payInfo.put("payMethod", PayConstant.PAY_METHOD_CODE_IMG);
        retObj.put("payParams", payInfo);
        _log.info("###### 商户统一下单处理完成 ######");
        return retObj;
    }


    /**
     * 支付宝当面付(PC)支付
     * 收银员通过收银台或商户后台调用支付宝接口，生成二维码后，展示给用户，由用户扫描二维码完成订单支付。
     * @param payOrder
     * @return
     */
    public JSONObject doAliPayQrPcReq(PayOrder payOrder, String type) {
        String logPrefix = "【支付宝当面付之PC支付下单】";
        String payOrderId = payOrder.getPayOrderId();
        AlipayConfig alipayConfig = new AlipayConfig(getPayParam(payOrder));
        AlipayClient client = new DefaultAlipayClient(alipayConfig.getReqUrl(), alipayConfig.getAppId(), alipayConfig.getPrivateKey(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getAlipayPublicKey(), AlipayConfig.SIGNTYPE);
        AlipayTradePrecreateRequest alipay_request = new AlipayTradePrecreateRequest();
        // 封装请求支付信息
        AlipayTradePrecreateModel model=new AlipayTradePrecreateModel();
        model.setOutTradeNo(payOrderId);
        model.setSubject(payOrder.getSubject());
        model.setTotalAmount(AmountUtil.convertCent2Dollar(payOrder.getAmount().toString()));
        model.setBody(payOrder.getBody());
        // 获取objParams参数
        String objParams = payOrder.getExtra();
        if (StringUtils.isNotEmpty(objParams)) {
            try {
                JSONObject objParamsJson = JSON.parseObject(objParams);
                if(StringUtils.isNotBlank(objParamsJson.getString("discountable_amount"))) {
                    //可打折金额
                    model.setDiscountableAmount(objParamsJson.getString("discountable_amount"));
                }
                if(StringUtils.isNotBlank(objParamsJson.getString("undiscountable_amount"))) {
                    //不可打折金额
                    model.setUndiscountableAmount(objParamsJson.getString("undiscountable_amount"));
                }
            } catch (Exception e) {
                _log.error("{}objParams参数格式错误！", logPrefix);
            }
        }
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(payConfig.getNotifyUrl(getChannelName()));
        // 设置同步跳转地址
        alipay_request.setReturnUrl(payConfig.getReturnUrl(getChannelName()));
        String aliResult;
        String codeUrl = "";
        JSONObject retObj = buildRetObj();
        try {
            aliResult = client.execute(alipay_request).getBody();
            JSONObject aliObj = JSONObject.parseObject(aliResult);
            JSONObject aliResObj = aliObj.getJSONObject("alipay_trade_precreate_response");
            codeUrl = aliResObj.getString("qr_code");
        } catch (AlipayApiException e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[" + e.getErrMsg() + "]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        } catch (Exception e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[调取通道异常]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
        _log.info("{}生成支付宝二维码：codeUrl={}", logPrefix, codeUrl);
        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrderId, null);

        String codeImgUrl = payConfig.getPayUrl() + "/qrcode_img_get?url=" + codeUrl + "&widht=200&height=200";
        StringBuffer payForm = new StringBuffer();
        String toPayUrl = payConfig.getPayUrl() + "/alipay/pay_"+type+".htm";
        payForm.append("<form style=\"display: none\" action=\""+toPayUrl+"\" method=\"post\">");
        payForm.append("<input name=\"mchOrderNo\" value=\""+payOrder.getMchOrderNo()+"\" >");
        payForm.append("<input name=\"payOrderId\" value=\""+payOrder.getPayOrderId()+"\" >");
        payForm.append("<input name=\"amount\" value=\""+payOrder.getAmount()+"\" >");
        payForm.append("<input name=\"codeUrl\" value=\""+codeUrl+"\" >");
        payForm.append("<input name=\"codeImgUrl\" value=\""+codeImgUrl+"\" >");
        payForm.append("<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >");
        payForm.append("</form>");
        payForm.append("<script>document.forms[0].submit();</script>");

        retObj.put("payOrderId", payOrderId);
        JSONObject payInfo = new JSONObject();
        payInfo.put("payUrl",payForm);
        payInfo.put("payMethod",PayConstant.PAY_METHOD_FORM_JUMP);
        retObj.put("payParams", payInfo);
        _log.info("###### 商户统一下单处理完成 ######");
        return retObj;
    }


    /**
     * 支付宝当面付(H5)支付
     * 收银员通过收银台或商户后台调用支付宝接口，可直接打开支付宝app付款。
     * @param payOrder
     * @return
     */
    public JSONObject doAliPayQrH5Req(PayOrder payOrder, String type) {
        String logPrefix = "【支付宝当面付之H5支付下单】";
        String payOrderId = payOrder.getPayOrderId();
        AlipayConfig alipayConfig = new AlipayConfig(getPayParam(payOrder));
        AlipayClient client = new DefaultAlipayClient(alipayConfig.getReqUrl(), alipayConfig.getAppId(), alipayConfig.getPrivateKey(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getAlipayPublicKey(), AlipayConfig.SIGNTYPE);
        AlipayTradePrecreateRequest alipay_request = new AlipayTradePrecreateRequest();
        // 封装请求支付信息
        AlipayTradePrecreateModel model=new AlipayTradePrecreateModel();
        model.setOutTradeNo(payOrderId);
        model.setSubject(payOrder.getSubject());
        model.setTotalAmount(AmountUtil.convertCent2Dollar(payOrder.getAmount().toString()));
        model.setBody(payOrder.getBody());
        // 获取objParams参数
        String objParams = payOrder.getExtra();
        if (StringUtils.isNotEmpty(objParams)) {
            try {
                JSONObject objParamsJson = JSON.parseObject(objParams);
                if(StringUtils.isNotBlank(objParamsJson.getString("discountable_amount"))) {
                    //可打折金额
                    model.setDiscountableAmount(objParamsJson.getString("discountable_amount"));
                }
                if(StringUtils.isNotBlank(objParamsJson.getString("undiscountable_amount"))) {
                    //不可打折金额
                    model.setUndiscountableAmount(objParamsJson.getString("undiscountable_amount"));
                }
            } catch (Exception e) {
                _log.error("{}objParams参数格式错误！", logPrefix);
            }
        }
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(payConfig.getNotifyUrl(getChannelName()));
        // 设置同步跳转地址
        alipay_request.setReturnUrl(payConfig.getReturnUrl(getChannelName()));
        String aliResult;
        String codeUrl = "";
        JSONObject retObj = buildRetObj();
        try {
            aliResult = client.execute(alipay_request).getBody();
            JSONObject aliObj = JSONObject.parseObject(aliResult);
            JSONObject aliResObj = aliObj.getJSONObject("alipay_trade_precreate_response");
            codeUrl = aliResObj.getString("qr_code");
        } catch (AlipayApiException e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[" + e.getErrMsg() + "]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        } catch (Exception e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[调取通道异常]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
        _log.info("{}生成支付宝二维码：codeUrl={}", logPrefix, codeUrl);
        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrderId, null);

        String codeImgUrl = payConfig.getPayUrl() + "/qrcode_img_get?url=" + codeUrl + "&widht=200&height=200";
        StringBuffer payForm = new StringBuffer();
        String toPayUrl = payConfig.getPayUrl() + "/alipay/pay_"+type+".htm";
        payForm.append("<form style=\"display: none\" action=\""+toPayUrl+"\" method=\"post\">");
        payForm.append("<input name=\"mchOrderNo\" value=\""+payOrder.getMchOrderNo()+"\" >");
        payForm.append("<input name=\"payOrderId\" value=\""+payOrder.getPayOrderId()+"\" >");
        payForm.append("<input name=\"amount\" value=\""+payOrder.getAmount()+"\" >");
        payForm.append("<input name=\"codeUrl\" value=\""+codeUrl+"\" >");
        payForm.append("<input name=\"codeImgUrl\" value=\""+codeImgUrl+"\" >");
        payForm.append("<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >");
        payForm.append("</form>");
        payForm.append("<script>document.forms[0].submit();</script>");

        retObj.put("payOrderId", payOrderId);
        JSONObject payInfo = new JSONObject();
        payInfo.put("payUrl",payForm);
        payInfo.put("payMethod",PayConstant.PAY_METHOD_FORM_JUMP);
        retObj.put("payParams", payInfo);

        _log.info("###### 商户统一下单处理完成 ######");
        return retObj;
    }

    /**
     * 支付宝红包无线支付
     * @param payOrder
     * @return
     */
    public JSONObject doAliPayCouponAppReq(PayOrder payOrder) {
        String logPrefix = "【支付宝红包无线支付下单】";
        String payOrderId = payOrder.getPayOrderId();
        AlipayConfig alipayConfig = new AlipayConfig(getPayParam(payOrder));
        AlipayClient client = new DefaultAlipayClient(alipayConfig.getReqUrl(), alipayConfig.getAppId(), alipayConfig.getPrivateKey(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getAlipayPublicKey(), AlipayConfig.SIGNTYPE);
        AlipayFundCouponOrderAppPayRequest alipay_request = new AlipayFundCouponOrderAppPayRequest();
        // 封装请求支付信息
        AlipayFundCouponOrderAppPayModel model = new AlipayFundCouponOrderAppPayModel();
        model.setOutOrderNo(payOrderId);
        model.setOutRequestNo(payOrderId);
        model.setOrderTitle(payOrder.getSubject());
        model.setAmount(AmountUtil.convertCent2Dollar(payOrder.getAmount().toString()));
        model.setPayTimeout("2h");

        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(payConfig.getNotifyUrl(getChannelName()));
        // 设置同步跳转地址
        alipay_request.setReturnUrl(payConfig.getReturnUrl(getChannelName()));
        String payUrl = null;
        JSONObject retObj = buildRetObj();
        try {
            String body = client.pageExecute(alipay_request).getBody();
            //payUrl = buildWapUrl(body);
            payUrl = body;
        } catch (AlipayApiException e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[" + e.getErrMsg() + "]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        } catch (Exception e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[调取通道异常]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
        _log.info("{}生成跳转路径：payUrl={}", logPrefix, payUrl);

        if(StringUtils.isBlank(payUrl)) {
            retObj.put("errDes", "调用支付宝异常!");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrderId, null);
        _log.info("{}生成请求支付宝数据,req={}", logPrefix, alipay_request.getBizModel());
        _log.info("###### 商户统一下单处理完成 ######");

        retObj.put("payOrderId", payOrderId);
        JSONObject payParams = new JSONObject();
        payParams.put("payUrl", payUrl);
        payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);
        retObj.put("payParams", payParams);
        return retObj;
    }

    /**
     * 支付宝红包页面支付
     * @param payOrder
     * @return
     */
    public JSONObject doAliPayCouponPageReq(PayOrder payOrder) {
        String logPrefix = "【支付宝红包页面支付下单】";
        String payOrderId = payOrder.getPayOrderId();
        AlipayConfig alipayConfig = new AlipayConfig(getPayParam(payOrder));
        AlipayClient client = new DefaultAlipayClient(alipayConfig.getReqUrl(), alipayConfig.getAppId(), alipayConfig.getPrivateKey(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getAlipayPublicKey(), AlipayConfig.SIGNTYPE);
        AlipayFundCouponOrderPagePayRequest alipay_request = new AlipayFundCouponOrderPagePayRequest();
        // 封装请求支付信息
        AlipayFundCouponOrderPagePayModel model = new AlipayFundCouponOrderPagePayModel();
        model.setOutOrderNo(payOrderId);
        model.setOutRequestNo(payOrderId);
        model.setOrderTitle(payOrder.getSubject());
        model.setAmount(AmountUtil.convertCent2Dollar(payOrder.getAmount().toString()));
        model.setPayTimeout("2h");

        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(payConfig.getNotifyUrl(getChannelName()));
        // 设置同步跳转地址
        alipay_request.setReturnUrl(payConfig.getReturnUrl(getChannelName()));
        String payUrl = null;
        JSONObject retObj = buildRetObj();
        try {
            String body = client.pageExecute(alipay_request).getBody();
            //payUrl = buildWapUrl(body);
            payUrl = body;
        } catch (AlipayApiException e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[" + e.getErrMsg() + "]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        } catch (Exception e) {
            _log.error(e, "");
            retObj.put("errDes", "下单失败[调取通道异常]");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
        _log.info("{}生成跳转路径：payUrl={}", logPrefix, payUrl);

        if(StringUtils.isBlank(payUrl)) {
            retObj.put("errDes", "调用支付宝异常!");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrderId, null);
        _log.info("{}生成请求支付宝数据,req={}", logPrefix, alipay_request.getBizModel());
        _log.info("###### 商户统一下单处理完成 ######");

        retObj.put("payOrderId", payOrderId);
        JSONObject payParams = new JSONObject();
        payParams.put("payUrl", payUrl);
        payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);
        retObj.put("payParams", payParams);
        return retObj;
    }

    /**
     * 生成支付宝wap支付url,解析html
     * @param formHtml
     * @return
     */
    String buildWapUrl(String formHtml) {
        Document doc = Jsoup.parse(formHtml);
        Elements formElements = doc.getElementsByTag("form");
        Element formElement = formElements.get(0);
        String action = formElement.attr("action");
        String biz_content = "";
        Elements inputElements = formElement.getElementsByTag("input");
        for(Element inputElement : inputElements) {
            String name = inputElement.attr("name");
            String value = inputElement.attr("value");
            if("biz_content".equals(name)) {
                biz_content = value;
                biz_content = value.replaceAll("&quot;", "\"");
                break;
            }
        }
        return action + "&biz_content=" + biz_content;
    }

}
