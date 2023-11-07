package org.xxpay.pay.task;


import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.DateUtils;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.service.RpcCommonService;
import org.xxpay.pay.util.RedisUtil;

import java.util.Date;
import java.util.List;

@Component
public class OrderPoolScheduled extends ReissuceBase {

    @Autowired
    private RpcCommonService rpcCommonService;

    private static final MyLog _log = MyLog.getLog(CheckOrderFailScheduled.class);

    /**
     * 订单池的任务
     */
//    @Scheduled(cron="0 0/1 * * * ?") //每分钟执行一次
//    public void ingTask() {
//        String logPrefix = "【订单池的任务】";
//
//        long startTime = System.currentTimeMillis();
//
//        RedisUtil.set("a", "a");
//
//        int pageIndex = 1;
//        int limit = 1000;
//        PayOrder queryPayOrder = new PayOrder();
//        queryPayOrder.setStatus(PayConstant.PAY_STATUS_PAYING);   // 支付中
//        // 查询需要处理的订单
//        Date createTimeStart = new Date(System.currentTimeMillis() - 20 * 60 * 1000);   // 20分钟查询需要处理的订单
//        Date createTimeEnd = new Date(System.currentTimeMillis() - 10 * 60 * 1000);     // 10分钟
//        JSONObject queyrObj = new JSONObject();
//        queyrObj.put("createTimeStart", createTimeStart);
//        queyrObj.put("createTimeEnd", createTimeEnd);
//
//        _log.info("{}开始查询需要处理的代收订单,查询订单{}<=创建时间<={}", logPrefix, DateUtils.getTimeStr(createTimeStart, "yyyy-MM-dd HH:mm:ss"), DateUtils.getTimeStr(createTimeEnd, "yyyy-MM-dd HH:mm:ss"));
//
//        List<PayOrder> payOrderList = rpcCommonService.rpcPayOrderService.select((pageIndex - 1) * limit, limit, queryPayOrder, createTimeStart, createTimeEnd);
//
//        if(payOrderList == null ) return;
//
//        _log.info("{}查询需要处理的支付订单,共:{}条", logPrefix, payOrderList == null ? 0 : payOrderList.size());
//
//        for(PayOrder payOrder : payOrderList) {
//            String payOrderId = payOrder.getPayOrderId();
//            _log.info("{}开始处理payOrderId={}", logPrefix, payOrderId);
//
//            int updateStatus4Fail = rpcCommonService.rpcPayOrderService.updateStatus4Fail(payOrderId);
//            if (updateStatus4Fail == 1) {
//                _log.info("{}处理完毕payOrderId={}.耗时:{} ms", logPrefix, payOrderId, System.currentTimeMillis() - startTime);
//            }
//            // 查询成功
//        }
//        _log.info("{}本次处理完成,", logPrefix);
//    }

}

