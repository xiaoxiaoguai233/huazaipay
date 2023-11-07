package org.xxpay.pay.channel.wh;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayNotify;
import org.xxpay.pay.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
public class WanhepayPayNotifyService extends BasePayNotify {
    private static final MyLog _log = MyLog.getLog(WanhepayPayNotifyService.class);


    @Override
    public String getChannelName() {
        return WanhepayConfig.CHANNEL_NAME;
    }


    @Override
    public JSONObject doNotify(Object notifyData) {
        String logPrefix = "【处理万和支付回调】";
        _log.info("====== 开始处理万和支付回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
        JSONObject retObj = buildRetObj();
        Map<String, Object> payContext = new HashMap<String, Object>();
        String respString = WanhepayConfig.RETURN_VALUE_FAIL;
        try {
            JSONObject params = new JSONObject();
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (String s : parameterMap.keySet()) {
                params.put(s, parameterMap.get(s)[0]);
            }
            payContext.put("parameters", params);

            if (!verifyPayParams(payContext)) {
                retObj.put(PayConstant.RESPONSE_RESULT, WanhepayConfig.RETURN_VALUE_FAIL);
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
                _log.error("{}更新支付状态成功,将payOrderId={},更新payStatus={}成功", logPrefix, payOrder.getPayOrderId(), PayConstant.PAY_STATUS_SUCCESS);
                payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
            }
            // 业务系统后端通知
            baseNotify4MchPay.doNotify(payOrder, true);
            _log.info("====== 完成处理万和支付回调通知 ======");
            respString = WanhepayConfig.RETURN_VALUE_SUCCESS;
        } catch (Exception e) {
            _log.error(e, logPrefix + "处理异常");
        }
        retObj.put(PayConstant.RESPONSE_RESULT, respString);
        return retObj;
    }

    /**
     * 验证万和支付通知参数
     *
     * @return
     */
    public boolean verifyPayParams(Map<String, Object> payContext) throws NoSuchAlgorithmException {
        JSONObject params = (JSONObject) payContext.get("parameters");
        String mchid = String.valueOf(params.get("mchid"));
        String paytype = String.valueOf(params.get("paytype"));
        Integer amount = Integer.parseInt(String.valueOf(params.get("amount")));
        String ordertime = String.valueOf(params.get("ordertime"));
        String orderstatus = String.valueOf(params.get("orderstatus"));
        String orderno = String.valueOf(params.get("orderno"));
        String mchorderno = String.valueOf(params.get("mchorderno"));
        String attach = String.valueOf(params.get("attach"));
        String sign = String.valueOf(params.get("sign"));

        // 查询payOrder记录
        String payOrderId = mchorderno;
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
        if (payOrder == null) {
            _log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "Can't found payOrder");
            return false;
        }
        if("0".equals(orderstatus)){
            payContext.put("retMsg", "上游通知失败");
            return false;
        }
        PayPassageAccount payPassageAccount = rpcCommonService.rpcPayPassageAccountService.findById(payOrder.getPassageAccountId());
        JSONObject object = JSONObject.parseObject(payPassageAccount.getParam());
        String apikey = object.getString("APP_KEY");
        String signTemp = "mchid="+mchid+"&paytype="+paytype+"&amount="+amount+"&ordertime="+ordertime+"&orderstatus="+orderstatus+"&orderno="+orderno+"&mchorderno="+mchorderno+"&"+apikey;
        String validSign = Util.md5(signTemp);
        if (!validSign.equals(sign)) {
            payContext.put("retMsg", "验签失败");
            return false;
        }

        // 核对金额
        long outPayAmt = amount.longValue();
        long dbPayAmt = payOrder.getAmount().longValue();
        if (dbPayAmt != outPayAmt) {
            _log.error("金额不一致. outPayAmt={},payOrderId={}", amount, payOrderId);
            payContext.put("retMsg", "金额不一致");
            return false;
        }
        payContext.put("payOrder", payOrder);
        return true;
    }




}
