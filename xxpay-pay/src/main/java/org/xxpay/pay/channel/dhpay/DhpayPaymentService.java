package org.xxpay.pay.channel.dhpay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.XXPayUtil;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.mq.BaseNotify4MchPay;

import java.text.DecimalFormat;

/**
 * @author: dingzhiwei
 * @date: 18/3/1
 * @description: 个付通支付接口
 */
@Service
public class DhpayPaymentService extends BasePayment {

    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    private static final MyLog _log = MyLog.getLog(DhpayPaymentService.class);

    @Override
    public String getChannelName() {
        return DhpayConfig.CHANNEL_NAME;
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
            case DhpayConfig.CHANNEL_NAME_WXPAY_QR:
                retObj = doPayQrReq(payOrder, "4");
                break;
            case DhpayConfig.CHANNEL_NAME_ALIPAY_QR:
                retObj = doPayQrReq(payOrder, "2");
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

        String logPrefix = "【个付通订单查询】";
        String payOrderId = payOrder.getPayOrderId();

        _log.info("{}开始查询个付通订单,payOrderId={}", logPrefix, payOrderId);
        DhpayConfig dhpayConfig = new DhpayConfig(getPayParam(payOrder));
        JSONObject paramMap = new JSONObject();
        paramMap.put("order_num", payOrderId);
        paramMap.put("username", dhpayConfig.getUsername());
        Long currtime = System.currentTimeMillis();
        paramMap.put("time", currtime);
        paramMap.put("APPID", dhpayConfig.getAPP_ID());
        String _sign = DigestUtils.md5Hex("APPID=" + dhpayConfig.getAPP_ID() + "&username=" + dhpayConfig.getUsername()
                + "&time=" + currtime + dhpayConfig.getAPP_KEY());
        paramMap.put("sign", _sign);
        JSONObject retObj = buildRetObj();
        retObj.put("status", 1); // 支付中
        Integer status = 0;
        Integer code = 0;

        try {
            String reqData = XXPayUtil.genUrlParams(paramMap);
            _log.info("{}请求数据:{}", logPrefix, reqData);
            String url = "http://47.74.226.230/api/index/getorderstate";
            String result = XXPayUtil.call4Post(url + "?" + reqData);
            _log.info("{}返回数据:{}", logPrefix, result);
            JSONObject resObj = JSONObject.parseObject(result);
            code = resObj.getInteger("code");
            status = resObj.getInteger("data");
            if (code == 200) {
                if (1 == status) {
                    retObj.put("status", 2); // 成功
                } else if (4 == status) {
                    retObj.put("status", 1); // 支付中
                }
            } else {
                retObj.put("message", result);
            }

        } catch (Exception e) {
            _log.error(e, "");
        }

        return retObj;
    }

    /**
     * 扫码支付
     * 
     * @param payOrder
     * @return
     */
    public JSONObject doPayQrReq(PayOrder payOrder, String channel) {
        DhpayConfig dhpayConfig = new DhpayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        JSONObject paramMap = new JSONObject();

        try {

            String username = dhpayConfig.getUsername();
            String APPID = dhpayConfig.getAPP_ID();
            String APPKEY = dhpayConfig.getAPP_KEY();
            Long currtime = System.currentTimeMillis();
            String order_num = payOrder.getPayOrderId();
            String notify_url = payConfig.getNotifyUrl(getChannelName());
            Integer type = Integer.valueOf(channel);
            paramMap.put("username", username);
            paramMap.put("time", currtime);
            paramMap.put("APPID", APPID); // 单位元
            String _sign = DigestUtils.md5Hex("APPID=" + APPID + "&username=" + username + "&time=" + currtime + APPKEY);
            paramMap.put("sign", _sign); // 订单号
            paramMap.put("order_num", order_num); // 订单号
            DecimalFormat df = new DecimalFormat("######0.00");
            String amount = df.format(payOrder.getAmount().doubleValue() / 100);
            paramMap.put("money", amount); // 订单号
            paramMap.put("type", type);
            paramMap.put("notify_url", payOrder.getNotifyUrl());
            String reqData = XXPayUtil.genUrlParams(paramMap);

            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(),
                    payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(),
                    payOrder.getPayOrderId(), updateCount);
            StringBuffer payForm = new StringBuffer();
            String toPayUrl = dhpayConfig.getReqUrl();
            payForm.append("<form style=\"display: none\" action=\"" + toPayUrl + "\" method=\"get\">");
            payForm.append("<input name=\"username\" value=\"" + username + "\" >");
            payForm.append("<input name=\"time\" value=\"" + currtime + "\" >");
            payForm.append("<input name=\"APPID\" value=\"" + APPID + "\" >");
            payForm.append("<input name=\"sign\" value=\"" + _sign + "\" >");
            payForm.append("<input name=\"order_num\" value=\"" + order_num + "\" >");
            payForm.append("<input name=\"money\" value=\"" + amount + "\" >");
            payForm.append("<input name=\"type\" value=\"" + type + "\" >");
            payForm.append("<input name=\"notify_url\" value=\"" + notify_url + "\" >");
            payForm.append("<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >");
            payForm.append("</form>");
            payForm.append("<script>document.forms[0].submit();</script>");

            // 支付链接地址
            retObj.put("payOrderId", payOrder.getPayOrderId()); // 设置支付订单ID
            JSONObject payParams = new JSONObject();
            payParams.put("payUrl", payForm);
            String payJumpUrl = toPayUrl + "?" + reqData;
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

}
