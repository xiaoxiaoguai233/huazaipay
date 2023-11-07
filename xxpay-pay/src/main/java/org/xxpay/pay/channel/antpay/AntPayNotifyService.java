package org.xxpay.pay.channel.antpay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayNotify;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/12/24
 * @description: 蚂蚁支付回调
 */
@Service
public class AntPayNotifyService extends BasePayNotify {

    private static final MyLog _log = MyLog.getLog(AntPayNotifyService.class);

    @Override
    public String getChannelName() {
        return AntPayConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject doNotify(Object notifyData) {
        String logPrefix = "【处理蚂蚁支付回调】";
        _log.info("====== 开始处理蚂蚁支付回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
        JSONObject retObj = buildRetObj();
        Map<String, Object> payContext = new HashMap<String, Object>();
        PayOrder payOrder;
        try {
            JSONObject params = new JSONObject();
            String appId = request.getParameter("appId");
            String orderSn = request.getParameter("orderSn");
            String payRemark = request.getParameter("payRemark"); // 订单标记
            String payTime = request.getParameter("payTime");  // 支付时间
            String totalFee = request.getParameter("totalFee"); // 订单金额
            String paySign = request.getParameter("paySign"); // 签名

            params.put("appId", appId);
            params.put("orderSn", orderSn);
            params.put("payRemark", payRemark);
            params.put("payTime", payTime);
            params.put("totalFee", totalFee);
            params.put("paySign", paySign);

            _log.info(JSONObject.toJSONString(params)+"==============");

            payContext.put("parameters", params);

            if (!verifyPayParams(payContext)) {
                retObj.put(PayConstant.RESPONSE_RESULT, AntPayConfig.RETURN_VALUE_FAIL);
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
            }else{
                retObj.put(PayConstant.RESPONSE_RESULT, AntPayConfig.RETURN_VALUE_SUCCESS);
            }
            // 业务系统后端通知
            baseNotify4MchPay.doNotify(payOrder, true);
            _log.info("====== 完成处理蚂蚁支付回调通知 ======");

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
        String appId = params.getString("appId"); // appId
        String orderSn = params.getString("orderSn"); // 订单号
        _log.info("++++++++++++++"+appId);
        String payRemark = params.getString("payRemark"); //  订单标记内容
        String payTime = params.getString("payTime"); // 订单支付时间
        String totalFee = params.getString("totalFee"); // 订单金额
        String sign = params.getString("paySign"); // 签名

        // 查询payOrder记录
        String payOrderId = orderSn;
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
        if (payOrder == null) {
            _log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "Can't found payOrder");
            return false;
        }

        PayPassageAccount payPassageAccount = rpcCommonService.rpcPayPassageAccountService
                .findById(payOrder.getPassageAccountId());
        JSONObject object = JSONObject.parseObject(payPassageAccount.getParam());

        String publicKey = object.getString("publicKey");

        String data ="appId="+appId+"&orderSn="+orderSn+"&payRemark="+payRemark+"&payTime="+payTime+"&totalFee="+totalFee+"&key="+publicKey;
        _log.info("======="+data);

            String _sign = DigestUtils.shaHex(data).toUpperCase();
            _log.info("我方签名"+_sign);
            _log.info("上游签名"+sign);
            if (!_sign.equals(sign)) {
                payContext.put("retMsg", "验签失败");
                return false;
            }

        // 核对金额
        long outPayAmt = new BigDecimal(totalFee).longValue();
        long dbPayAmt = payOrder.getAmount().longValue()/100;
        if (dbPayAmt != outPayAmt) {
            _log.error("金额不一致. outPayAmt={},payOrderId={}", totalFee, payOrderId);
            payContext.put("retMsg", "金额不一致");
            return false;
        }
        payContext.put("payOrder", payOrder);
        return true;
    }

}
