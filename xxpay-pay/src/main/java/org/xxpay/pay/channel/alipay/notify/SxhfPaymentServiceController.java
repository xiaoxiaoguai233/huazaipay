package org.xxpay.pay.channel.alipay.notify;

import com.alibaba.fastjson.JSON;
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
public class SxhfPaymentServiceController extends BaseController {

    private final MyLog _log = MyLog.getLog(SxhfPaymentServiceController.class);

    @Autowired
    private BaseNotify4MchPay baseNotify4MchPay;

    private String ApiKey = "47c80c89c7a59279ca4faa431e8028b1";


    @Autowired
    private RpcCommonService rpcCommonService;

    @RequestMapping("/api/pay_notify/sxhf")
    public String sjyh_pay_notify(@RequestParam("mchid") String mchid, @RequestParam("mch_order_id") String mch_order_id,
                                  @RequestParam("price") Integer price, @RequestParam("status") String status){

        _log.info("=== 圣鑫话费-订单回调： {} {} {} {} ===", mchid, mch_order_id, price, status);

        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(mch_order_id);

        System.out.println(JSONObject.toJSON(payOrder));

        if (payOrder == null) return "fail";
        String retAmount = String.valueOf(price);
        String amount = String.format("%.2f", Double.valueOf((payOrder.getAmount() / 100)));

        // 确认订单成功
        if (status.equals("1")){
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