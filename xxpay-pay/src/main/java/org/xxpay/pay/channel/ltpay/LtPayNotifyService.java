package org.xxpay.pay.channel.ltpay;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayNotify;
import org.xxpay.pay.channel.gepay.GepayConfig;
import org.xxpay.pay.channel.ltpay.utils.LtUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: carter
 * @date: 2019-07-28
 * @description: 龙腾支付回调
 */
@Service
public class LtPayNotifyService extends BasePayNotify {

    private static final MyLog _log = MyLog.getLog(LtPayNotifyService.class);

    @Override
    public String getChannelName() {
        return LtpayConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject doNotify(Object notifyData) {

        String logPrefix = "【处理龙腾支付回调】";
        _log.info("====== 开始处理龙腾支付回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
        JSONObject retObj = buildRetObj();
        Map<String, Object> payContext = new HashMap<String, Object>();
        PayOrder payOrder;
        String respString = LtpayConfig.RETURN_VALUE_FAIL;
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
            
            payOrder = (PayOrder) payContext.get("payOrder");
            
            //上游返回成功
            if(returncode.equals("00")){
                
                byte payStatus = payOrder.getStatus(); //-1：支付失败，  0：订单生成，1：支付中，2：支付成功，3：业务处理完成，-2：订单过期
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
                _log.info("====== 完成处理龙腾支付回调通知 ======");
                respString = LtpayConfig.RETURN_VALUE_SUCCESS;
                
            }

        } catch (Exception e) {
            _log.error(e, logPrefix + "处理异常");
        }
        retObj.put(PayConstant.RESPONSE_RESULT, respString);
        return retObj;

    }
    
    
    /**
     * 验证龙腾支付通知参数
     *
     * @return
     */
    public boolean verifyPayParams(Map<String, Object> payContext) {
        JSONObject params = (JSONObject) payContext.get("parameters");
        TreeMap<String, String> signParam = new TreeMap<>();
        LtUtils.JsonToMap(params, signParam);
        // 校验结果是否成功
        Object orderid = params.get("orderid");//订单号
        Object sign = params.get("sign");
        Object returncode = params.get("returncode");
        Object memberid = params.get("memberid");//商户编号
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
        String validSign = LtUtils.getMd5Sign(signParam, APP_KEY);
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

    

}
