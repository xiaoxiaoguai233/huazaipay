package org.xxpay.manage.order.ctrl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.DateUtil;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.vo.ManagerPayOrderVO;
import org.xxpay.core.entity.PayInterface;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassage;
import org.xxpay.manage.common.ctrl.BaseController;
import org.xxpay.manage.common.service.RpcCommonService;
import org.xxpay.manage.config.service.CommonConfigService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(Constant.MGR_CONTROLLER_ROOT_PATH + "/pay_order")
public class PayOrderController extends BaseController {

    @Autowired
    private RpcCommonService rpcCommonService;

    @Autowired
    private CommonConfigService commonConfigService;

    @Autowired
    private static final MyLog _log = MyLog.getLog(PayOrderController.class);

    /**
     * 查询单条支付记录
     * @return
     */
    @RequestMapping("/get")
    @ResponseBody
    public ResponseEntity<?> get(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        String payOrderId = getStringRequired(param, "payOrderId");
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
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
        if(StringUtils.isNotBlank(payOrder.getChannelId())){
            PayInterface payInterface =  commonConfigService.getCodeBychannelId(payOrder.getChannelId());
            payOrder.setChannelId(payInterface.getIfCode());
        }
        if(StringUtils.isNotBlank(payOrder.getChannelType())) {
            PayPassage payPassage =commonConfigService.getCodeBychannelType(payOrder.getChannelType());
            payOrder.setChannelType(payPassage.getIfTypeCode());
        }
        int count = rpcCommonService.rpcPayOrderService.count(payOrder, createTimeStart, createTimeEnd);
        if(count == 0) {
            return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        }
        List<PayOrder> payOrderList = rpcCommonService.rpcPayOrderService.select(
                (getPageIndex(page) -1) * getPageSize(limit), getPageSize(limit), payOrder, createTimeStart, createTimeEnd);

        Map payInterfaceMap = commonConfigService.getPayInterfaceMap(); // 支付接口Map
        Map payChannleMap = commonConfigService.getPayChannleMap(); // 支付通道Map
        for (PayOrder vo:payOrderList) {
            String id=vo.getChannelId();
            vo.setChannelType(String.valueOf(payChannleMap.get(id)));
            vo.setChannelId(String.valueOf(payInterfaceMap.get(id)));
        }

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
        Long mchId = getLong(param, "mchId");
        Byte productType = getByte(param, "productType");
        String channelId = getString(param, "channelId");
        String channelType = getString(param, "channelType");
        String subMchId = getString(param, "subMchId");
        String phone = getString(param, "phone");


        String cardPwd = getString(param, "cardPwd");

        // 订单起止时间
        String createTimeStartStr = getString(param, "createTimeStart");
        String createTimeEndStr = getString(param, "createTimeEnd");
        if(StringUtils.isNotBlank(channelId)){
            PayInterface payInterface =  commonConfigService.getCodeBychannelId(channelId);
            if(payInterface==null) return ResponseEntity.ok(XxPayResponse.buildSuccess("未查询到有效值，请核对"));
            channelId=payInterface.getIfCode();
        }
        if(StringUtils.isNotBlank(channelType)) {
            PayPassage payPassage =commonConfigService.getCodeBychannelType(channelType);
            if(payPassage==null)return ResponseEntity.ok(XxPayResponse.buildSuccess("未查询到有效值，请核对"));
            channelType=payPassage.getIfTypeCode();

        }
        Map allMap = rpcCommonService.rpcPayOrderService.count4All(null, mchId, productId, payOrderId, mchOrderNo, productType, createTimeStartStr, createTimeEndStr,channelId,channelType,subMchId, phone, cardPwd);
        Map successMap = rpcCommonService.rpcPayOrderService.count4Success(null, mchId, productId, payOrderId, mchOrderNo, productType, createTimeStartStr, createTimeEndStr,channelId,channelType,subMchId, phone, cardPwd);
        Map failMap = rpcCommonService.rpcPayOrderService.count4Fail(null, mchId, productId, payOrderId, mchOrderNo, productType, createTimeStartStr, createTimeEndStr,channelId,channelType,subMchId, phone, cardPwd);

        JSONObject obj = new JSONObject();
        obj.put("allTotalCount", allMap.get("totalCount"));                         // 所有订单数
        obj.put("allTotalAmount", allMap.get("totalAmount"));                       // 总金额
        obj.put("successTotalCount", successMap.get("totalCount"));                 // 成功订单数
        obj.put("successTotalAmount", successMap.get("totalAmount"));               // 成功金额
        obj.put("successTotalMchIncome", successMap.get("totalMchIncome"));         // 成功商户收入
        obj.put("successTotalAgentProfit", successMap.get("totalAgentProfit"));     // 成功代理商利润
        obj.put("successTotalPlatProfit", successMap.get("totalPlatProfit"));       // 成功平台利润
        obj.put("failTotalCount", failMap.get("totalCount"));                       // 未完成订单数
        obj.put("failTotalAmount", failMap.get("totalAmount"));                     // 未完成金额
        return ResponseEntity.ok(XxPayResponse.buildSuccess(obj));
    }

    /**
     * 补单
     * 1. 将订单为 支付中 状态的修改为支付成功
     * 2. 给商户下发一次通知
     * @return
     */
    @RequestMapping("/reissue")
    @ResponseBody
    public ResponseEntity<?> reissue(HttpServletRequest request) {
//        JSONObject param = getJsonParam(request);
//
//        // 判断输入的超级密码是否正确
//        String password = getStringRequired(param, "password");
//        if(!MchConstant.MGR_SUPER_PASSWORD.equals(password)) {
//            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MGR_SUPER_PASSWORD_NOT_MATCH));
//        }
//        // 修改订单状态
//        String payOrderId = getStringRequired(param, "payOrderId");
//        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
//
//        // 修改状态为支付成功,
//        int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Success(payOrderId);
//        _log.info("[补单]userId={},payOrderId={},将支付中修改为支付成功,返回结果:{}", getUser().getId(), payOrder.getPayOrderId(), updateCount);
//
//        // 发送商户通知
//        rpcCommonService.rpcXxPayNotifyService.executePayNotify(payOrderId);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(""));
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
        Long mchId = getUser().getId()==1?null:getUser().getId();
        List<ManagerPayOrderVO> managerOrderList = rpcCommonService.rpcPayOrderService.downOrder(mchId,
                payOrder, createTimeStart, createTimeEnd);
        Map payInterfaceMap = commonConfigService.getPayInterfaceMap(); // 支付接口Map
        Map payChannleMap = commonConfigService.getPayChannleMap(); // 支付通道Map
        for (ManagerPayOrderVO vo:managerOrderList) {
            String id = vo.getChannelId();
            vo.setChannelType(String.valueOf(payChannleMap.get(id)));
            vo.setChannelId(String.valueOf(payInterfaceMap.get(id)));

        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(managerOrderList, count));
    }


}