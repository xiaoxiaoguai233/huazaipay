package org.xxpay.manage.receive.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.entity.AgentAccount;
import org.xxpay.core.entity.AgentInfo;
import org.xxpay.core.entity.Z7881Account;
import org.xxpay.manage.common.ctrl.BaseController;
import org.xxpay.manage.common.service.RpcCommonService;
import org.xxpay.manage.secruity.JwtUser;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(Constant.MGR_CONTROLLER_ROOT_PATH + "/receive/7881")
public class Z7881Controller  extends BaseController {


    @Autowired private RpcCommonService rpcCommonService;


    /* ******************************************************************************************************************
    *
    *                                                    收款账号
    *
    *  **************************************************************************************************************** */
    @RequestMapping("/account/list")
    @ResponseBody
    public ResponseEntity<?> list(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Z7881Account z7881Account = getObject(param, Z7881Account.class);
        // 获取当前访问人的用户ID
        z7881Account.setParentId(getUser().getId());

        System.out.println("+++++++++++++1");
        System.out.println(JSONObject.toJSONString(z7881Account));

        IPage<Z7881Account> z7881AccountIPage = rpcCommonService.rpcZ7881AccountService.listByPage(getIPage(param), z7881Account, param, Z7881Account.gw());


        System.out.println("+++++++++++++2");
        System.out.println(JSONObject.toJSONString(z7881AccountIPage));

        int count = 0;
        if(z7881AccountIPage == null) count = -1;

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(z7881AccountIPage, count));
    }

}
