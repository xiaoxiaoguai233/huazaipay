package org.xxpay.asst.order;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xxpay.asst.common.ctrl.BaseController;
import org.xxpay.asst.common.service.RpcCommonService;
import org.xxpay.asst.secruity.JwtUser;
import org.xxpay.asst.utils.RedisUtil;
import org.xxpay.asst.websocket.WebSocketServer;
import org.xxpay.core.common.Exception.ServiceException;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.entity.AssistantAccount;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.ZFkKami;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@RestController
@RequestMapping(Constant.MGR_CONTROLLER_ROOT_PATH + "/receive/ssskm")
public class OrderContorller extends BaseController {

//    private String imgUrl = "http://192.168.1.3:8194/upLoadfile/imagefile/";

    private String imgUrl = "http://assistant.astropay99.top/upLoadfile/imagefile/";


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
        zFkKami.setUser_id( user.getId() );

        int page = param.getInteger("page");
        int limit = param.getInteger("limit");

        List<ZFkKami> zFkKamiList = rpcCommonService.rpcZFkKamiService.selectList(zFkKami, page, limit);
        int count = rpcCommonService.rpcZFkKamiService.selectListCount(zFkKami.getUser_id());

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zFkKamiList, count));
    }



    @RequestMapping("/list/order")
    @ResponseBody
    public ResponseEntity<?> getlistByOrder(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZFkKami zFkKami = getObject(param, ZFkKami.class);

        PayOrder byPayOrderId = rpcCommonService.rpcPayOrderService.findByPayOrderId(zFkKami.getOrder_id());


        zFkKami.setAmount(byPayOrderId.getAmount());
        zFkKami.setOrder_id(null);
        zFkKami.setState("0");

        // 获取当前用户的信息
        JwtUser user = getUser();
        // 获取当前用户的ID。是否超级管理员 0：否, 1：是
        zFkKami.setUser_id( user.getId() );

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


    @RequestMapping("/get/update")
    @ResponseBody
    public ResponseEntity<?> update(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);

        ZFkKami zFkKami  = getObject(param, ZFkKami.class);

        zFkKami.setProvide_user_id( zFkKami.getUser_id() );
        zFkKami.setProvide_user_name(zFkKami.getUser_name());
        zFkKami.setState("1");
        zFkKami.setBuy_time(new Date());

        int update = rpcCommonService.rpcZFkKamiService.update(zFkKami);
        if(update == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }

    @RequestMapping("/get/amount/{orderId}")
    @ResponseBody
    public ResponseEntity<?> getRedisAmount(@PathVariable("orderId") String orderId){

        // 获取订单参数
        JSONObject params = RedisUtil.getObject(orderId, JSONObject.class);

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(params.getString("amount"))) ;
    }


    /**
     * 生成卡密
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("/account/add")
    @ResponseBody
    public ResponseEntity<?> add(HttpServletRequest request) throws IOException {
        JSONObject param = getJsonParam(request);

        JSONObject res = new JSONObject();


        Long amount = Long.valueOf(param.getString("amount")) * 100;
        Integer count = Integer.valueOf(param.getString("count"));

        if(count > 20) count = 20;

        // 获取当前用户的信息
        JwtUser user = getUser();

        // 获取当前用户的ID。是否超级管理员 0：否, 1：是
       Long user_id = user.getId();
       String user_name = user.getName();

        AssistantAccount byAssistantId = rpcCommonService.rpcAssistantAccountService.findByAssistantId(user_id);
        Long balance = byAssistantId.getBalance();

        int i = 1;
        for ( ; i <= count ; i++){

            // 金额不足，推出
            if((balance - amount) > 0) {
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

                int insert = rpcCommonService.rpcZFkKamiService.insert(zFkKami);

                // 给商户减金额 -- 卡密生成
                if (insert != -1){
                    // 流水表添加记录  业务类型,1-管理员调账,2-卡密调账
                    rpcCommonService.rpcAssistantAccountService.debit2Account(user_id, MchConstant.ASST_BIZ_TYPE_CHANGE_BALANCE_KM, amount, MchConstant.BIZ_ITEM_BALANCE, card);
                }
                balance -= amount;
            }else{
                throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
            }
        }

        res.put("count", i);

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(res));
    }

    /**
     * 通知websocket通知码商
     * @param request
     * @return
     */
    @RequestMapping("/send_message_assistant/{assistantId}")
    @ResponseBody
    public ResponseEntity<?> send_message_assistant(HttpServletRequest request, @PathVariable("assistantId") String assistantId) throws IOException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fromUserId", "Server");
        jsonObject.put("toUserId", assistantId);
        jsonObject.put("message", "update");
        WebSocketServer.sendInfo(jsonObject.toJSONString());

        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }


    /**
     * 通知websocket通知码商
     * @param request
     * @return
     */
    @RequestMapping("/get_orders_info")
    @ResponseBody
    public ResponseEntity<?> get_orders_info(HttpServletRequest request) {

        // 获取当前用户的信息
        JwtUser user = getUser();

        List<JSONObject> objects = new ArrayList<JSONObject>();
        Collection<String> keys = RedisUtil.keys("P*");

        long l1 = System.currentTimeMillis();

        for (String key : keys){
            JSONObject params = RedisUtil.getObject(key, JSONObject.class);
            String assistantId = params.getString("assistantId");
            if (assistantId == null || !assistantId.equals(String.valueOf(user.getId()))) {
                continue;
            }

            Long visit_time = params.getLong("visit_time");

            long l = l1 - visit_time;
            if (l >= 60000){
                params.put("online_status", "掉线");
            }else {
                params.put("online_status", "在线");
            }
            objects.add(params);
        }

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(objects));
    }





    /**
     * 图片上传到服务器文件夹中
     * @param
     * @return imgurl图片路径，success接口状态
     */
    @RequestMapping("/uploadimage")
    @ResponseBody
    public String uploadPicture(@RequestParam(value = "file", required = false) MultipartFile file,
                                HttpServletRequest request, HttpServletResponse response) {
        JSONObject obj = new JSONObject();
        File targetFile = null;
        String url = "";// 返回存储路径
        String fileName = file.getOriginalFilename();// 获取文件名加后缀
        if (fileName != null && fileName != "") {
            //文件存储位置
            ServletContext scontext = request.getSession().getServletContext();
            // 获取绝对路径
            String path = scontext.getRealPath("/") + "upLoadfile/imagefile";
            String lastname = fileName.substring(fileName.lastIndexOf("."), fileName.length());//文件后缀
            fileName = new Date().getTime() + "_" + new Random().nextInt(1000) + lastname;//当前时间+随机数=新的文件名
            // 如果文件夹不存在则创建
            File pathfile = new File(path);
            if (!pathfile.exists()) {
                pathfile.mkdirs();
            }
            // 将图片存入文件夹
            targetFile = new File(path, fileName);
            try {
                // 将上传的文件写到服务器上指定的文件。
                file.transferTo(targetFile);
                obj.put("success", true);
                url = fileName;//保存路径，便于后续存入数据库
            } catch (Exception e) {
                e.printStackTrace();
                obj.put("success", false);
                obj.put("errorMsg", e.getMessage());
            }
        }
        obj.put("imgurl", imgUrl + url);
        return obj.toString();
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



    @RequestMapping("/kami/get")
    @ResponseBody
    public ResponseEntity<?> getById(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZFkKami zFkKami = getObject(param, ZFkKami.class);

        // 搜索手机号是否重复, 如果为 null，那就反馈错误（CODE:11022）
        ZFkKami zFkKami1 = rpcCommonService.rpcZFkKamiService.selectById(zFkKami.getId());
        if (zFkKami1 == null){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(zFkKami1));
    }

    @RequestMapping("/kami/update")
    @ResponseBody
    public ResponseEntity<?> kami_update(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        ZFkKami zFkKami = getObject(param, ZFkKami.class);

        if (zFkKami.getState() == "2"){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }

        ZFkKami zFkKami1 = rpcCommonService.rpcZFkKamiService.selectById(zFkKami.getId());

        zFkKami1.setProvide_user_id( zFkKami.getUser_id() );
        zFkKami1.setProvide_user_name(zFkKami.getUser_name());
        zFkKami1.setBuy_time(new Date());

        zFkKami1.setState(zFkKami.getState());

        int update = rpcCommonService.rpcZFkKamiService.update(zFkKami1);
        if(update == 0){
            throw ServiceException.build(RetEnum.RET_COMM_OPERATION_FAIL);
        }
        return ResponseEntity.ok(XxPayPageRes.buildSuccess());
    }
}
