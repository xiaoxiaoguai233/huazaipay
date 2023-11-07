package org.xxpay.pay.channel.ysfpay;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.service.RpcCommonService;
import org.xxpay.pay.util.RedisUtil;

@Component
@Slf4j
public class YsfAsynCreateOrders {

    private static final MyLog _log = MyLog.getLog(YsfAsynCreateOrders.class);

    @Autowired public RpcCommonService rpcCommonService;

    /**
     * 查询订单状态
     * @throws InterruptedException
     */
    @Async
    public void YsfPay_h5(PayOrder payOrder) throws InterruptedException {

        Thread.sleep(1500);

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("orderId", payOrder.getPayOrderId());
        jsonObject.put("payLink", "http://192.168.75.1:3020/api/orders/" + payOrder.getPayOrderId());
        jsonObject.put("amount", payOrder.getAmount() / 100);
        jsonObject.put("status", "1");
        jsonObject.put("method", "alipay");

        RedisUtil.setString(payOrder.getPayOrderId(), jsonObject.toJSONString(), 150);


        rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), null);

    }
}
