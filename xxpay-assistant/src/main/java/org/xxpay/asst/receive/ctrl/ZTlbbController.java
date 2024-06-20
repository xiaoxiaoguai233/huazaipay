package org.xxpay.asst.receive.ctrl;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.asst.common.ctrl.BaseController;
import org.xxpay.asst.common.service.RpcCommonService;
import org.xxpay.asst.secruity.JwtUser;
import org.xxpay.core.common.Exception.ServiceException;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.entity.AssistantInfo;
import org.xxpay.core.entity.ZTlbbAccount;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(Constant.MGR_CONTROLLER_ROOT_PATH + "/receive/tlbb")
public class ZTlbbController extends BaseController {


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
        ZTlbbAccount zTlbbAccount = getObject(param, ZTlbbAccount.class);
        // 获取当前用户的信息
        JwtUser user = getUser();
        // 获取当前用户的ID。是否超级管理员 0：否, 1：是
        zTlbbAccount.setParentId(user.getId());


        int page = param.getInteger("page");
        int limit = param.getInteger("limit");

        List<ZTlbbAccount> zTlbbAccounts = rpcCommonService.rpcZTlbbAccountService.selectList(zTlbbAccount, page, limit);
        int count = rpcCommonService.rpcZTlbbAccountService.selectListCount(zTlbbAccount.getParentId());

        for(ZTlbbAccount zTlbbAccount_ : zTlbbAccounts){
            AssistantInfo byAssistantId = rpcCommonService.rpcAssistantInfoService.findByAssistantId(zTlbbAccount_.getParentId());
            zTlbbAccount_.setParentName(byAssistantId.getAssistantName());
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zTlbbAccounts, count));
    }

    @RequestMapping("/account/add")
    @ResponseBody
    public ResponseEntity<?> add(HttpServletRequest request) throws IOException {
        JSONObject param = getJsonParam(request);
        ZTlbbAccount zTlbbAccount = getObject(param, ZTlbbAccount.class);

        // 获取当前用户的信息
        JwtUser user = getUser();
        // 获取当前用户的ID。是否超级管理员 0：否, 1：是
        zTlbbAccount.setParentId(user.getId());

        // 搜索手机号是否重复, 如果为 null，那就反馈错误（CODE:11022）
        ZTlbbAccount zTlbbAccount1 = rpcCommonService.rpcZTlbbAccountService.selectByToken(zTlbbAccount.getToken());
        if (zTlbbAccount1 != null){
            throw ServiceException.build(RetEnum.RET_SERVICE_REPEAT_TOKEN);
        }

        // 限额 * 100
        zTlbbAccount.setLimitDayMoney(zTlbbAccount.getLimitDayMoney() * 100);
        zTlbbAccount.setLimitMaxMoney(zTlbbAccount.getLimitMaxMoney() * 100);

        int insert = rpcCommonService.rpcZTlbbAccountService.insert(zTlbbAccount);
        if(insert == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/account/get")
    @ResponseBody
    public ResponseEntity<?> getById(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZTlbbAccount zTlbbAccount = getObject(param, ZTlbbAccount.class);

        // 搜索手机号是否重复, 如果为 null，那就反馈错误（CODE:11022）
        ZTlbbAccount zTlbbAccount1 = rpcCommonService.rpcZTlbbAccountService.selectById(zTlbbAccount.getId());
        if (zTlbbAccount1 == null){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zTlbbAccount1));
    }

    @RequestMapping("/account/update")
    @ResponseBody
    public ResponseEntity<?> update(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZTlbbAccount zTlbbAccount = getObject(param, ZTlbbAccount.class);

        // 金额与限额 * 100
        zTlbbAccount.setTodayMoney(zTlbbAccount.getTodayMoney() * 100);
        zTlbbAccount.setTotalMoney(zTlbbAccount.getTotalMoney() * 100);
        zTlbbAccount.setLimitDayMoney(zTlbbAccount.getLimitDayMoney() * 100);
        zTlbbAccount.setLimitMaxMoney(zTlbbAccount.getLimitMaxMoney() * 100);

        int update = rpcCommonService.rpcZTlbbAccountService.update(zTlbbAccount);
        if(update == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/account/delete")
    @ResponseBody
    public ResponseEntity<?> delete(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZTlbbAccount zTlbbAccount = getObject(param, ZTlbbAccount.class);

        int delete = rpcCommonService.rpcZTlbbAccountService.deleteByPrimaryKey(zTlbbAccount.getId());
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

        ZTlbbAccount zTlbbAccount = rpcCommonService.rpcZTlbbAccountService.selectByPhone(phone);
        if (zTlbbAccount != null){
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
        int delete = rpcCommonService.rpcZTlbbAccountService.clearTodayMoney();
        if(delete == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

}


