package org.xxpay.asst.user.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.asst.common.ctrl.BaseController;
import org.xxpay.asst.common.service.RpcCommonService;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.entity.AssistantAccountHistory;
import org.xxpay.core.entity.AssistantAccount;
import org.xxpay.core.entity.AssistantAccount;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author: dingzhiwei
 * @date: 17/12/6
 * @description:
 */
@RestController
@RequestMapping(Constant.ASST_CONTROLLER_ROOT_PATH + "/account")
@PreAuthorize("hasRole('"+ MchConstant.ASST_ROLE_NORMAL+"')")
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
        AssistantAccount assistantAccount = rpcCommonService.rpcAssistantAccountService.findByAssistantId(getUser().getId());
        JSONObject object = (JSONObject) JSON.toJSON(assistantAccount);
        object.put("availableBalance", assistantAccount.getAvailableBalance());       // 可用余额
        object.put("availableSettAmount", assistantAccount.getAvailableSettAmount()); // 可结算金额
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
        AssistantAccountHistory assistantAccountHistory = getObject(param, AssistantAccountHistory.class);
        if(assistantAccountHistory == null) assistantAccountHistory = new AssistantAccountHistory();
        assistantAccountHistory.setAssistantId(getUser().getId());
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
                .findByAssistantIdAndId(getUser().getId(), id);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(assistantAccountHistory));
    }

}

