package org.xxpay.mch.user.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.DateUtil;
import org.xxpay.core.common.vo.ManagerPayOrderVO;
import org.xxpay.core.entity.MchAccount;
import org.xxpay.core.entity.MchAccountHistory;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.mch.common.ctrl.BaseController;
import org.xxpay.mch.common.service.RpcCommonService;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: dingzhiwei
 * @date: 17/12/6
 * @description:
 */
@RestController
@RequestMapping(Constant.MCH_CONTROLLER_ROOT_PATH + "/account")
@PreAuthorize("hasRole('"+ MchConstant.MCH_ROLE_NORMAL+"')")
public class AccountController extends BaseController {

    @Autowired
    private RpcCommonService rpcCommonService;

    /**
     * 查询账户信息
     * @return
     */
    @RequestMapping("/get")
    @ResponseBody
    public ResponseEntity<?> get() {
        MchAccount mchAccount = rpcCommonService.rpcMchAccountService.findByMchId(getUser().getId());
        JSONObject object = (JSONObject) JSON.toJSON(mchAccount);
        object.put("availableBalance", mchAccount.getAvailableBalance());                       // 可用余额
        object.put("availableSettAmount", mchAccount.getAvailableSettAmount());                 // 可结算金额
        object.put("availableAgentpayBalance", mchAccount.getAvailableAgentpayBalance());       // 可用代付余额
        return ResponseEntity.ok(XxPayResponse.buildSuccess(object));
    }

    /**
     * 查询资金流水列表
     * @return
     */
    @RequestMapping("/history_list")
    @ResponseBody
    public ResponseEntity<?> historyList(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        MchAccountHistory mchAccountHistory = getObject(param, MchAccountHistory.class);
        int count = rpcCommonService.rpcMchAccountHistoryService.count(getUser().getId(), mchAccountHistory, getQueryObj(param));
        if(count == 0) return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        List<MchAccountHistory> mchAccountHistoryList = rpcCommonService.rpcMchAccountHistoryService
                .select(getUser().getId(), (getPageIndex(param) - 1) * getPageSize(param), getPageSize(param), mchAccountHistory, getQueryObj(param));
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(mchAccountHistoryList, count));
    }

    /**
     * 查询资金流水列表
     * @return
     */
    @RequestMapping("/history_get")
    @ResponseBody
    public ResponseEntity<?> historyGet(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Long id = getLongRequired(param, "id");
        MchAccountHistory mchAccountHistory = rpcCommonService.rpcMchAccountHistoryService
                .findById(getUser().getId(), id);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(mchAccountHistory));
    }


    /**
     * 资金流水列表下载
     * @return
     */
    @RequestMapping("/downhistory")
    @ResponseBody
    public ResponseEntity<?> downOrder(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        MchAccountHistory mchAccountHistory = getObject(param, MchAccountHistory.class);

        int count = rpcCommonService.rpcMchAccountHistoryService.count(null, mchAccountHistory, getQueryObj(param));

        if(count == 0) return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        List<MchAccountHistory> mchAccountHistoryList = rpcCommonService.rpcMchAccountHistoryService
                .select(getUser().getId(), mchAccountHistory, getQueryObj(param));

        List<MchBalanceHistoryVo> mchBalanceHistoryVos = new ArrayList<>();
        for(MchAccountHistory mchAccountHistory_item : mchAccountHistoryList){

            MchBalanceHistoryVo mchBalanceHistoryVo = new MchBalanceHistoryVo();
            mchBalanceHistoryVo.setId(mchAccountHistory_item.getId());

            if (mchAccountHistory_item.getBalance() != null)
                mchBalanceHistoryVo.setBefore_balance(
                        new BigDecimal(mchAccountHistory_item.getBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() / 100);

            if (mchAccountHistory_item.getAmount() != null){
                String a = "";
                if (mchAccountHistory_item.getFundDirection() == 1){
                    a += "+";
                }else{
                    a += "-";
                }
                mchBalanceHistoryVo.setAmount(a + String.format("%.2f", new Double(mchAccountHistory_item.getAmount())  / 100 ));
            }

            if (mchAccountHistory_item.getAfterBalance() != null)
                mchBalanceHistoryVo.setAfter_balance(
                        new BigDecimal(mchAccountHistory_item.getAfterBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() / 100);

            if (mchAccountHistory_item.getBizType() == 1){
                mchBalanceHistoryVo.setBizType("支付");
            } else if (mchAccountHistory_item.getBizType() == 2){
                mchBalanceHistoryVo.setBizType("提现");
            } else if (mchAccountHistory_item.getBizType() == 3){
                mchBalanceHistoryVo.setBizType("调账");
            } else if (mchAccountHistory_item.getBizType() == 4){
                mchBalanceHistoryVo.setBizType("充值");
            } else if (mchAccountHistory_item.getBizType() == 5){
                mchBalanceHistoryVo.setBizType("差错处理");
            } else{
                mchBalanceHistoryVo.setBizType("代付");
            }

            mchBalanceHistoryVo.setOrderId(mchAccountHistory_item.getOrderId());
            if (mchAccountHistory_item.getOrderAmount() != null)
                mchBalanceHistoryVo.setOrderAmount(
                        new BigDecimal(mchAccountHistory_item.getOrderAmount()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() / 100);
            if (mchAccountHistory_item.getFee() != null)
                mchBalanceHistoryVo.setFee(
                        new BigDecimal(mchAccountHistory_item.getFee()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() / 100);
            mchBalanceHistoryVo.setCreateTime(DateUtil.date2Str(mchAccountHistory_item.getCreateTime()));

            mchBalanceHistoryVos.add(mchBalanceHistoryVo);
        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(mchBalanceHistoryVos, count));
    }


    @Data
    @Setter
    @Getter
    class MchBalanceHistoryVo{
        Long id;
        double before_balance;
        String amount;
        double after_balance;
        String bizType;
        String orderId;
        double orderAmount;
        double fee;
        String createTime;
    }

}
