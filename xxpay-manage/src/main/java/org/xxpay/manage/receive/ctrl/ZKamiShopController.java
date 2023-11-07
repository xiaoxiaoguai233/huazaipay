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
import org.xxpay.core.entity.SysUser;
import org.xxpay.core.entity.ZFkShop;
import org.xxpay.manage.common.ctrl.BaseController;
import org.xxpay.manage.common.service.RpcCommonService;
import org.xxpay.manage.secruity.JwtUser;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(Constant.MGR_CONTROLLER_ROOT_PATH + "/receive/ssskm_shop")
public class ZKamiShopController extends BaseController {


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
        ZFkShop zFkShop = getObject(param, ZFkShop.class);
        // 获取当前用户的信息
        JwtUser user = getUser();
   

        int page = param.getInteger("page");
        int limit = param.getInteger("limit");

        List<ZFkShop> zFkShops = rpcCommonService.rpcZFkKamiShopService.selectList(zFkShop, page, limit);

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zFkShops, zFkShops.size()));
    }

    @RequestMapping("/account/add")
    @ResponseBody
    public ResponseEntity<?> add(HttpServletRequest request) throws IOException {
        JSONObject param = getJsonParam(request);
        ZFkShop zFkShop = getObject(param, ZFkShop.class);

        // 获取当前用户的信息
        JwtUser user = getUser();

        // 搜索手机号是否重复, 如果为 null，那就反馈错误（CODE:11022）
        ZFkShop zFkShop1 = rpcCommonService.rpcZFkKamiShopService.selectByName(zFkShop.getName());
        if (zFkShop1 != null){
            throw ServiceException.build(RetEnum.RET_SERVICE_REPEAT_PHONE);
        }

        int insert = rpcCommonService.rpcZFkKamiShopService.insert(zFkShop);
        if(insert == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/account/get")
    @ResponseBody
    public ResponseEntity<?> getById(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZFkShop zFkShop = getObject(param, ZFkShop.class);

        // 搜索手机号是否重复, 如果为 null，那就反馈错误（CODE:11022）
        ZFkShop zFkShop1 = rpcCommonService.rpcZFkKamiShopService.selectById(zFkShop.getId());
        if (zFkShop1 == null){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zFkShop1));
    }

    @RequestMapping("/account/update")
    @ResponseBody
    public ResponseEntity<?> update(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZFkShop zFkShop = getObject(param, ZFkShop.class);

        int update = rpcCommonService.rpcZFkKamiShopService.update(zFkShop);
        if(update == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/account/delete")
    @ResponseBody
    public ResponseEntity<?> delete(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZFkShop zFkShop = getObject(param, ZFkShop.class);

        int delete = rpcCommonService.rpcZFkKamiShopService.deleteByPrimaryKey(zFkShop.getId());
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

        ZFkShop zFkShop = rpcCommonService.rpcZFkKamiShopService.selectByName(phone);
        if (zFkShop != null){
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


}
