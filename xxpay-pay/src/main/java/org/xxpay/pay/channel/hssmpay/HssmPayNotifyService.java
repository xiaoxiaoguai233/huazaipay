package org.xxpay.pay.channel.hssmpay;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayNotify;
import org.xxpay.pay.channel.gepay.GepayConfig;
import org.xxpay.pay.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class HssmPayNotifyService extends BasePayNotify {

    private static final MyLog _log = MyLog.getLog(HssmPayNotifyService.class);


    @Override
    public String getChannelName() {
        return HssmConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject doNotify(Object notifyData) {
        String logPrefix = "【处理弘顺商贸支付回调】";
        _log.info("====== 开始处理弘顺商贸支付回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
//        String retJsonStr = Util.JsonReq(request);
//        JSONObject retJson= JSONObject.parseObject(retJsonStr);
        _log.info("=======弘顺商贸支付返回结果======="+request.getParameterMap());
        JSONObject retObj = buildRetObj();
        Map<String, Object> payContext = new HashMap<String, Object>();
        String respString = HssmConfig.RETURN_VALUE_FAIL;
        try {
            JSONObject params = new JSONObject();
            String status = request.getParameter("status");
            String retCode = request.getParameter("retCode");
            String merOrderNo = request.getParameter("merOrderNo");
            String orderPrice = request.getParameter("orderPrice");
            String message = request.getParameter("message");
            String sign = request.getParameter("sign");

            _log.info("=========="+sign);
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (String s : parameterMap.keySet()) {
                params.put(s, parameterMap.get(s)[0]);
            }

            payContext.put("parameters", params);
            if(!"SUCCESS".equals(status)){
                retObj.put(PayConstant.RESPONSE_RESULT, GepayConfig.RETURN_VALUE_FAIL);
                return retObj;
            }

            if (!verifyPayParams(payContext)) {
                retObj.put(PayConstant.RESPONSE_RESULT, GepayConfig.RETURN_VALUE_FAIL);
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
            _log.info("====== 完成处理弘顺商贸回调通知 ======");
            respString = HssmConfig.RETURN_VALUE_SUCCESS;
        } catch (Exception e) {
            _log.error(e, logPrefix + "处理异常");
        }
        retObj.put(PayConstant.RESPONSE_RESULT, respString);
        return retObj;
    }

    /**
     * 验证新诚信支付通知参数
     *
     * @return
     */
    public boolean verifyPayParams(Map<String, Object> payContext)throws NoSuchAlgorithmException {
        JSONObject params = (JSONObject) payContext.get("parameters");
        TreeMap<String, String> signParam = new TreeMap<>();

        //   校验结果是否成功
        String _status = params.get("status").toString();
        String _retCode = params.get("retCode").toString();
        String _merOrderNo = params.get("merOrderNo").toString();
        String _orderPrice = params.get("orderPrice").toString();
        String _message = params.get("message").toString();
        String  sign = params.get("sign").toString();


        // 查询payOrder记录
        String payOrderId = _merOrderNo;
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
        if (payOrder == null) {
            _log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "Can't found payOrder");
            return false;
        }

        //更新三方单号
//        payOrder.setChannelOrderNo(orderid.toString());
//
        PayPassageAccount payPassageAccount = rpcCommonService.rpcPayPassageAccountService.findById(payOrder.getPassageAccountId());
        JSONObject object = JSONObject.parseObject(payPassageAccount.getParam());
        String APP_KEY = object.getString("APP_KEY");
        String signStr = "merOrderNo="+_merOrderNo+"&orderPrice="+_orderPrice+"&retCode="+_retCode+"&status="+_status+"&key="+APP_KEY;
        String validSign = Util.md5(signStr);
        if (!validSign.equals(sign)) {
            payContext.put("retMsg", "验签失败");
            _log.info("我方签名："+validSign);
            _log.info("上游签名："+sign);
            return false;
        }

        // 核对金额
        long outPayAmt = new BigDecimal(_orderPrice).multiply(new BigDecimal("100")).longValue();
        long dbPayAmt = payOrder.getAmount().longValue();
        if (dbPayAmt != outPayAmt) {
            _log.error("金额不一致. outPayAmt={},payOrderId={}", _orderPrice, payOrderId);
            payContext.put("retMsg", "金额不一致");
            return false;
        }
        payContext.put("payOrder", payOrder);
        return true;
    }


}
