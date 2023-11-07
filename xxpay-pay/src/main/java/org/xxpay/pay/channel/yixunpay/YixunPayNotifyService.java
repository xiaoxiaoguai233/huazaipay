package org.xxpay.pay.channel.yixunpay;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayNotify;
import org.xxpay.pay.channel.gepay.GepayConfig;
import org.xxpay.pay.channel.yixunpay.utils.RequestUtil;
import org.xxpay.pay.channel.yixunpay.utils.SignUtils;
import org.xxpay.pay.channel.yixunpay.utils.YixunpayUtils;
import org.xxpay.pay.util.Util;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: carter
 * @date: 2019-07-31
 * @description: 易迅回调
 */
@Service
public class YixunPayNotifyService extends BasePayNotify {

    private static final MyLog _log = MyLog.getLog(YixunPayNotifyService.class);

    @Override
    public String getChannelName() {
        return YixunpayConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject doNotify(Object notifyData) {

        String logPrefix = "【处理易迅回调】";
        _log.info("====== 开始处理易迅回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
        String retJsonStr = Util.JsonReq(request);//返回的json
        JSONObject retObj = buildRetObj();
        Map<String, Object> payContext = new HashMap<String, Object>();
        PayOrder payOrder;
        String respString = YixunpayConfig.RETURN_VALUE_FAIL;
        try {
            payContext.put("parameters", retJsonStr);
            if (!verifyPayParams(payContext)) {
                retObj.put(PayConstant.RESPONSE_RESULT, GepayConfig.RETURN_VALUE_FAIL);
                return retObj;
            }
            
            payOrder = (PayOrder) payContext.get("payOrder");
                
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
            _log.info("====== 完成处理易迅回调通知 ======");
            respString = YixunpayConfig.RETURN_VALUE_SUCCESS;
        } catch (Exception e) {
            _log.error(e, logPrefix + "处理异常");
        }
        retObj.put(PayConstant.RESPONSE_RESULT, respString);
        return retObj;

    }
    
    
    /**
     * 验证易迅通知参数
     *
     * @return
     */
    public boolean verifyPayParams(Map<String, Object> payContext) {
        JSONObject params = JSONObject.parseObject((String)payContext.get("parameters"));
        SortedMap<String, String> signParam = new TreeMap<String, String>();
        YixunpayUtils.JsonToMap(params, signParam);
        // 校验结果是否成功
        
        Object upOrderId = params.get("upOrderId");//下单成功返回，支付通道标记
        //Object merchantId = params.get("merchantId");
        Object orderId = params.get("orderId");//下单成功返回，平台订单号
        Object sign = params.get("sign");
        Object orderAmt = params.get("orderAmt");
        //Object status = params.get("status");

        // 查询payOrder记录
        String payOrderId = orderId.toString();
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
        if (payOrder == null) {
            _log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "Can't found payOrder");
            return false;
        }

        PayPassageAccount payPassageAccount = rpcCommonService.rpcPayPassageAccountService.findById(payOrder.getPassageAccountId());
        JSONObject object = JSONObject.parseObject(payPassageAccount.getParam());
        String APP_KEY = object.getString("APP_KEY");
        signParam.remove("sign");
        String validSign = RequestUtil.mapToStringAndTrim(signParam) + "&key=" + APP_KEY;
        try {
            validSign = SignUtils.md5Encode(validSign, "utf-8").toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!validSign.equals(sign)){
            payContext.put("retMsg", "验签失败");
            return false;
        }
        
        //核对金额
        long outPayAmt = new BigDecimal(orderAmt.toString()).longValue();
        long dbPayAmt = payOrder.getAmount().longValue();
        if (dbPayAmt != outPayAmt) {
            _log.error("金额不一致. outPayAmt={},payOrderId={}", orderAmt.toString(), payOrderId);
            payContext.put("retMsg", "金额不一致");
            return false;
        }
        
        //更新三方单号
        payOrder.setChannelOrderNo(upOrderId.toString());
        payContext.put("payOrder", payOrder);
        return true;
    }

    

}
