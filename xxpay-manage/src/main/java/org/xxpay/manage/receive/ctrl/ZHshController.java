package org.xxpay.manage.receive.ctrl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.core.common.Exception.ServiceException;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.entity.Z7881Account;
import org.xxpay.core.entity.ZHshAccount;
import org.xxpay.manage.common.ctrl.BaseController;
import org.xxpay.manage.common.service.RpcCommonService;
import org.xxpay.manage.secruity.JwtUser;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(Constant.MGR_CONTROLLER_ROOT_PATH + "/receive/hsh")
public class ZHshController  extends BaseController {


    @Autowired
    private RpcCommonService rpcCommonService;


    /* ******************************************************************************************************************
     *
     *                                                    收款账号
     *
     *  **************************************************************************************************************** */
    @RequestMapping("/account/list")
    @ResponseBody
    public ResponseEntity<?> getlist(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZHshAccount zHshAccount = getObject(param, ZHshAccount.class);
        // 获取当前用户的信息
        JwtUser user = getUser();
        // 获取当前用户的ID。是否超级管理员 0：否, 1：是
        zHshAccount.setParentId((int) user.getIsSuperAdmin() == 0 ? user.getId() : null);

        int page = param.getInteger("page");
        int limit = param.getInteger("limit");

        List<ZHshAccount> zHshAccounts = rpcCommonService.rpcZHshAccountService.selectList(zHshAccount, page, limit);
        int count = rpcCommonService.rpcZHshAccountService.selectListCount(zHshAccount.getParentId());
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zHshAccounts, count));
    }

    @RequestMapping("/account/add")
    @ResponseBody
    public ResponseEntity<?> add(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZHshAccount zHshAccount = getObject(param, ZHshAccount.class);

        // 获取当前用户的信息
        JwtUser user = getUser();
        // 获取当前用户的ID。是否超级管理员 0：否, 1：是
        zHshAccount.setParentId(user.getId());

        // 搜索手机号是否重复, 如果为 null，那就反馈错误（CODE:11022）
        ZHshAccount zHshAccount1 = rpcCommonService.rpcZHshAccountService.selectByPhone(zHshAccount.getPhone());
        if (zHshAccount1 != null){
            throw ServiceException.build(RetEnum.RET_SERVICE_REPEAT_PHONE);
        }

        // 限额 * 100
        zHshAccount.setLimitDayMoney(zHshAccount.getLimitDayMoney() * 100);
        zHshAccount.setLimitMaxMoney(zHshAccount.getLimitMaxMoney() * 100);

        int insert = rpcCommonService.rpcZHshAccountService.insert(zHshAccount);
        if(insert == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/account/get")
    @ResponseBody
    public ResponseEntity<?> getById(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZHshAccount zHshAccount = getObject(param, ZHshAccount.class);

        // 搜索手机号是否重复, 如果为 null，那就反馈错误（CODE:11022）
        ZHshAccount zHshAccount1 = rpcCommonService.rpcZHshAccountService.selectById(zHshAccount.getId());
        if (zHshAccount1 == null){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zHshAccount1));
    }

    @RequestMapping("/account/update")
    @ResponseBody
    public ResponseEntity<?> update(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZHshAccount zHshAccount = getObject(param, ZHshAccount.class);

        // 金额与限额 * 100
        zHshAccount.setTodayMoney(zHshAccount.getTodayMoney() * 100);
        zHshAccount.setTotalMoney(zHshAccount.getTotalMoney() * 100);
        zHshAccount.setLimitDayMoney(zHshAccount.getLimitDayMoney() * 100);
        zHshAccount.setLimitMaxMoney(zHshAccount.getLimitMaxMoney() * 100);

        int update = rpcCommonService.rpcZHshAccountService.update(zHshAccount);
        if(update == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/account/delete")
    @ResponseBody
    public ResponseEntity<?> delete(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZHshAccount zHshAccount = getObject(param, ZHshAccount.class);

        int delete = rpcCommonService.rpcZHshAccountService.deleteByPrimaryKey(zHshAccount.getId());
        if(delete == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

}

