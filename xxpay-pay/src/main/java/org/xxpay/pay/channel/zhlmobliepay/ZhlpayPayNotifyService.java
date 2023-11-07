package org.xxpay.pay.channel.zhlmobliepay;

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
import java.util.*;

import static org.xxpay.pay.channel.zhlmobliepay.ZhlpayPaymentService.doSignString;

@Service
public class ZhlpayPayNotifyService extends BasePayNotify {
    private static final MyLog _log = MyLog.getLog(ZhlpayPayNotifyService.class);


    @Override
    public String getChannelName() {
        return ZhlpayConfig.CHANNEL_NAME;
    }


    @Override
    public JSONObject doNotify(Object notifyData) {
        String logPrefix = "【处理掌灵移动支付回调】";
        _log.info("====== 开始处理掌灵移动支付回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
        JSONObject retObj = buildRetObj();
        Map<String, Object> payContext = new HashMap<String, Object>();
        String respString = ZhlpayConfig.RETURN_VALUE_FAIL;
        String retJsonStr = Util.JsonReq(request);//返回的json
        try {
//            JSONObject params = new JSONObject();
//            Map<String, String[]> parameterMap = request.getParameterMap();
//            for (String s : parameterMap.keySet()) {
//                params.put(s, parameterMap.get(s)[0]);
//            }
            payContext.put("parameters", retJsonStr);
            if (!verifyPayParams(payContext)) {
                retObj.put(PayConstant.RESPONSE_RESULT, respString);
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
            _log.info("====== 完成处理掌灵移动支付回调通知 ======");
            respString = ZhlpayConfig.RETURN_VALUE_SUCCESS;
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
        JSONObject params = JSONObject.parseObject((String)payContext.get("parameters"));
        Object  mchntOrderNo = params.get("mchntOrderNo");
        Object  paySt = params.get("paySt");
        Object  signature = params.get("signature");

        // 查询payOrder记录
        String payOrderId = mchntOrderNo.toString();
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
        if (payOrder == null) {
            _log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "Can't found payOrder");
            return false;
        }
        if("3".equals(paySt.toString())){
            payContext.put("retMsg", "上游通知失败");
            return false;
        }
        PayPassageAccount payPassageAccount = rpcCommonService.rpcPayPassageAccountService.findById(payOrder.getPassageAccountId());
        JSONObject object = JSONObject.parseObject(payPassageAccount.getParam());
        String apikey = object.getString("APP_KEY");
        TreeMap<String,Object> resParam =  this.jsonToTreeMap(params);
        String signTemp = doSignString(resParam)+"key="+apikey;
        String validSign = Util.md5(signTemp);
        if (!validSign.equals(signature.toString().toUpperCase())) {
            payContext.put("retMsg", "验签失败");
            return false;
        }

        // 核对金额
        long outPayAmt = new BigDecimal(String.valueOf(params.get("amount"))).longValue();
        long dbPayAmt = payOrder.getAmount().longValue();
        if (dbPayAmt != outPayAmt) {
            _log.error("金额不一致. outPayAmt={},payOrderId={}", outPayAmt, payOrderId);
            payContext.put("retMsg", "金额不一致");
            return false;
        }
        payContext.put("payOrder", payOrder);
        return true;
    }

    private TreeMap<String,Object> jsonToTreeMap(JSONObject params) {
        params.remove("signature");
        Set keySet = params.keySet();
        Iterator it = keySet.iterator();
        TreeMap<String, Object> resMap = new TreeMap<String, Object>();
        while (it.hasNext()) {
            String key = it.next().toString();
            if (StringUtils.isNotBlank(String.valueOf(params.get(key))))
                resMap.put(key, params.get(key));
        }
        return resMap;
    }

}
