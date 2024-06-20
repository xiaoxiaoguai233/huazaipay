package org.xxpay.pay.channel.alipay.notify;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.MchAgentpayRecord;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.TransOrder;
import org.xxpay.pay.ctrl.common.BaseController;
import org.xxpay.pay.mq.BaseNotify4MchAgentpay;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.service.RpcCommonService;
import org.xxpay.pay.util.mth.MD5Util;

public class BfjPaymentServiceController extends BaseController {

    private final MyLog _log = MyLog.getLog(SjyhPaymentServiceController.class);

    @Autowired
    private BaseNotify4MchPay baseNotify4MchPay;

    @Autowired
    public BaseNotify4MchAgentpay baseNotify4MchAgentpay;

    private String ApiKey = "d76615785bb54889953fe2904a3980a9";


    @Autowired
    private RpcCommonService rpcCommonService;

    @RequestMapping("/api/pay_notify/df/bfj")
    public String sjyh_pay_notify(@RequestBody JSONObject param){
        _log.info("=== 备付金订单回调： {} ===", param);

        if (param.getString("PayOrderStatus").equals("100")){
            MchAgentpayRecord merchantUniqueOrder = rpcCommonService.rpcMchAgentpayService.findByTransOrderId(param.getString("MerchantUniqueOrderId"));

            String Amount = param.getString("Amount");

            String retAmount = String.format("%.2f", Double.valueOf((merchantUniqueOrder.getAmount() / 100)));

            System.out.println("===============" + Amount + " " + retAmount);

            // 更新转账状态为成功
            String channelOrderNo = merchantUniqueOrder.getChannelId();
            int result = rpcCommonService.rpcTransOrderService.updateStatus4Success(merchantUniqueOrder.getTransOrderId(), channelOrderNo);
            _log.info("更新转账订单状态为成功({}),transOrderId={},返回结果:{}", PayConstant.TRANS_STATUS_SUCCESS, merchantUniqueOrder.getTransOrderId(), result);
            // 通知业务
            if (result == 1 && retAmount.equals(Amount)) {
                // 通知业务系统
                if(result == 1 && StringUtils.isNotBlank(merchantUniqueOrder.getNotifyUrl())) {
                    merchantUniqueOrder.setStatus(PayConstant.AGENTPAY_STATUS_SUCCESS);   // 状态为成功

                    baseNotify4MchAgentpay.doNotify(merchantUniqueOrder, true);
                }

                return "SUCCESS";
            }
        }
        return  "fail";
    }

}
