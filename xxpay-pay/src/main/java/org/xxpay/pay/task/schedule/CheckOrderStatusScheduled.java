package org.xxpay.pay.task.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.service.RpcCommonService;
import org.xxpay.pay.task.schedule.task.PayOrderCheckStatusTask;
import org.xxpay.pay.util.RedisUtil;

import java.util.Collection;
import java.util.concurrent.ExecutorService;


@Component
@EnableAsync        // 2.开启多线程
@Slf4j
public class CheckOrderStatusScheduled {

    private static final MyLog _log = MyLog.getLog(CheckOrderStatusScheduled.class);
    @Autowired
    private ExecutorService executorService;

    @Autowired private RpcCommonService rpcCommonService;

    @Autowired public BaseNotify4MchPay baseNotify4MchPay;

    /**
     * 查询订单状态
     * @throws InterruptedException
     */
    @Async
//    @Scheduled(fixedDelay = 1000 * 10)  //   间隔10秒
    public void PayOrderCheckState() throws InterruptedException {

        _log.info("================【CheckOrderStatusScheduled RUNNING】================");

        Collection<String> keys = RedisUtil.keys( "P*");

        for (String key : keys){
            executorService.execute(new PayOrderCheckStatusTask(key, rpcCommonService, baseNotify4MchPay));
        }
    }
}