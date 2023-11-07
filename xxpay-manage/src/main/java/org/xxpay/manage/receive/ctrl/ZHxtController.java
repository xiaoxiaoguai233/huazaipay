package org.xxpay.manage.receive.ctrl;


import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.core.common.Exception.ServiceException;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.ZHxtAccount;
import org.xxpay.manage.common.ctrl.BaseController;
import org.xxpay.manage.common.service.RpcCommonService;
import org.xxpay.manage.secruity.JwtUser;
import org.xxpay.manage.utils.mth.SignUtil;
import org.jsoup.Connection;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(Constant.MGR_CONTROLLER_ROOT_PATH + "/receive/hxt")
public class ZHxtController  extends BaseController {


    private static final MyLog _log = MyLog.getLog(ZHxtController.class);

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
        ZHxtAccount zMthAccount = getObject(param, ZHxtAccount.class);
        // 获取当前用户的信息
        JwtUser user = getUser();
        // 获取当前用户的ID。是否超级管理员 0：否, 1：是
        zMthAccount.setParentId((int) user.getIsSuperAdmin() == 0 ? user.getId() : null);

        int page = param.getInteger("page");
        int limit = param.getInteger("limit");

        List<ZHxtAccount> zMthAccounts = rpcCommonService.rpcZHxtAccountService.selectList(zMthAccount, page, limit);
        int count = rpcCommonService.rpcZHxtAccountService.selectListCount(zMthAccount.getParentId());
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zMthAccounts, count));
    }

    @RequestMapping("/account/add")
    @ResponseBody
    public ResponseEntity<?> add(HttpServletRequest request) throws IOException {
        JSONObject param = getJsonParam(request);
        ZHxtAccount zHxtAccount = getObject(param, ZHxtAccount.class);

        // 获取当前用户的信息
        JwtUser user = getUser();
        // 获取当前用户的ID。是否超级管理员 0：否, 1：是
        zHxtAccount.setParentId(user.getId());

        // 搜索手机号是否重复, 如果为 null，那就反馈错误（CODE:11022）
        ZHxtAccount zMthAccount1 = rpcCommonService.rpcZHxtAccountService.selectByPhone(zHxtAccount.getPhone());
        if (zMthAccount1 != null){
            throw ServiceException.build(RetEnum.RET_SERVICE_REPEAT_PHONE);
        }

        // 限额 * 100
        zHxtAccount.setLimitDayMoney(zHxtAccount.getLimitDayMoney() * 100);
        zHxtAccount.setLimitMaxMoney(zHxtAccount.getLimitMaxMoney() * 100);

        System.out.println(zHxtAccount);

        int insert = rpcCommonService.rpcZHxtAccountService.insert(zHxtAccount);
        if(insert == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/account/get")
    @ResponseBody
    public ResponseEntity<?> getById(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZHxtAccount zMthAccount = getObject(param, ZHxtAccount.class);

        // 搜索手机号是否重复, 如果为 null，那就反馈错误（CODE:11022）
        ZHxtAccount zMthAccount1 = rpcCommonService.rpcZHxtAccountService.selectById(zMthAccount.getId());
        if (zMthAccount1 == null){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zMthAccount1));
    }

    @RequestMapping("/account/update")
    @ResponseBody
    public ResponseEntity<?> update(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZHxtAccount zMthAccount = getObject(param, ZHxtAccount.class);

        // 金额与限额 * 100
        zMthAccount.setTodayMoney(zMthAccount.getTodayMoney() * 100);
        zMthAccount.setTotalMoney(zMthAccount.getTotalMoney() * 100);
        zMthAccount.setLimitDayMoney(zMthAccount.getLimitDayMoney() * 100);
        zMthAccount.setLimitMaxMoney(zMthAccount.getLimitMaxMoney() * 100);

        int update = rpcCommonService.rpcZHxtAccountService.update(zMthAccount);
        if(update == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/account/delete")
    @ResponseBody
    public ResponseEntity<?> delete(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZHxtAccount zMthAccount = getObject(param, ZHxtAccount.class);

        int delete = rpcCommonService.rpcZHxtAccountService.deleteByPrimaryKey(zMthAccount.getId());
        if(delete == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/account/number_card/get")
    @ResponseBody
    public ResponseEntity<?> GetToken(HttpServletRequest request) throws IOException {
        JSONObject param = getJsonParam(request);
        JSONObject jsonObject = getObject(param, JSONObject.class);

        String phone = jsonObject.getString("phone");
        String token = jsonObject.getString("token");

        ZHxtAccount zHxtAccount = rpcCommonService.rpcZHxtAccountService.selectByPhone(phone);
        if (zHxtAccount != null){
            throw ServiceException.build(RetEnum.RET_SERVICE_REPEAT_PHONE);
        }

        /*******************************************************
         *
         *                    获取Token
         *
         ******************************************************/
        String params = "{\"date\":\"1678008471\",\"keycode\":\"209ebfc44de358fc306b037c076c750f\",\"appVersion\":\"V2.16.2\",\"channel\":\"010\",\"systemType\":1,\"systemVersion\":\"28\",\"results\":{},\"mac\":\"oZziF1A8W/BtIctlqMF2C4AVbHvT9ROH1SJaE4F9ArE0b6YXyEXck+JPB58u2y21jbS+pnijKTZt\\n0Fi1oLye0CqiybY4jOBE0PjX9lnu6uBlVJtyG6AVdTZQA+5SAyWNACnhwyVnNfPcty39iNYei166\\nbJ1nmalcHtSH6eIbWjE=\",\"sid\":\"sid\"}";
        JSONObject jsonObject_params = JSONObject.parseObject(params);
        jsonObject_params.put("sid", token);

        String body = "";
        try {
            body = Jsoup.connect("http://app.hui724.com/app/card/newCardManager")
                    .method(Connection.Method.POST)
                    .header("Content-Type", "*/*")
                    .timeout(20000)
                    .requestBody(jsonObject_params.toJSONString())
                    .ignoreContentType(true)
                    .execute().body();
        }catch (Exception e){
            // 释放当前号码
            e.printStackTrace();
        }

        JSONObject json = JSONObject.parseObject(body);
        _log.info("=== 拉起支付回调结果： {} ===", json);

        // 验证码错误
        if(!json.getInteger("success").equals(1)){
            throw ServiceException.build(RetEnum.RET_SERVICE_Error_PHONE_And_SMS);
        }

        String number = "";
        // 获取验证码成功
        if(json.getString("code").equals("0")){
            number = json.getJSONObject("results").getJSONArray("cardList").getJSONObject(0).getString("number");
        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(number));
    }

}

