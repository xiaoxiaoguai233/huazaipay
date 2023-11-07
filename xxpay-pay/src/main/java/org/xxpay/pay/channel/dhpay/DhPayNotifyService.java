package org.xxpay.pay.channel.dhpay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayNotify;
import org.xxpay.pay.channel.gepay.GepayConfig;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/12/24
 * @description: 个付通支付回调
 */
@Service
public class DhPayNotifyService extends BasePayNotify {

    private static final MyLog _log = MyLog.getLog(DhPayNotifyService.class);

    @Override
    public String getChannelName() {
        return DhpayConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject doNotify(Object notifyData) {
        String logPrefix = "【处理个付通支付回调】";
        _log.info("====== 开始处理个付通支付回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
        JSONObject retObj = buildRetObj();
        Map<String, Object> payContext = new HashMap<String, Object>();
        PayOrder payOrder;
        String respString = DhpayConfig.RETURN_VALUE_FAIL;
        try {
            JSONObject params = new JSONObject();

            String order_num = request.getParameter("order_num");
            String money = request.getParameter("money");
            String status = request.getParameter("status"); // 订单状态1=’成功’,2=’已扣款’,3=’失效’,4’=’未支付’,5’=’支付失败’,6’=’待验证
            String create_time = request.getParameter("create_time");
            String user_order = request.getParameter("user_order"); // 三方单号
            String time = request.getParameter("time"); // 系统订单号
            String sign = request.getParameter("sign"); // 签名

            params.put("order_num", order_num);
            params.put("money", money);
            params.put("status", status);
            params.put("create_time", create_time);
            params.put("user_order", user_order);
            params.put("time", time);
            params.put("sign", sign);

            Integer sta = Integer.valueOf(status);

            payContext.put("parameters", params);

            if (!verifyPayParams(payContext)) {
                retObj.put(PayConstant.RESPONSE_RESULT, GepayConfig.RETURN_VALUE_FAIL);
                return retObj;
            }
            
            payOrder = (PayOrder) payContext.get("payOrder");

            payOrder.setChannelOrderNo(user_order);
            payOrder.setPayOrderId(order_num);
            payOrder.setStatus(sta.byteValue());

            // 1=’成功’,2=’已扣款’,3=’失效’,4’=’未支付’,5’=’支付失败’,6’=’待验证
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
                _log.error("{}更新支付状态成功,将payOrderId={},更新payStatus={}成功", logPrefix, payOrder.getPayOrderId(),
                        PayConstant.PAY_STATUS_SUCCESS);
                payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
            }
            // 业务系统后端通知
            baseNotify4MchPay.doNotify(payOrder, true);
            _log.info("====== 完成处理个付通支付回调通知 ======");
            respString = DhpayConfig.RETURN_VALUE_SUCCESS;
        } catch (Exception e) {
            _log.error(e, logPrefix + "处理异常");
        }
        retObj.put(PayConstant.RESPONSE_RESULT, respString);
        return retObj;
    }

    /**
     * 验证个付通支付通知参数
     * 
     * @return
     */
    public boolean verifyPayParams(Map<String, Object> payContext) {
        JSONObject params = (JSONObject) payContext.get("parameters");
        // 校验结果是否成功
        String order_num = params.getString("order_num");
        String money = params.getString("money");
        String status = params.getString("status"); // 订单状态1=’成功’,2=’已扣款’,3=’失效’,4’=’未支付’,5’=’支付失败’,6’=’待验证
        String create_time = params.getString("create_time");
        String user_order = params.getString("user_order"); // 三方单号
        String time = params.getString("time"); // 系统订单号
        String sign = params.getString("sign"); // 签名

        // 查询payOrder记录
        String payOrderId = order_num;
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
        if (payOrder == null) {
            _log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "Can't found payOrder");
            return false;
        }

        // 更新三方单号
        payOrder.setChannelOrderNo(user_order);

        PayPassageAccount payPassageAccount = rpcCommonService.rpcPayPassageAccountService
                .findById(payOrder.getPassageAccountId());
        JSONObject object = JSONObject.parseObject(payPassageAccount.getParam());

        String APP_ID = object.getString("APP_ID");
        String APP_KEY = object.getString("APP_KEY");
        String username = object.getString("username");

        if ("1".equals(status) || "2".equals(status)) {
            String _sign = DigestUtils.md5Hex("APPID=" + APP_ID + "&username=" + username + "&time=" + time + APP_KEY);
            if (!_sign.equals(sign)) {
                payContext.put("retMsg", "验签失败");
                return false;
            }
        }

        // 核对金额
        long outPayAmt = new BigDecimal(money).longValue();
        long dbPayAmt = payOrder.getAmount().longValue();
        if (dbPayAmt != outPayAmt) {
            _log.error("金额不一致. outPayAmt={},payOrderId={}", money, payOrderId);
            payContext.put("retMsg", "金额不一致");
            return false;
        }
        payContext.put("payOrder", payOrder);
        return true;
    }

}
