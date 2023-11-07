package org.xxpay.pay.channel.tlpay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayNotify;
import org.xxpay.pay.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class AllinpayPayNotifyService extends BasePayNotify {
    private static final MyLog _log = MyLog.getLog(AllinpayPayNotifyService.class);


    @Override
    public String getChannelName() {
        return AllinpayConfig.CHANNEL_NAME;
    }


    @Override
    public JSONObject doNotify(Object notifyData) {
        String logPrefix = "【处理通联支付回调】";
        _log.info("====== 开始处理通联支付回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
        Map<String, String[]> parameterMap = request.getParameterMap();
//        String retJsonStr = Util.JsonReq(request);//返回的json
        JSONObject retObj = buildRetObj();
        Map<String, Object> payContext = new HashMap<String, Object>();
        String respString = AllinpayConfig.RETURN_VALUE_FAIL;
        try {
            JSONObject params = new JSONObject();
            for (String s : parameterMap.keySet()) {
                params.put(s, parameterMap.get(s)[0]);
            }
            payContext.put("parameters", params);
            if (!verifyPayParams(payContext)) {
                retObj.put(PayConstant.RESPONSE_RESULT, AllinpayConfig.RETURN_VALUE_FAIL);
                return retObj;
            }

            PayOrder payOrder = (PayOrder) payContext.get("payOrder");
            Byte payStatus = payOrder.getStatus();

            if (payStatus != PayConstant.PAY_STATUS_SUCCESS && payStatus != PayConstant.PAY_STATUS_COMPLETE) {
                int updatePayOrderRows = rpcCommonService.rpcPayOrderService.updateStatus4Success(payOrder.getPayOrderId());
                if (updatePayOrderRows != 1) {
                    _log.error("{}更新支付状态失败,将payOrderId={},更新payStatus={}失败", logPrefix, payOrder.getPayOrderId(), PayConstant.PAY_STATUS_SUCCESS);
                    retObj.put(PayConstant.RESPONSE_RESULT, "处理订单失败");
                    return retObj;
                }
                _log.info("{}更新支付状态成功,将payOrderId={},更新payStatus={}成功", logPrefix, payOrder.getPayOrderId(), PayConstant.PAY_STATUS_SUCCESS);
                    payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
            }
            // 业务系统后端通知
            baseNotify4MchPay.doNotify(payOrder, true);
            _log.info("====== 完成处理通联支付回调通知 ======");
            respString = AllinpayConfig.RETURN_VALUE_SUCCESS;
        } catch (Exception e) {
            _log.error(e, logPrefix + "处理异常");
        }
        retObj.put(PayConstant.RESPONSE_RESULT, respString);
        return retObj;
    }

    /**
     * 验证通联支付通知参数
     *
     * @return
     */
    public boolean verifyPayParams(Map<String, Object> payContext) {
        JSONObject params = (JSONObject) payContext.get("parameters");
        TreeMap<String, String> signParam = new TreeMap<>();
        JsonToMap(params, signParam);
        // 校验结果是否成功
        Object total_fee = params.get("trxamt");
        Object out_trade_no = params.get("cusorderid");
        Object sign = params.get("sign");

        // 查询payOrder记录
        String payOrderId = out_trade_no.toString();
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
        if (payOrder == null) {
            _log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "Can't found payOrder");
            return false;
        }

        PayPassageAccount payPassageAccount = rpcCommonService.rpcPayPassageAccountService.findById(payOrder.getPassageAccountId());
        JSONObject object = JSONObject.parseObject(payPassageAccount.getParam());
        String APP_KEY = object.getString("APP_KEY");
        String validSign = getMd5Sign(signParam, APP_KEY);
        if (!validSign.equals(sign)) {
            payContext.put("retMsg", "验签失败");
            return false;
        }

        // 核对金额
        long outPayAmt = new BigDecimal(total_fee.toString()).longValue();
        long dbPayAmt = payOrder.getAmount().longValue();
        if (dbPayAmt != outPayAmt) {
            _log.error("金额不一致. outPayAmt={},payOrderId={}", total_fee, payOrderId);
            payContext.put("retMsg", "金额不一致");
            return false;
        }
        payContext.put("payOrder", payOrder);
        return true;
    }

    static String getMd5Sign(TreeMap<String, String> signParam, String app_key) {
        StringBuffer stringBuffer = new StringBuffer();
        signParam.remove("sign");
        signParam.put("key",app_key);
        for (String s : signParam.keySet()) {
            stringBuffer.append(s + "=" + signParam.get(s) + "&");
        }
        String s = "";
        String signStr =stringBuffer.toString();
        try {
            s = Util.md5(signStr.substring(0,signStr.length()-1)).toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }
    private static void JsonToMap(JSONObject stObj, Map<String, String> resultMap) {

        if (stObj == null) {
            return;
        }
        Set<String> strings = stObj.keySet();
        for (String s : strings) {
            String key = s;
            Object value = stObj.get(key);
            if (value != null && StringUtils.isNotBlank(value.toString()))
                resultMap.put(key, value.toString());
        }
    }

}
