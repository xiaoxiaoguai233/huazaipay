package org.xxpay.pay.channel.xhpay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayNotify;
import org.xxpay.pay.channel.dhpay.DhpayConfig;
import org.xxpay.pay.channel.gepay.GepayConfig;
import sun.util.resources.cldr.lg.CurrencyNames_lg;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/12/24
 * @description: 星火支付回调
 */
@Service
public class XhPayNotifyService extends BasePayNotify {

    private static final MyLog _log = MyLog.getLog(XhPayNotifyService.class);

    @Override
    public String getChannelName() {
        return org.xxpay.pay.channel.xhpay.XhpayConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject doNotify(Object notifyData) {
        String logPrefix = "【处理星火支付回调】";
        _log.info("====== 开始处理火星支付回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
        JSONObject retObj = buildRetObj();
        Map<String, Object> payContext = new HashMap<String, Object>();
        PayOrder payOrder;
        try {
            JSONObject params = new JSONObject();

            String customerAmount = request.getParameter("customerAmount");
            String customerAmountCny = request.getParameter("customerAmountCny");
            String outOrderId = request.getParameter("outOrderId"); // 客户订单号
            String orderId = request.getParameter("orderId");  // 平台订单号
            String signType = request.getParameter("signType"); // 固定MD5
            String status = request.getParameter("status"); // 系统订单号
            String sign = request.getParameter("sign"); // 签名

            params.put("customerAmount", customerAmount);
            params.put("customerAmountCny", customerAmountCny);
            params.put("outOrderId", outOrderId);
            params.put("orderId", orderId);
            params.put("signType", signType);
            params.put("status", status);
            params.put("sign", sign);
            _log.info(JSONObject.toJSONString(params) + "==============");
            payContext.put("parameters", params);

            if (!verifyPayParams(payContext)) {
                retObj.put(PayConstant.RESPONSE_RESULT, GepayConfig.RETURN_VALUE_FAIL);
                return retObj;
            }

            payOrder = (PayOrder) payContext.get("payOrder");

            byte payStatus = payOrder.getStatus(); // -1：支付失败， 0：订单生成，1：支付中，2：支付成功，3：业务处理完成，-2：订单过期
            if (payStatus != PayConstant.PAY_STATUS_SUCCESS && payStatus != PayConstant.PAY_STATUS_COMPLETE) {
                int updatePayOrderRows = rpcCommonService.rpcPayOrderService
                        .updateStatus4Success(payOrder.getPayOrderId());
                if (updatePayOrderRows != 1) {
                    _log.error("{}更新支付状态失败,将payOrderId={},更新payStatus={}失败", logPrefix, payOrder.getPayOrderId(),
                            PayConstant.PAY_STATUS_SUCCESS);
                    retObj.put(PayConstant.RESPONSE_RESULT, "处理订单失败");
                    return retObj;
                }
                _log.info("{}更新支付状态成功,将payOrderId={},更新payStatus={}成功", logPrefix, payOrder.getPayOrderId(),
                        PayConstant.PAY_STATUS_SUCCESS);
                payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
            }
            // 业务系统后端通知
            baseNotify4MchPay.doNotify(payOrder, true);
            _log.info("====== 完成处理星火支付回调通知 ======");
        } catch (Exception e) {
            _log.error(e, logPrefix + "处理异常");
        }
        return retObj;
    }

    /**
     * 验证星火支付通知参数
     *
     * @return
     */
    public boolean verifyPayParams(Map<String, Object> payContext) {
        JSONObject params = (JSONObject) payContext.get("parameters");
        // 校验结果是否成功
        String customerAmount = params.getString("customerAmount"); // 充值金额USDT
        String customerAmountCny = params.getString("customerAmountCny"); // 充值金额CNY
        _log.info("++++++++++++++" + customerAmountCny);
        String outOrderId = params.getString("outOrderId"); //  客户订单号
        String orderId = params.getString("orderId"); // 内部订单号
        String signType = params.getString("signType"); // 签名类型
        String status = params.getString("status"); // 订单状态1=’成功’,2=’已扣款’,3=’失效’,4’=’未支付’,5’=’支付失败’,6’=’待验证
        String sign = params.getString("sign"); // 签名

        // 查询payOrder记录
        String payOrderId = outOrderId;
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
        if (payOrder == null) {
            _log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "Can't found payOrder");
            return false;
        }

        PayPassageAccount payPassageAccount = rpcCommonService.rpcPayPassageAccountService
                .findById(payOrder.getPassageAccountId());
        JSONObject object = JSONObject.parseObject(payPassageAccount.getParam());

        String md5Key = object.getString("MD5_KEY");
        String data = customerAmount + customerAmountCny + outOrderId + orderId + "MD5" + "success" + md5Key;
        _log.info("=======" + data);
        if ("success".equals(status)) {
            String _sign = DigestUtils.md5Hex(data);
            _log.info("我方签名" + _sign);
            _log.info("上游签名" + sign);
            if (!_sign.equals(sign)) {
                payContext.put("retMsg", "验签失败");
                return false;
            }
        }

        // 核对金额
        long outPayAmt = new BigDecimal(customerAmountCny).longValue();
        long dbPayAmt = payOrder.getAmount().longValue() / 100;
        if (dbPayAmt != outPayAmt) {
            _log.error("金额不一致. outPayAmt={},payOrderId={}", customerAmountCny, payOrderId);
            payContext.put("retMsg", "金额不一致");
            return false;
        }
        payContext.put("payOrder", payOrder);
        return true;
    }

}
