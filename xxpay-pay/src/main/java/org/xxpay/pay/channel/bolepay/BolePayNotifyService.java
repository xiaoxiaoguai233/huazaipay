package org.xxpay.pay.channel.bolepay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayNotify;
import org.xxpay.pay.channel.bolepay.util.BolePayUtils;
import org.xxpay.pay.channel.gepay.GepayConfig;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class BolePayNotifyService extends BasePayNotify {
    private static final MyLog _log = MyLog.getLog(BolePayNotifyService.class);


    @Override
    public String getChannelName() {
        return BoleConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject doNotify(Object notifyData) {
        String logPrefix = "【处理博乐支付回调】";
        _log.info("====== 开始处理博乐支付回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
        JSONObject retObj = buildRetObj();
        Map<String, Object> payContext = new HashMap<String, Object>();
        String respString = BoleConfig.RETURN_VALUE_FAIL;
        try {
            JSONObject params = new JSONObject();
            String orderid = request.getParameter("orderid");
            String returncode = request.getParameter("returncode");
            String transaction_id = request.getParameter("transaction_id");
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (String s : parameterMap.keySet()) {
                params.put(s, parameterMap.get(s)[0]);
            }
            payContext.put("parameters", params);

            if (!verifyPayParams(payContext)) {
                retObj.put(PayConstant.RESPONSE_RESULT, GepayConfig.RETURN_VALUE_FAIL);
                return retObj;
            }
            Integer sta = Integer.valueOf(returncode);
            PayOrder payOrder = (PayOrder) payContext.get("payOrder");
            Byte payStatus = payOrder.getStatus();

            if (payStatus != PayConstant.PAY_STATUS_SUCCESS && payStatus != PayConstant.PAY_STATUS_COMPLETE) {
                int updatePayOrderRows = rpcCommonService.rpcPayOrderService.updateStatus4Success(payOrder.getPayOrderId());
                if (updatePayOrderRows != 1) {
                    _log.error("{}更新支付状态失败,将payOrderId={},更新payStatus={}失败", logPrefix, payOrder.getPayOrderId(), PayConstant.PAY_STATUS_SUCCESS);
                    retObj.put(PayConstant.RESPONSE_RESULT, "处理订单失败");
                    return retObj;
                }
                _log.error("{}更新支付状态成功,将payOrderId={},更新payStatus={}成功", logPrefix, payOrder.getPayOrderId(), PayConstant.PAY_STATUS_SUCCESS);
                payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
            }
            // 业务系统后端通知
            baseNotify4MchPay.doNotify(payOrder, true);
            _log.info("====== 完成处理博乐支付回调通知 ======");
            respString = BoleConfig.RETURN_VALUE_SUCCESS;
        } catch (Exception e) {
            _log.error(e, logPrefix + "处理异常");
        }
        retObj.put(PayConstant.RESPONSE_RESULT, respString);
        return retObj;
    }

    /**
     * 验证博乐支付通知参数
     *
     * @return
     */
    public boolean verifyPayParams(Map<String, Object> payContext) {
        JSONObject params = (JSONObject) payContext.get("parameters");
        TreeMap<String, String> signParam = new TreeMap<>();
        JsonToMap(params, signParam);
        // 校验结果是否成功
        Object orderid = params.get("orderid");
        Object sign = params.get("sign");
        Object returncode = params.get("returncode");
        Object memberid = params.get("memberid");
        Object amount = params.get("amount");

        // 查询payOrder记录
        String payOrderId = orderid.toString();
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
        if (payOrder == null) {
            _log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "Can't found payOrder");
            return false;
        }

        //更新三方单号
        payOrder.setChannelOrderNo(memberid.toString());

        PayPassageAccount payPassageAccount = rpcCommonService.rpcPayPassageAccountService.findById(payOrder.getPassageAccountId());
        JSONObject object = JSONObject.parseObject(payPassageAccount.getParam());
        String APP_KEY = object.getString("APP_KEY");
        String validSign = getMd5Sign(signParam, APP_KEY);
        if (!validSign.equals(sign)) {
            payContext.put("retMsg", "验签失败");
            return false;
        }

        // 核对金额
        long outPayAmt = new BigDecimal(amount.toString()).multiply(new BigDecimal("100")).longValue();
        long dbPayAmt = payOrder.getAmount().longValue();
        if (dbPayAmt != outPayAmt) {
            _log.error("金额不一致. outPayAmt={},payOrderId={}", amount, payOrderId);
            payContext.put("retMsg", "金额不一致");
            return false;
        }
        payContext.put("payOrder", payOrder);
        return true;
    }

    static String getMd5Sign(TreeMap<String, String> signParam, String app_key) {
        StringBuffer stringBuffer = new StringBuffer();
        signParam.remove("sign");
        for (String s : signParam.keySet()) {
            stringBuffer.append(s + "=" + signParam.get(s) + "&");
        }
        stringBuffer.append("key=" + app_key);
        String s = "";
        try {
            s = BolePayUtils.md5(stringBuffer.toString()).toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }


    public static void main(String[] args) {
        TreeMap<String, String> signParam = new TreeMap<>();

        signParam.put("memberid", "190809516");
        signParam.put("orderid", "P01201907291924375510008");
        signParam.put("amount", "100");
        signParam.put("datetime", "2019-07-29 19:24:37");
        signParam.put("returncode", "00");
        signParam.put("transaction_id", "1111111111");
        System.out.println(getMd5Sign(signParam, "k8jn6i0fxva92ytmhpt8kt5bzmttw9c0"));
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
