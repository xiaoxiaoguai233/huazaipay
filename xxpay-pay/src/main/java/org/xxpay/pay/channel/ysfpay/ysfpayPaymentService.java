package org.xxpay.pay.channel.ysfpay;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.util.RedisUtil;

@Service
public class ysfpayPaymentService extends BasePayment {


    private static final MyLog _log = MyLog.getLog(ysfpayPaymentService.class);

    public final static String PAY_CHANNEL_YSFPAY_H5 = "ysfpay_h5";	            // 支付宝当面付之H5支付

    @Autowired public BaseNotify4MchPay baseNotify4MchPay;

    @Autowired YsfAsynCreateOrders ysfAsynCreateOrders;

    @Override
    public String getChannelName() {
        return PayConstant.CHANNEL_NAME_YSFPAY;
    }

    @Override
    public JSONObject pay(PayOrder payOrder) throws InterruptedException {
        String channelId = payOrder.getChannelId();
        JSONObject retObj;
        switch (channelId) {
            case PAY_CHANNEL_YSFPAY_H5:
                retObj = doYsfPayH5Req(payOrder,"h5");
                break;
            default:
                retObj = buildRetObj(PayConstant.RETURN_VALUE_FAIL, "不支持的支付宝渠道[channelId="+channelId+"]");
                break;
        }
        return retObj;
    }

    public JSONObject doYsfPayH5Req(PayOrder payOrder, String type) throws InterruptedException {
        String logPrefix = "【账户支付】";
        JSONObject retObj = new JSONObject();
        // 将订单更改为支付中

        ysfAsynCreateOrders.YsfPay_h5(payOrder);

//        String userId = payOrder.getChannelUser();
//        Long mchId = payOrder.getMchId();
//        Long amount = payOrder.getAmount();
//        // 扣账户余额
//        result = 1;
//        _log.info("{}userId={},mchId={},amount={},结果:{}", logPrefix, userId, mchId, amount, result);
//
//        // 扣款成功
//        if(result == 1){
//            result = rpcCommonService.rpcPayOrderService.updateStatus4Success(payOrder.getPayOrderId());
//            _log.info("[{}]更新订单状态为支付成功:payOrderId={},result={}", getChannelName(), payOrder.getPayOrderId(), result);
//            retObj.put(PayConstant.RESULT_PARAM_RESCODE, PayConstant.RESULT_VALUE_SUCCESS);
//            // 发送商户支付成功通知
//            if (result == 1) {
//                // 通知业务系统
//                baseNotify4MchPay.doNotify(payOrder, true);
//            }
//        }else {
//            // 明确为失败,可修改订单状态为失败
//            retObj.put(PayConstant.RESULT_PARAM_RESCODE, PayConstant.RESULT_VALUE_FAIL);
//            retObj.put("errDes", "扣余额失败");
//            retObj.put("errCode", "");
//        }
//        retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);

        // Redis参数设置
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "0");

        RedisUtil.setString(payOrder.getPayOrderId(), jsonObject.toJSONString(), 60);

        // formJump payUrl
        // urlJump  payUrl
        // codeImg  codeImgUrl

        retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);

        JSONObject payParams = new JSONObject();
        payParams.put("payMethod", "urlJump");
        payParams.put("payUrl", "http://192.168.75.1:3020/api/waiting/" + payOrder.getPayOrderId());
        retObj.put("payParams", payParams);
        return retObj;
    }

    @Override
    public JSONObject query(PayOrder payOrder) {
        return null;
    }

    @Override
    public JSONObject close(PayOrder payOrder) {
        return null;
    }
}
