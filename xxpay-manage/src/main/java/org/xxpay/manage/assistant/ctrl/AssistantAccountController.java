package org.xxpay.manage.assistant.ctrl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.core.common.Exception.ServiceException;
import org.xxpay.core.common.annotation.MethodLog;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.AssistantAccount;
import org.xxpay.core.entity.AssistantAccountHistory;
import org.xxpay.manage.common.ctrl.BaseController;
import org.xxpay.manage.common.service.RpcCommonService;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(Constant.MGR_CONTROLLER_ROOT_PATH + "/assistant_account")
public class AssistantAccountController  extends BaseController {

    @Autowired
    private RpcCommonService rpcCommonService;

    private static final MyLog _log = MyLog.getLog(AssistantAccountController.class);

    /**
     * 查询账户信息
     * @return
     */
    @RequestMapping("/get")
    @ResponseBody
    public ResponseEntity<?> get(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Long assistantId = getLongRequired(param, "assistantId");
        AssistantAccount assistantAccount = rpcCommonService.rpcAssistantAccountService.findByAssistantId(assistantId);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(assistantAccount));
    }

    /**
     * 码商账户加钱
     * @return
     */
    @RequestMapping("/credit")
    @ResponseBody
    @MethodLog( remark = "码商资金增加" )
    public ResponseEntity<?> credit(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Long assistantId = getLongRequired(param, "assistantId");
        String amount = getStringRequired(param, "amount");         // 变更金额,前端填写的为元,可以为小数点2位
        Long amountL = new BigDecimal(amount.trim()).multiply(new BigDecimal(100)).longValue(); // // 转成分
        // 判断输入的超级密码是否正确
        String password = getStringRequired(param, "password");
        if(!MchConstant.MGR_SUPER_PASSWORD.equals(password)) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MGR_SUPER_PASSWORD_NOT_MATCH));
        }
        try {
            rpcCommonService.rpcAssistantAccountService.credit2Account(assistantId, MchConstant.AGENT_BIZ_TYPE_CHANGE_BALANCE, amountL, MchConstant.BIZ_ITEM_BALANCE);
            return ResponseEntity.ok(BizResponse.buildSuccess());
        }catch (ServiceException e) {
            _log.error(e, "");
            return ResponseEntity.ok(BizResponse.build(e.getRetEnum()));
        }catch (Exception e) {
            _log.error(e, "");
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_COMM_UNKNOWN_ERROR));
        }
    }

    /**
     * 码商账户减钱
     * @return
     */
    @RequestMapping("/debit")
    @ResponseBody
    @MethodLog( remark = "码商资金减少" )
    public ResponseEntity<?> debit(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Long assistantId = getLongRequired(param, "assistantId");
        String amount = getStringRequired(param, "amount");         // 变更金额,前端填写的为元,可以为小数点2位
        Long amountL = new BigDecimal(amount.trim()).multiply(new BigDecimal(100)).longValue(); // // 转成分
        // 判断输入的超级密码是否正确
        String password = getStringRequired(param, "password");
        if(!MchConstant.MGR_SUPER_PASSWORD.equals(password)) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MGR_SUPER_PASSWORD_NOT_MATCH));
        }
        try {
            rpcCommonService.rpcAssistantAccountService.debit2Account(assistantId, MchConstant.AGENT_BIZ_TYPE_CHANGE_BALANCE, amountL, MchConstant.BIZ_ITEM_BALANCE);
            return ResponseEntity.ok(BizResponse.buildSuccess());
        }catch (ServiceException e) {
            _log.error(e, "");
            return ResponseEntity.ok(BizResponse.build(e.getRetEnum()));
        }catch (Exception e) {
            _log.error(e, "");
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_COMM_UNKNOWN_ERROR));
        }
    }

    /**
     * 查询资金流水列表
     * @return
     */
    @RequestMapping("/history_list")
    @ResponseBody
    public ResponseEntity<?> historyList(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        AssistantAccountHistory assistantAccountHistory = getObject(param, AssistantAccountHistory.class);

        int count = rpcCommonService.rpcAssistantAccountHistoryService.count(assistantAccountHistory);
        if(count == 0) return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        List<AssistantAccountHistory> assistantAccountHistoryList = rpcCommonService.rpcAssistantAccountHistoryService
                .select((getPageIndex(param) -1) * getPageSize(param), getPageSize(param), assistantAccountHistory);
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(assistantAccountHistoryList, count));
    }

    /**
     * 查询资金流水
     * @return
     */
    @RequestMapping("/history_get")
    @ResponseBody
    public ResponseEntity<?> historyGet(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Long id = getLongRequired(param, "id");
        AssistantAccountHistory assistantAccountHistory = rpcCommonService.rpcAssistantAccountHistoryService
                .findById(id);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(assistantAccountHistory));
    }

}
