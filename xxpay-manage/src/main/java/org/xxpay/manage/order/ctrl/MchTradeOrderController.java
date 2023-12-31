package org.xxpay.manage.order.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.DateUtil;
import org.xxpay.core.entity.MchAccount;
import org.xxpay.core.entity.MchAccountHistory;
import org.xxpay.core.entity.MchTradeOrder;
import org.xxpay.core.entity.PayProduct;
import org.xxpay.manage.common.ctrl.BaseController;
import org.xxpay.manage.common.service.RpcCommonService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/12/21
 * @description:
 */
@Controller
@RequestMapping(Constant.MGR_CONTROLLER_ROOT_PATH + "/trade_order")
public class MchTradeOrderController extends BaseController {

    @Autowired
    private RpcCommonService rpcCommonService;

    /**
     * 查询单条记录
     *
     * @return
     */
    @RequestMapping("/get")
    @ResponseBody
    public ResponseEntity<?> get(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        String tradeOrderId = getStringRequired(param, "tradeOrderId");
        MchTradeOrder mchTradeOrder = rpcCommonService.rpcMchTradeOrderService.findByTradeOrderId(tradeOrderId);
        if (mchTradeOrder == null) return ResponseEntity.ok(XxPayResponse.buildSuccess(mchTradeOrder));
        Map<String, PayProduct> payProductMap = rpcCommonService.rpcCommonService.getPayProdcutMap(null);
        JSONObject object = (JSONObject) JSON.toJSON(mchTradeOrder);
        PayProduct payProduct = payProductMap.get(String.valueOf(mchTradeOrder.getProductId()));
        object.put("productName", payProduct == null ? "" : payProduct.getProductName());  // 产品名称
        return ResponseEntity.ok(XxPayResponse.buildSuccess(object));

    }

    /**
     * 订单记录列表
     *
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public ResponseEntity<?> list(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        MchTradeOrder mchTradeOrder = getObject(param, MchTradeOrder.class);
        // 订单起止时间
        Date createTimeStart = null;
        Date createTimeEnd = null;
        String createTimeStartStr = getString(param, "createTimeStart");
        if (StringUtils.isNotBlank(createTimeStartStr)) createTimeStart = DateUtil.str2date(createTimeStartStr);
        String createTimeEndStr = getString(param, "createTimeEnd");
        if (StringUtils.isNotBlank(createTimeEndStr)) createTimeEnd = DateUtil.str2date(createTimeEndStr);

        int count = rpcCommonService.rpcMchTradeOrderService.count(mchTradeOrder, createTimeStart, createTimeEnd);
        if (count == 0) return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        List<MchTradeOrder> mchTradeOrderList = rpcCommonService.rpcMchTradeOrderService.select(
                (getPageIndex(param) - 1) * getPageSize(param), getPageSize(param), mchTradeOrder, createTimeStart, createTimeEnd);
        Map<String, PayProduct> payProductMap = rpcCommonService.rpcCommonService.getPayProdcutMap(null);
        List<JSONObject> objects = new LinkedList<>();
        for (MchTradeOrder order : mchTradeOrderList) {
            JSONObject object = (JSONObject) JSON.toJSON(order);
            PayProduct payProduct = payProductMap.get(String.valueOf(order.getProductId()));
            object.put("productName", payProduct == null ? "" : payProduct.getProductName());  // 产品名称
            objects.add(object);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(objects, count));
    }

    /**
     * 查询统计数据
     *
     * @return
     */
    @RequestMapping("/count")
    @ResponseBody
    public ResponseEntity<?> count(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Long mchId = getLong(param, "mchId");
        String tradeOrderId = getString(param, "tradeOrderId");
        String payOrderId = getString(param, "payOrderId");
        Byte tradeType = getByte(param, "tradeType");
        Byte status = getByte(param, "status");

        // 订单起止时间
        String createTimeStartStr = getString(param, "createTimeStart");
        String createTimeEndStr = getString(param, "createTimeEnd");
        Map allMap = rpcCommonService.rpcMchTradeOrderService.count4All(mchId, tradeOrderId, payOrderId, tradeType, status, createTimeStartStr, createTimeEndStr);

        JSONObject obj = new JSONObject();
        obj.put("allTotalCount", allMap.get("totalCount"));                         // 所有订单数
        obj.put("allTotalAmount", allMap.get("totalAmount"));                       // 金额
        obj.put("allTotalMchIncome", allMap.get("totalMchIncome"));                 // 入账金额
        return ResponseEntity.ok(XxPayResponse.buildSuccess(obj));
    }


    /**
     * 电子账户系统
     *
     * @return
     */
    @RequestMapping("/account")
    @ResponseBody
    public ResponseEntity<?> AccountList(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        MchAccount mchAccount = getObject(param, MchAccount.class);
        int count = rpcCommonService.rpcMchAccountService.count();
        if (count == 0) return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        List<MchAccount> all = rpcCommonService.rpcMchAccountService.getAll((getPageIndex(param) - 1) * getPageSize(param), getPageSize(param), mchAccount);
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(all,count));
    }


    @RequestMapping("/stat")
    @ResponseBody
    public ResponseEntity<?> stat(HttpServletRequest request) {
        // 订单起止时间
        Map allMap = rpcCommonService.rpcMchAccountService.stat();

        JSONObject obj = new JSONObject();
        obj.put("allTotalBalance", allMap.get("allTotalBalance"));                         // 可用总金额
        obj.put("allTotalSettAmount", allMap.get("allTotalSettAmount"));                       // 可结算总金额
        obj.put("allTotalUnBalance", allMap.get("allTotalUnBalance"));                 // 不可用总金额
        obj.put("allTotalFrozenMoney", allMap.get("allTotalFrozenMoney"));                 // 冻结总金额
        return ResponseEntity.ok(XxPayResponse.buildSuccess(obj));
    }


    @RequestMapping("/get_account")
    @ResponseBody
    public ResponseEntity<?> getAccount(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        MchAccount mchAccount = getObject(param, MchAccount.class);
        MchAccount byMchId = rpcCommonService.rpcMchAccountService.findByMchId(mchAccount.getMchId());
        return ResponseEntity.ok(XxPayResponse.buildSuccess(JSONObject.toJSON(byMchId)));
    }


    @RequestMapping("/history_list")
    @ResponseBody
    public ResponseEntity<?> historyList(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        MchAccountHistory mchAccountHistory = getObject(param, MchAccountHistory.class);
        int count = rpcCommonService.rpcMchAccountHistoryService.count(mchAccountHistory.getMchId(), mchAccountHistory, getQueryObj(param));
        if (count == 0) return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        List<MchAccountHistory> mchAccountHistoryList = rpcCommonService.rpcMchAccountHistoryService
                .select(mchAccountHistory.getMchId(), (getPageIndex(param) - 1) * getPageSize(param), getPageSize(param), mchAccountHistory, getQueryObj(param));
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(mchAccountHistoryList, count));
    }


    @RequestMapping("/history_get")
    @ResponseBody
    public ResponseEntity<?> historyGet(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Long id = getLongRequired(param, "id");
        MchAccountHistory mchAccountHistory = rpcCommonService.rpcMchAccountHistoryService
                .findById(getUser().getId(), id);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(mchAccountHistory));
    }
}
