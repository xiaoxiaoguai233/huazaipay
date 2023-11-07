package org.xxpay.manage.receive.ctrl;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.entity.ZFkKami;
import org.xxpay.manage.common.ctrl.BaseController;
import org.xxpay.manage.common.service.RpcCommonService;
import org.xxpay.manage.secruity.JwtUser;
import org.xxpay.manage.utils.RedisUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping(Constant.MGR_CONTROLLER_ROOT_PATH + "/receive/ssskm")
public class ZKamiController  extends BaseController {


    @Autowired
    private RpcCommonService rpcCommonService;


    /* ******************************************************************************************************************
     *
     *                                                    收款账号
     *
     *  **************************************************************************************************************** */
    @RequestMapping("/list")
    @ResponseBody
    public ResponseEntity<?> getlist(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZFkKami zFkKami = getObject(param, ZFkKami.class);
        // 获取当前用户的信息
        JwtUser user = getUser();
        // 获取当前用户的ID。是否超级管理员 0：否, 1：是
//        zSlrjAccount.setParentId((int) user.getIsSuperAdmin() == 0 ? user.getId() : null);
//
//        zFkKami.setUser_id((int) user.getIsSuperAdmin() == 0 ? user.getId() : null);

        int page = param.getInteger("page");
        int limit = param.getInteger("limit");

        List<ZFkKami> zFkKamiList = rpcCommonService.rpcZFkKamiService.selectList(zFkKami, page, limit);
        int count = rpcCommonService.rpcZFkKamiService.selectListCount(zFkKami.getUser_id());

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zFkKamiList, count));
    }


    @RequestMapping("/adds/get")
    @ResponseBody
    public ResponseEntity<?> GetToken(HttpServletRequest request) throws IOException {
        JSONObject param = getJsonParam(request);
        JSONObject jsonObject = getObject(param, JSONObject.class);

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(jsonObject));
    }

    @RequestMapping("/account/add")
    @ResponseBody
    public ResponseEntity<?> add(HttpServletRequest request) throws IOException {
        JSONObject param = getJsonParam(request);


        Long amount = Long.valueOf(param.getString("amount")) * 100;
        Integer count = Integer.valueOf(param.getString("count"));

        if(count > 20) count = 20;

        // 获取当前用户的信息
        JwtUser user = getUser();

        // 获取当前用户的ID。是否超级管理员 0：否, 1：是
        Long user_id = user.getId();
        String user_name = user.getName();

        System.out.println(user_id);
        System.out.println(user_name);


        for (int i = 0 ; i < count ; i++){
            ZFkKami zFkKami;
            String card;
            String card_pwd = getNumber(6);
            while (true){
                card = getNumber(10);
                zFkKami = rpcCommonService.rpcZFkKamiService.selectByCard(card);
                if(zFkKami == null) break;
            }

            zFkKami = new ZFkKami();

            zFkKami.setUser_id(user_id);
            zFkKami.setUser_name(user_name);
            zFkKami.setAdd_time(new Date());
            zFkKami.setAmount(amount);
            zFkKami.setCard(card);
            zFkKami.setCard_pwd(card_pwd);

            rpcCommonService.rpcZFkKamiService.insert(zFkKami);
        }


        return ResponseEntity.ok(XxPayPageRes.buildSuccess(param));
    }


    public String getNumber(int num){
        int hashCode = java.util.UUID.randomUUID().toString().hashCode();
        if (hashCode <0){
            hashCode=-hashCode;
        }
        // 0 代表前面补充0
        // 10 代表长度为10
        // d 代表参数为正数型
        return String.format("%010d", hashCode).substring(0,num);
    }


    @RequestMapping("/asst/online/list")
    @ResponseBody
    public ResponseEntity<?> asst_online_list(HttpServletRequest request) throws IOException {

        // 获取在线码商列表
        List<String> online_assistants = RedisUtil.getObject("online_assistants", List.class);

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(online_assistants));
    }


    @RequestMapping("/vip/online/list")
    @ResponseBody
    public ResponseEntity<?> vip_online_list(HttpServletRequest request) throws IOException {

        List<JSONObject> objects = new ArrayList<JSONObject>();
        Collection<String> keys = RedisUtil.keys("P*");

        long l1 = System.currentTimeMillis();

        for (String key : keys){
            JSONObject params = RedisUtil.getObject(key, JSONObject.class);

            Long visit_time = params.getLong("visit_time");

            if(visit_time != null){
                long l = l1 - visit_time;
                if (l >= 60000){
                    params.put("online_status", "掉线");
                }else {
                    params.put("online_status", "在线");
                }
            }

            objects.add(params);
        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(objects));
    }



//
//    @RequestMapping("/account/get")
//    @ResponseBody
//    public ResponseEntity<?> getById(HttpServletRequest request) {
//        JSONObject param = getJsonParam(request);
//        ZFkKami zSlrjAccount = getObject(param, ZFkKami.class);
//
//        // 搜索手机号是否重复, 如果为 null，那就反馈错误（CODE:11022）
//        ZFkKami zSlrjAccount1 = rpcCommonService.rpcZFkKamiService.selectById(zSlrjAccount.getId());
//        if (zSlrjAccount1 == null){
//            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
//        }
//        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zSlrjAccount1));
//    }
//
//    @RequestMapping("/account/update")
//    @ResponseBody
//    public ResponseEntity<?> update(HttpServletRequest request) {
//        JSONObject param = getJsonParam(request);
//        ZFkKami zSlrjAccount = getObject(param, ZFkKami.class);
//
//        // 金额与限额 * 100
//        zSlrjAccount.setTodayMoney(zSlrjAccount.getTodayMoney() * 100);
//        zSlrjAccount.setTotalMoney(zSlrjAccount.getTotalMoney() * 100);
//        zSlrjAccount.setLimitDayMoney(zSlrjAccount.getLimitDayMoney() * 100);
//        zSlrjAccount.setLimitMaxMoney(zSlrjAccount.getLimitMaxMoney() * 100);
//
//        int update = rpcCommonService.rpcZFkKamiService.update(zSlrjAccount);
//        if(update == 0){
//            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
//        }
//
//        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
//    }
//
//    @RequestMapping("/account/delete")
//    @ResponseBody
//    public ResponseEntity<?> delete(HttpServletRequest request) {
//        JSONObject param = getJsonParam(request);
//        ZFkKami zSlrjAccount = getObject(param, ZFkKami.class);
//
//        int delete = rpcCommonService.rpcZFkKamiService.deleteByPrimaryKey(zSlrjAccount.getId());
//        if(delete == 0){
//            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
//        }
//        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
//    }
//

//
//
//    /**
//     * 清除当日所有的
//     * @param request
//     * @return
//     */
//    @RequestMapping("/account/clear")
//    @ResponseBody
//    public ResponseEntity<?> clear(HttpServletRequest request) {
//        int delete = rpcCommonService.rpcZFkKamiService.clearTodayMoney();
//        if(delete == 0){
//            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
//        }
//        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
//    }

}


