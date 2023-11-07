package org.xxpay.pay.channel.alipay;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.*;
import org.xxpay.pay.ctrl.constant.CS;
import org.xxpay.pay.service.RpcCommonService;
import org.xxpay.pay.util.RedisUtil;
import java.net.URLEncoder;

@Component
@Slf4j
public class AlipayAsynCreateOrders {

    private static final MyLog _log = MyLog.getLog(AlipayAsynCreateOrders.class);

    @Autowired public RpcCommonService rpcCommonService;

    /**
     * 查询订单状态
     * @throws InterruptedException
     */
    @Async("mqExecutor")
    public void Alipay_SLRJ_App(PayOrder payOrder) throws Exception {
        String logPrefix = "【8024-支付宝原生任额-8024】";
        JSONObject retObj = new JSONObject();

        _log.info("{} ==== {} ", logPrefix, payOrder.getPayOrderId());

        // 获取收款账号
        ZSlrjAccount zSlrjAccount = rpcCommonService.rpcZSlrjAccountService.selectReceiveAccountByUpdateTime();
        if(zSlrjAccount == null){
            retObj.put(PayConstant.RESULT_PARAM_ERRCODE, PayConstant.RESULT_VALUE_FAIL);        // errCode -1
            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "获取收款账号失败");
//            return retObj;
        }

        // 给订单添加手机号, 和码商名称

        AssistantInfo assistant = null;
        SysUser sysUser = null;

        String asstname = "";
        if (zSlrjAccount.getParentId() >= 30000000){
            assistant = rpcCommonService.rpcAsstInfoService.findByAssistantId(zSlrjAccount.getParentId());
            asstname = assistant.getAssistantName();
        }else {
            sysUser = rpcCommonService.rpcSysService.findByUserId(zSlrjAccount.getParentId());
            asstname = sysUser.getNickName();
        }


        int res_update_phone = rpcCommonService.rpcPayOrderService.updatePhoneAndChannelUserByOrderId(payOrder.getPayOrderId(), zSlrjAccount.getPhone(), asstname);
        if(res_update_phone == 0){
            retObj.put(PayConstant.RESULT_PARAM_ERRCODE, PayConstant.RESULT_VALUE_FAIL);        // errCode -1
            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "订单更新收款账号失败");
//            return retObj;
        }

        // TODO 1. 获取订单号
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", "1");
        jsonObject.put("invoice_amt", String.valueOf(payOrder.getAmount() / 100));
        jsonObject.put("value", String.valueOf(payOrder.getAmount() / 100));
        jsonObject.put("auth_token", zSlrjAccount.getToken());

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), String.valueOf(jsonObject));

        Request request_getOrder = new Request.Builder()
                .url("https://app.homeinns.com/api/v4/giftcards/buy")
                .post(body)
                .build();

        Response response = new OkHttpClient().newCall(request_getOrder).execute();
        JSONObject res_orderSn_json = JSONObject.parseObject(response.body().string());

        _log.info("{} === 拉起订单结果： {} ===", payOrder.getPayOrderId(), res_orderSn_json);

        String orderSn = null;      // SLRJ平台订单
        // 获取支付链接（过期时间24H）成功
        if (res_orderSn_json.getString("result").equals("购买成功")){
            orderSn = res_orderSn_json.getJSONObject("data").getString("order_no");
        }else{
            retObj.put(PayConstant.RESULT_PARAM_ERRCODE, PayConstant.RESULT_VALUE_FAIL);        // errCode -1
            retObj.put(PayConstant.RESULT_PARAM_ERRDES, "获取支付链接失败，请联系相关技术人员1002");

            _log.info("{} === 获取支付链接失败，请联系相关技术人员1002 ===", payOrder.getPayOrderId());
//            return retObj;
        }


        // TODO 2. 获取支付链接
        jsonObject = new JSONObject();
        jsonObject.put("order_no", orderSn);
        jsonObject.put("price", String.valueOf(payOrder.getAmount() / 100));
        jsonObject.put("auth_token", zSlrjAccount.getToken());

        RequestBody body_payLink = RequestBody.create(
                MediaType.parse("application/json"), String.valueOf(jsonObject));

        Request request_payLink = new Request.Builder()
                .url("https://app.homeinns.com/api/v4/giftcards/sign")
                .post(body_payLink)
                .build();

        Response response_payLink = new OkHttpClient().newCall(request_payLink).execute();
        JSONObject res_payLink_json = JSONObject.parseObject(response_payLink.body().string());

        _log.info("{} === 拉起支付链接结果： {} ===", payOrder.getPayOrderId(), res_payLink_json);

        // 将结果链接编码
        String result = "alipays://plarformapi/startapp?appId=20000125&orderSuffix=" + URLEncoder.encode( res_payLink_json.getString("result"), "UTF-8" );

        // Redis添加查单信息
        JSONObject redis_param = new JSONObject();
        redis_param.put("payOrderId", payOrder.getPayOrderId());
        redis_param.put("phone", zSlrjAccount.getPhone());
        redis_param.put("token", zSlrjAccount.getToken());
        redis_param.put("orderSn", orderSn);
        redis_param.put("amount", String.valueOf(payOrder.getAmount() / 100));
        redis_param.put("status", "1");
        redis_param.put("method", "alipay");
        redis_param.put("payLink", result);
        RedisUtil.setString(payOrder.getPayOrderId(), redis_param.toJSONString(), CS.CHANNEL_KEY_TIME);

        // 更改支付状态为支付中
        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), null);

        System.out.println(redis_param.toJSONString());
    }
}
