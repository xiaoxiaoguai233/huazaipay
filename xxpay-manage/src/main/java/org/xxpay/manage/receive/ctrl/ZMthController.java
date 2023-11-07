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
import org.xxpay.core.entity.ZMthAccount;
import org.xxpay.manage.common.ctrl.BaseController;
import org.xxpay.manage.common.service.RpcCommonService;
import org.xxpay.manage.secruity.JwtUser;
import org.xxpay.manage.utils.mth.SignUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(Constant.MGR_CONTROLLER_ROOT_PATH + "/receive/mth")
public class ZMthController  extends BaseController {


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
        ZMthAccount zMthAccount = getObject(param, ZMthAccount.class);
        // 获取当前用户的信息
        JwtUser user = getUser();
        // 获取当前用户的ID。是否超级管理员 0：否, 1：是
        zMthAccount.setParentId((int) user.getIsSuperAdmin() == 0 ? user.getId() : null);

        int page = param.getInteger("page");
        int limit = param.getInteger("limit");

        List<ZMthAccount> zMthAccounts = rpcCommonService.rpcZMthAccountService.selectList(zMthAccount, page, limit);
        int count = rpcCommonService.rpcZMthAccountService.selectListCount(zMthAccount.getParentId());
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zMthAccounts, count));
    }

    @RequestMapping("/account/add")
    @ResponseBody
    public ResponseEntity<?> add(HttpServletRequest request) throws IOException {
        JSONObject param = getJsonParam(request);
        ZMthAccount zMthAccount = getObject(param, ZMthAccount.class);

        // 获取当前用户的信息
        JwtUser user = getUser();
        // 获取当前用户的ID。是否超级管理员 0：否, 1：是
        zMthAccount.setParentId(user.getId());

        // 搜索手机号是否重复, 如果为 null，那就反馈错误（CODE:11022）
        ZMthAccount zMthAccount1 = rpcCommonService.rpcZMthAccountService.selectByPhone(zMthAccount.getPhone());
        if (zMthAccount1 != null){
            throw ServiceException.build(RetEnum.RET_SERVICE_REPEAT_PHONE);
        }

        // 限额 * 100
        zMthAccount.setLimitDayMoney(zMthAccount.getLimitDayMoney() * 100);
        zMthAccount.setLimitMaxMoney(zMthAccount.getLimitMaxMoney() * 100);

        // 获取Address
        Request request_getAddress = new Request.Builder()
                .url("https://nb.quanqiuwa.com/api/users/app/addresses")
                .get()
                .build();
        JSONObject sign = SignUtil.sign(request_getAddress);
        request_getAddress = request_getAddress.newBuilder()
                .addHeader("x-qqw-token", zMthAccount.getToken())
                .addHeader("x-qqw-client-version", "android-KJLJC2J7I9-4.0.2")
                .addHeader("x-qqw-request-nonce", sign.getString("x-qqw-request-nonce"))
                .addHeader("x-qqw-request-sign", sign.getString("x-qqw-request-sign"))
                .build();
        Response response = new OkHttpClient().newCall(request_getAddress).execute();
        JSONObject jsonObject = JSONObject.parseObject(response.body().string());
        zMthAccount.setAddressId(Long.valueOf(jsonObject.getJSONArray("data").getJSONObject(0).getInteger("id")));

        int insert = rpcCommonService.rpcZMthAccountService.insert(zMthAccount);
        if(insert == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/account/get")
    @ResponseBody
    public ResponseEntity<?> getById(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZMthAccount zMthAccount = getObject(param, ZMthAccount.class);

        // 搜索手机号是否重复, 如果为 null，那就反馈错误（CODE:11022）
        ZMthAccount zMthAccount1 = rpcCommonService.rpcZMthAccountService.selectById(zMthAccount.getId());
        if (zMthAccount1 == null){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zMthAccount1));
    }

    @RequestMapping("/account/update")
    @ResponseBody
    public ResponseEntity<?> update(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZMthAccount zMthAccount = getObject(param, ZMthAccount.class);

        // 金额与限额 * 100
        zMthAccount.setTodayMoney(zMthAccount.getTodayMoney() * 100);
        zMthAccount.setTotalMoney(zMthAccount.getTotalMoney() * 100);
        zMthAccount.setLimitDayMoney(zMthAccount.getLimitDayMoney() * 100);
        zMthAccount.setLimitMaxMoney(zMthAccount.getLimitMaxMoney() * 100);

        int update = rpcCommonService.rpcZMthAccountService.update(zMthAccount);
        if(update == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/account/delete")
    @ResponseBody
    public ResponseEntity<?> delete(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZMthAccount zMthAccount = getObject(param, ZMthAccount.class);

        int delete = rpcCommonService.rpcZMthAccountService.deleteByPrimaryKey(zMthAccount.getId());
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

        ZMthAccount zMthAccount1 = rpcCommonService.rpcZMthAccountService.selectByPhone(phone);
        if (zMthAccount1 != null){
            throw ServiceException.build(RetEnum.RET_SERVICE_REPEAT_PHONE);
        }

        /*******************************************************
         *
         *                    获取Token
         *
         ******************************************************/

        String json = "{\"channelId\":\"1\",\"verificationCode\":\"848578\",\"phone\":\"13934565000\"}";

        jsonObject = JSONObject.parseObject(json);
        jsonObject.put("phone", phone);
        jsonObject.put("verificationCode", verificationCode);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), String.valueOf(jsonObject));

        Request request_getOrder = new Request.Builder()
                .url("https://nb.quanqiuwa.com/api/users/app/login/meihui")
                .post(body)
                .build();

        JSONObject sign = SignUtil.sign(request_getOrder);
        Request request_order = request_getOrder.newBuilder()
                .addHeader("x-qqw-client-version", "android-KJLJC2J7I9-4.0.2")
                .addHeader("x-qqw-request-nonce", sign.getString("x-qqw-request-nonce"))
                .addHeader("x-qqw-request-sign", sign.getString("x-qqw-request-sign"))
                .addHeader("x-qqw-channel-no", "KJLJC2J7I9")
                .build();

        System.out.println(sign.getString("x-qqw-request-nonce"));
        System.out.println(sign.getString("x-qqw-request-sign"));

        Response response = new OkHttpClient().newCall(request_order).execute();
        jsonObject = JSONObject.parseObject(response.body().string());

        System.out.println(jsonObject);

        // 验证码错误
        if(jsonObject.getString("code").equals("-1")){
            throw ServiceException.build(RetEnum.RET_SERVICE_Error_PHONE_And_SMS);
        }

        String token = "";
        // 获取验证码成功
        if(jsonObject.getString("code").equals("0")){
            token = jsonObject.getJSONObject("data").getString("token");
        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(token));
    }

}

