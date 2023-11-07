package org.xxpay.pay.task.schedule.task;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.ZSlrjAccount;
import org.xxpay.pay.channel.PaymentInterface;
import org.xxpay.pay.channel.alipay.AlipayAsynCreateOrders;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.service.RpcCommonService;
import org.xxpay.pay.task.schedule.CheckOrderStatusScheduled;
import org.xxpay.pay.util.RedisUtil;


@Slf4j
@Data
public class PayOrderCheckStatusTask implements Runnable {


    private static final MyLog _log = MyLog.getLog(CheckOrderStatusScheduled.class);

    private String key;

    private RpcCommonService rpcCommonService;

    private BaseNotify4MchPay baseNotify4MchPay;

    private PaymentInterface paymentInterface;

    public PayOrderCheckStatusTask(String key, RpcCommonService rpcCommonService, BaseNotify4MchPay baseNotify4MchPay) {
        this.key = key;
        this.rpcCommonService = rpcCommonService;
        this.baseNotify4MchPay = baseNotify4MchPay;
    }

    @Override
    public void run() {
        // 获取订单参数
        JSONObject params = RedisUtil.getObject(key, JSONObject.class);

        // 重新获取新链接
        if(params.getString("status").equals("0")) {

            _log.info("{} === 重新获取订单链接中，请稍后", key);
            // 执行支付
            try {
                paymentInterface.pay(rpcCommonService.rpcPayOrderService.selectPayOrder(key));
                return;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

            // 获取phone与token
        String phone = params.getString("phone");
        String token = params.getString("token");
        String orderSn = params.getString("orderSn");
        String payOrderId = params.getString("payOrderId");

        String url = "https://app.homeinns.com/api/v4/giftcards/show_order?auth_token=" + token + "&order_no=" + orderSn;
        // 获取支付链接
        String body = "";
        try {
            body = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .timeout(20000)
                    .ignoreContentType(true)
                    .execute().body();
        }catch (Exception e){
            // 释放当前号码
            e.printStackTrace();
            return;
        }

        JSONObject json = JSONObject.parseObject(body);
        _log.info("=== {} 拉起订单状态回调结果： {} ===", payOrderId, json);

        String status = json.getJSONObject("data").getJSONObject("order").getString("status");

        // 订单状态支付成功
        if (status.equals("F")){
            // 获取订单的详细信息
            PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);

            // 更新收款账号信息，并且判断账号是否收款成功
            ZSlrjAccount zSlrjAccount = rpcCommonService.rpcZSlrjAccountService.selectByPhone(phone);
            zSlrjAccount.setTodayMoney(zSlrjAccount.getTodayMoney() + payOrder.getAmount());
            zSlrjAccount.setTotalMoney(zSlrjAccount.getTotalMoney() + payOrder.getAmount());
            // 如果收款金额达到日限额或者账号限额，状态设置为限额
            if (zSlrjAccount.getTodayMoney() > zSlrjAccount.getLimitDayMoney() || zSlrjAccount.getTotalMoney() >= zSlrjAccount.getLimitMaxMoney()){
                zSlrjAccount.setState((byte) 3);
            }

            int update = rpcCommonService.rpcZSlrjAccountService.update(zSlrjAccount);

            // 更新订单为支付成功状态
            int result = rpcCommonService.rpcPayOrderService.updateStatus4Success(payOrder.getPayOrderId());

            // 添加如家订单号
            rpcCommonService.rpcPayOrderService.updateCardPwdByOrderId(payOrder.getPayOrderId(), orderSn);

            // 通知业务
            if (result == 1 && update == 1) {
                // 通知业务系统
                baseNotify4MchPay.doNotify(payOrder, true);
                // 删除redis的Key
                RedisUtil.del(key);
            }
        }


    }
}
