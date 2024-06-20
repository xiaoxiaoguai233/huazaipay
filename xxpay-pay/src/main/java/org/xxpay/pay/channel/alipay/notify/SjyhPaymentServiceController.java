package org.xxpay.pay.channel.alipay.notify;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.ctrl.common.BaseController;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.service.RpcCommonService;
import org.xxpay.pay.util.mth.MD5Util;

@RestController
public class SjyhPaymentServiceController extends BaseController {

    private final MyLog _log = MyLog.getLog(SjyhPaymentServiceController.class);

    @Autowired
    private BaseNotify4MchPay baseNotify4MchPay;

    private String ApiKey = "d76615785bb54889953fe2904a3980a9";


    @Autowired
    private RpcCommonService rpcCommonService;

    @RequestMapping("/api/pay_notify/sjyh")
    public String sjyh_pay_notify(@RequestBody JSONObject param){
        _log.info("=== 订单回调： {} ===", param);

        PayOrder payOrder = rpcCommonService.rpcPayOrderService.selectPayOrder(param.getString("orderid"));

        if (payOrder == null) return "fail";

        String retAmount = param.getString("amount");

        String amount = String.format("%.2f", Double.valueOf((payOrder.getAmount() / 100)));

        String state = param.getString("state");
        String retsign = param.getString("retsign");
        String sign = param.getString("sign");
        String encrypt = MD5Util.encrypt(sign + ApiKey);


        // 确认订单成功
        if (state.equals("4") && amount.equals(retAmount) && encrypt.equals(retsign)){
            // 更新订单为支付成功状态
            int result = rpcCommonService.rpcPayOrderService.updateStatus4Success(payOrder.getPayOrderId());

            System.out.println(state);
            System.out.println(amount);
            System.out.println(retAmount);
            System.out.println(retsign);
            System.out.println(encrypt);
            System.out.println(result);

            // 通知业务
            if (result == 1) {
                // 通知业务系统
                baseNotify4MchPay.doNotify(payOrder, true);
                return "success";
            }
        }
        return "fail";
    }
}