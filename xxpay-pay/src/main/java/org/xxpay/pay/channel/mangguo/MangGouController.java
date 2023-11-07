package org.xxpay.pay.channel.mangguo;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.ctrl.common.BaseController;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.service.RpcCommonService;
import org.xxpay.pay.util.RedisUtil;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MangGouController extends BaseController {


    private static final MyLog _log = MyLog.getLog(MangGouController.class);

    @Autowired private RpcCommonService rpcCommonService;

    private BaseNotify4MchPay baseNotify4MchPay;

    @RequestMapping("/api/mangguo/notify.htm")
    @ResponseBody
    public String toPay(HttpServletRequest request){
        JSONObject po = getJsonParam(request);

        String mer_no = getString(po, "mer_no");
        String amount = getString(po, "amount");
        String trade_no = getString(po, "trade_no");
        String out_trade_no = getString(po, "out_trade_no");
        String pay_time = getString(po, "pay_time");


        System.out.println(po);


        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(trade_no);
        if(payOrder == null){
            return "fail, wrong trade_no or payOrder!";
        }

        JSONObject redis_params = RedisUtil.getObject(trade_no, JSONObject.class);

        if(!redis_params.getString("amount").equals(amount)){
            return "fail, wrong amount !";
        }
        if(!mer_no.equals("M0311209417")){
            return "fail, wrong mer_no !";
        }
        if(!redis_params.getString("trade_no").equals(trade_no)){
            return "fail, wrong trade_no !";
        }
        if(!redis_params.getString("out_trade_no").equals(out_trade_no)){
            return "fail, wrong out_trade_no !";
        }

        // 更新订单为支付成功状态
        int result = rpcCommonService.rpcPayOrderService.updateStatus4Success(trade_no);

        System.out.println(result);
        System.out.println(result);
        System.out.println(result);
        System.out.println(result);
        System.out.println(result);
        System.out.println(result);
        System.out.println(result);
        System.out.println(result);
        System.out.println(result);
        System.out.println(result);
        System.out.println(result);
        System.out.println(result);
        System.out.println(result);
        System.out.println(result);
        System.out.println(result);

        // 通知业务
        if (result == 1) {
            // 通知业务系统
            baseNotify4MchPay.doNotify(payOrder, true);
            // 删除redis的Key
            RedisUtil.del(payOrder.getPayOrderId());

            return "success";
        }
        return "fail, system wrong !";
    }

}
