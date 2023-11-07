package org.xxpay.manage.receive.ctrl;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.core.common.Exception.ServiceException;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.entity.AssistantInfo;
import org.xxpay.core.entity.SysUser;
import org.xxpay.core.entity.ZSlrjAccount;
import org.xxpay.manage.common.ctrl.BaseController;
import org.xxpay.manage.common.service.RpcCommonService;
import org.xxpay.manage.secruity.JwtUser;
import org.xxpay.manage.utils.mth.SignUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(Constant.MGR_CONTROLLER_ROOT_PATH + "/receive/slrj")
public class ZSlrjController extends BaseController {


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
        ZSlrjAccount zSlrjAccount = getObject(param, ZSlrjAccount.class);
        // 获取当前用户的信息
        JwtUser user = getUser();

        int page = param.getInteger("page");
        int limit = param.getInteger("limit");

        List<ZSlrjAccount> zSlrjAccounts = rpcCommonService.rpcZSlrjAccountService.selectList(zSlrjAccount, page, limit);
        int count = rpcCommonService.rpcZMthAccountService.selectListCount(zSlrjAccount.getParentId());

        for(ZSlrjAccount zSlrjAccount_ : zSlrjAccounts){
            if (zSlrjAccount_.getParentId() >= 20000000){
                AssistantInfo byAssistantId = rpcCommonService.rpcAssistantInfoService.findByAssistantId(zSlrjAccount_.getParentId());
                zSlrjAccount_.setParentName(byAssistantId.getAssistantName());
            }else{
                SysUser sysUser = rpcCommonService.rpcSysService.findByUserId(zSlrjAccount_.getParentId());
                zSlrjAccount_.setParentName(sysUser.getNickName());
            }

        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zSlrjAccounts, count));
    }

    @RequestMapping("/account/add")
    @ResponseBody
    public ResponseEntity<?> add(HttpServletRequest request) throws IOException {
        JSONObject param = getJsonParam(request);
        ZSlrjAccount zSlrjAccount = getObject(param, ZSlrjAccount.class);

        // 获取当前用户的信息
        JwtUser user = getUser();
        // 获取当前用户的ID。是否超级管理员 0：否, 1：是
        zSlrjAccount.setParentId(user.getId());

        // 搜索手机号是否重复, 如果为 null，那就反馈错误（CODE:11022）
        ZSlrjAccount zSlrjAccount1 = rpcCommonService.rpcZSlrjAccountService.selectByPhone(zSlrjAccount.getPhone());
        if (zSlrjAccount1 != null){
            throw ServiceException.build(RetEnum.RET_SERVICE_REPEAT_PHONE);
        }

        // 限额 * 100
        zSlrjAccount.setLimitDayMoney(zSlrjAccount.getLimitDayMoney() * 100);
        zSlrjAccount.setLimitMaxMoney(zSlrjAccount.getLimitMaxMoney() * 100);

        int insert = rpcCommonService.rpcZSlrjAccountService.insert(zSlrjAccount);
        if(insert == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/account/get")
    @ResponseBody
    public ResponseEntity<?> getById(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZSlrjAccount zSlrjAccount = getObject(param, ZSlrjAccount.class);

        // 搜索手机号是否重复, 如果为 null，那就反馈错误（CODE:11022）
        ZSlrjAccount zSlrjAccount1 = rpcCommonService.rpcZSlrjAccountService.selectById(zSlrjAccount.getId());
        if (zSlrjAccount1 == null){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zSlrjAccount1));
    }

    @RequestMapping("/account/update")
    @ResponseBody
    public ResponseEntity<?> update(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZSlrjAccount zSlrjAccount = getObject(param, ZSlrjAccount.class);

        // 金额与限额 * 100
        zSlrjAccount.setTodayMoney(zSlrjAccount.getTodayMoney() * 100);
        zSlrjAccount.setTotalMoney(zSlrjAccount.getTotalMoney() * 100);
        zSlrjAccount.setLimitDayMoney(zSlrjAccount.getLimitDayMoney() * 100);
        zSlrjAccount.setLimitMaxMoney(zSlrjAccount.getLimitMaxMoney() * 100);

        int update = rpcCommonService.rpcZSlrjAccountService.update(zSlrjAccount);
        if(update == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/account/delete")
    @ResponseBody
    public ResponseEntity<?> delete(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZSlrjAccount zSlrjAccount = getObject(param, ZSlrjAccount.class);

        int delete = rpcCommonService.rpcZSlrjAccountService.deleteByPrimaryKey(zSlrjAccount.getId());
        if(delete == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/account/token/get")
    @ResponseBody
    public ResponseEntity<?> GetToken(HttpServletRequest request) throws IOException {
        JSONObject param = getJsonParam(request);
        JSONObject jsonObject = getObject(param, JSONObject.class);

        String phone = jsonObject.getString("phone");
        String verificationCode = jsonObject.getString("smsCode");

        ZSlrjAccount zSlrjAccount = rpcCommonService.rpcZSlrjAccountService.selectByPhone(phone);
        if (zSlrjAccount != null){
            throw ServiceException.build(RetEnum.RET_SERVICE_REPEAT_PHONE);
        }

        /*******************************************************
         *
         *                    获取Token
         *
         ******************************************************/

        String json = "{\"code\":\"\",\"phone\":\"\",\"os_info\":\"\",\"name\":\"\"}";

        jsonObject = JSONObject.parseObject(json);
        jsonObject.put("phone", phone);
        jsonObject.put("code", verificationCode);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), String.valueOf(jsonObject));

        Request request_getOrder = new Request.Builder()
                .url("https://app.homeinns.com/api/v3/user/sms_login")
                .post(body)
                .build();

        Response response = new OkHttpClient().newCall(request_getOrder).execute();
        jsonObject = JSONObject.parseObject(response.body().string());

        System.out.println(jsonObject);

        // 验证码错误
        if(!jsonObject.getString("result_code").equals("0")){
            throw ServiceException.build(RetEnum.RET_SERVICE_Error_PHONE_And_SMS);
        }

        String token = "";
        // 获取验证码成功
        if(jsonObject.getString("result_code").equals("0") && jsonObject.getString("result").equals("登录成功") ){
            token = jsonObject.getJSONObject("data").getString("auth_token");
        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(token));
    }


    /**
     * 清除当日所有的
     * @param request
     * @return
     */
    @RequestMapping("/account/clear")
    @ResponseBody
    public ResponseEntity<?> clear(HttpServletRequest request) {
        int delete = rpcCommonService.rpcZSlrjAccountService.clearTodayMoney();
        if(delete == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

}

