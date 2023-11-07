package org.xxpay.mch.order.ctrl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.DateUtil;
import org.xxpay.core.common.vo.ManagerPayOrderVO;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.mch.common.ctrl.BaseController;
import org.xxpay.mch.common.service.RpcCommonService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(Constant.MCH_CONTROLLER_ROOT_PATH + "/pay_order")
@PreAuthorize("hasRole('"+ MchConstant.MCH_ROLE_NORMAL+"')")
public class PayOrderController extends BaseController {

    @Autowired
    private RpcCommonService rpcCommonService;

    /**
     * 查询单条支付记录
     * @return
     */
    @RequestMapping("/get")
    @ResponseBody
    public ResponseEntity<?> get(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        String payOrderId = getStringRequired(param, "payOrderId");
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByMchIdAndPayOrderId(getUser().getId(), payOrderId);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(payOrder));
    }

    /**
     * 支付订单记录列表
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public ResponseEntity<?> list(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Integer page = getInteger(param, "page");
        Integer limit = getInteger(param, "limit");
        PayOrder payOrder = getObject(param, PayOrder.class);
        // 订单起止时间
        Date createTimeStart = null;
        Date createTimeEnd = null;
        String createTimeStartStr = getString(param, "createTimeStart");
        if(StringUtils.isNotBlank(createTimeStartStr)) createTimeStart = DateUtil.str2date(createTimeStartStr);
        String createTimeEndStr = getString(param, "createTimeEnd");
        if(StringUtils.isNotBlank(createTimeEndStr)) createTimeEnd = DateUtil.str2date(createTimeEndStr);

        int count = rpcCommonService.rpcPayOrderService.count(getUser().getId(), payOrder, createTimeStart, createTimeEnd);
        if(count == 0) return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        List<PayOrder> payOrderList = rpcCommonService.rpcPayOrderService.select(getUser().getId(),
                (getPageIndex(page) -1) * getPageSize(limit), getPageSize(limit), payOrder, createTimeStart, createTimeEnd);
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(payOrderList, count));
    }

    /**
     * 查询订单统计数据
     * @return
     */
    @RequestMapping("/count")
    @ResponseBody
    public ResponseEntity<?> count(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        String payOrderId = getString(param, "payOrderId");
        String mchOrderNo = getString(param, "mchOrderNo");
        Long productId = getLong(param, "productId");
        Byte productType = getByte(param, "productType");
        Long mchId = getUser().getId();
        String channelId = getString(param, "channelId");
        String channelType = getString(param, "channelType");
        String subMchId = getString(param, "subMchId");
        // 订单起止时间
        String createTimeStartStr = getString(param, "createTimeStart");
        String createTimeEndStr = getString(param, "createTimeEnd");
        Map allMap = rpcCommonService.rpcPayOrderService.count4All(null, mchId, productId, payOrderId, mchOrderNo, productType,createTimeStartStr, createTimeEndStr,channelId,channelType,subMchId, null, null);
        Map successMap = rpcCommonService.rpcPayOrderService.count4Success(null, mchId, productId, payOrderId, mchOrderNo, productType, createTimeStartStr, createTimeEndStr,channelId,channelType,subMchId, null, null);
        Map failMap = rpcCommonService.rpcPayOrderService.count4Fail(null, mchId, productId, payOrderId, mchOrderNo, productType, createTimeStartStr, createTimeEndStr,channelId,channelType,subMchId, null, null);

        JSONObject obj = new JSONObject();
        obj.put("allTotalCount", allMap.get("totalCount"));                         // 所有订单数
        obj.put("allTotalAmount", allMap.get("totalAmount"));                       // 总金额
        obj.put("successTotalCount", successMap.get("totalCount"));                 // 成功订单数
        obj.put("successTotalAmount", successMap.get("totalAmount"));               // 成功金额
        obj.put("successTotalMchIncome", successMap.get("totalMchIncome"));         // 成功商户收入
        obj.put("failTotalCount", failMap.get("totalCount"));                       // 未完成订单数
        obj.put("failTotalAmount", failMap.get("totalAmount"));                     // 未完成金额
        return ResponseEntity.ok(XxPayResponse.buildSuccess(obj));
    }

    /**
     * 支付订单记录列表下载
     * @return
     */
    @RequestMapping("/downOrder")
    @ResponseBody
    public ResponseEntity<?> downOrder(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        PayOrder payOrder = getObject(param, PayOrder.class);
        // 订单起止时间
        Date createTimeStart = null;
        Date createTimeEnd = null;
        String createTimeStartStr = getString(param, "createTimeStart");
        if(StringUtils.isNotBlank(createTimeStartStr)) createTimeStart = DateUtil.str2date(createTimeStartStr);
        String createTimeEndStr = getString(param, "createTimeEnd");
        if(StringUtils.isNotBlank(createTimeEndStr)) createTimeEnd = DateUtil.str2date(createTimeEndStr);
        int count = rpcCommonService.rpcPayOrderService.count(payOrder, createTimeStart, createTimeEnd);
        if(count == 0) return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        List<ManagerPayOrderVO> payOrderList = rpcCommonService.rpcPayOrderService.downOrder(getUser().getId(),
                payOrder, createTimeStart, createTimeEnd);
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(payOrderList, count));
    }

}