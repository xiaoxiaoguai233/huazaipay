package org.xxpay.pay.channel.alipay.notify;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.ctrl.common.BaseController;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.service.RpcCommonService;

import javax.servlet.http.HttpServletRequest;

@RestController
public class JfdfPaymentServiceController extends BaseController {

    private final MyLog _log = MyLog.getLog(SjyhPaymentServiceController.class);

    @Autowired
    private BaseNotify4MchPay baseNotify4MchPay;

    private String ApiKey = "1e26bf2e77ad4525a22568cc59cac162";

    @Autowired
    private RpcCommonService rpcCommonService;

    @RequestMapping("/api/pay_notify/jfdf")
    public String sjyh_pay_notify(HttpServletRequest request){
        JSONObject param = getJsonParam(request);
        _log.info("=== 订单回调： {} ===", param);

        PayOrder payOrder = rpcCommonService.rpcPayOrderService.selectPayOrder(param.getString("orderId"));

        if (payOrder == null) return "fail";

        String retAmount = param.getString("amount");

        String amount = String.format("%.2f", Double.valueOf((payOrder.getAmount() / 100)));

        String status = param.getString("status");  // ok

        // 确认订单成功
        if (status.equals("ok")){
            // 更新订单为支付成功状态
            int result = rpcCommonService.rpcPayOrderService.updateStatus4Success(payOrder.getPayOrderId());

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