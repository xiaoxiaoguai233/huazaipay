package org.xxpay.pay.ctrl.paycenter;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.ZFkKami;
import org.xxpay.core.entity.ZFkShop;
import org.xxpay.pay.ctrl.common.BaseController;
import org.xxpay.pay.mq.BaseNotify4MchPay;
import org.xxpay.pay.service.RpcCommonService;
import org.xxpay.pay.util.RedisUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@Configuration
public class SsskmController extends BaseController {

    private final MyLog _log = MyLog.getLog(SsskmController.class);

    @Autowired
    private RpcCommonService rpcCommonService;

    @Autowired
    private BaseNotify4MchPay baseNotify4MchPay;

    @RequestMapping("/api/ssskm/{orderId}")
    public String waitingOrders(@PathVariable("orderId") String orderId, ModelMap model){

        // 获取订单参数
        JSONObject params = RedisUtil.getObject(orderId, JSONObject.class);

        // 获取在线码商列表
        List<String> online_assistants = RedisUtil.getObject("online_assistants", List.class);

        _log.info("{}", params);

        if(params == null){
            return "paycenter/order_expire";
        }

        List<ZFkShop> zFkShopLists = rpcCommonService.rpcZFkKamiShopService.selectListByPay();

        model.put("orderId", orderId);
        model.put("status", params.getString("status"));
        model.put("expireTime", RedisUtil.getExpire(orderId));    // 过期时间
        model.put("amount", params.getString("amount"));
        model.put("interval", params.getString("interval"));

        model.put("online_assistants", online_assistants);

        model.put("ShopLists", zFkShopLists);

        return "paycenter/ssskm/index";
    }

    @RequestMapping("/api/ssskm/submit")
    @ResponseBody
    public ResponseEntity<?> submit(String card, String orderId, ModelMap model){
        Long amount = new Long(0);
        String[] cards = card.split("\n");

        ZFkKami zFkKami = new ZFkKami();

        ArrayList<ZFkKami> arrayzFkKami = new ArrayList<>();

        // 检查提交订单的正确性
        for(String x : cards){
            System.out.println(x);
            String[] cards_ = x.split(",");

            zFkKami = rpcCommonService.rpcZFkKamiService.selectByCard(cards_[0]);

            if (zFkKami != null && zFkKami.getCard_pwd().equals(cards_[1])){
                amount += zFkKami.getAmount();
                zFkKami.setState("2");
                zFkKami.setUse_time(new Date());
                arrayzFkKami.add(zFkKami);
            }
            // 提交的卡密有错误
            else {
                return ResponseEntity.ok(XxPayPageRes.build(RetEnum.RET_COMM_RECORD_NOT_EXIST, "卡密错误！请检查后重试。"));
            }
        }


        // 核对金额
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.selectPayOrder(orderId);

        if(amount.longValue() != payOrder.getAmount().longValue()){
            return ResponseEntity.ok(XxPayPageRes.build(RetEnum.RET_COMM_RECORD_NOT_EXIST, "卡密总金额与订单充值金额不一致"));
        }else {
            // 遍历更新卡密的状态 已发放 -> 已使用
            for (ZFkKami x : arrayzFkKami){
                x.setOrder_id(orderId);
                rpcCommonService.rpcZFkKamiService.update(x);
            }
        }

        // TODO 充值成功，同时商户
        rpcCommonService.rpcPayOrderService.updatePhoneAndChannelUserByOrderId(orderId, String.valueOf(zFkKami.getUser_id()), zFkKami.getUser_name(), card);

        int result = rpcCommonService.rpcPayOrderService.updateStatus4Success(orderId);

        // 通知业务
        if ( result == 1) {
            // 通知业务系统
            baseNotify4MchPay.doNotify(rpcCommonService.rpcPayOrderService.selectPayOrder(orderId), true);
            // 删除redis的Key
            RedisUtil.del(orderId);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(amount/100));
    }
}
