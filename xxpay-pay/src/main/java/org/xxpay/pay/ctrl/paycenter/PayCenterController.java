package org.xxpay.pay.ctrl.paycenter;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.*;
import org.xxpay.core.entity.MchInfo;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.ZFkKami;
import org.xxpay.pay.channel.PayConfig;
import org.xxpay.pay.ctrl.constant.CS;
import org.xxpay.pay.ctrl.common.BaseController;
import org.xxpay.pay.service.RpcCommonService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.xxpay.pay.util.RedisUtil;

/**
 * @Description: 支付收银台
 * @author Allen 
 * @date 2019-07-27
 * @version V1.0
  */
@Controller
public class PayCenterController extends BaseController {

    private final MyLog _log = MyLog.getLog(PayCenterController.class);
    
    @Autowired public PayConfig payConfig;
    @Autowired private RpcCommonService rpcCommonService;

//    public String paySiteUrl = "http://payment.wuye6688.com/api/orders/";	// 代付补单任务开关

//    public String paySiteUrl = "http://pay.huazaipay.info/api/orders/";	// 代付补单任务开关



    // TODO 1. 新天龙八部
    public String paySiteUrl = "http://pay.lalapay.cyou/api/orders/";	// 代付补单任务开关
//
    private String imgUrl = "http://pay.lalapay.cyou/upLoadfile/imagefile/";  // 上传图片


//    public String paySiteUrl = "http://192.168.1.3:3020/api/orders/";	// 代付补单任务开关
//
//    private String imgUrl = "http://127.0.0.1:3020/upLoadfile/imagefile/";  // 上传图片


    @RequestMapping("/api/orders/{orderId}")
    public String orders(@PathVariable("orderId") String orderId, ModelMap model) throws ServletException, IOException {

        // 获取订单参数
        JSONObject params = RedisUtil.getObject(orderId, JSONObject.class);

        _log.info("{}", params);

        if(params == null){
            return "paycenter/order_expire";
        }

        model.put("orderId", orderId);
        model.put("payLink", params.getString("payLink"));
        model.put("amount", params.getString("amount"));

        model.put("expireTime", RedisUtil.getExpire(orderId));

        String method = params.getString("method");
        if (method.equals("alipay")){
            return "paycenter/alipay";
        }else {
            return "paycenter/wxpay";
        }
    }

    @RequestMapping("/api/waiting/{orderId}")
    public String waitingOrders(@PathVariable("orderId") String orderId, ModelMap model){

        // 获取订单参数
        JSONObject params = RedisUtil.getObject(orderId, JSONObject.class);

        _log.info("{}", params);

        if(params == null){
            return "paycenter/order_expire";
        }

        model.put("orderId", orderId);
        model.put("expireTime", RedisUtil.getExpire(orderId));
        return "paycenter/waiting";
    }

    @RequestMapping("/api/slrj/status/{orderId}")
    @ResponseBody
    public ResponseEntity<?> statusOrdersslrj(@PathVariable("orderId") String orderId){

        // 获取订单参数
        JSONObject params = RedisUtil.getObject(orderId, JSONObject.class);

        JSONObject jsonObject = new JSONObject();

        if (params == null){
            jsonObject.put("status", "-1");
            return ResponseEntity.ok(XxPayResponse.buildSuccess(jsonObject));
        }

        String status = params.getString("status");
        if (status.equals("0")){
            return ResponseEntity.ok(XxPayResponse.buildSuccess(status));
        }else {
            jsonObject.put("status", status);
            jsonObject.put("payLink", paySiteUrl + orderId);
            return ResponseEntity.ok(XxPayResponse.buildSuccess(jsonObject));
        }
    }


    @RequestMapping("/api/status/{orderId}")
    @ResponseBody
    public ResponseEntity<?> statusOrders(@PathVariable("orderId") String orderId){

        // 获取订单参数
        JSONObject params = RedisUtil.getObject(orderId, JSONObject.class);

        params.put("visit_time", System.currentTimeMillis());
        long expire_time = RedisUtil.getExpire(orderId);
        RedisUtil.setString(orderId, params.toJSONString(), expire_time);     // 一个小时


        return ResponseEntity.ok(XxPayResponse.buildSuccess(params));

//        JSONObject jsonObject = new JSONObject();

//        if (params == null){
//            jsonObject.put("status", "-1");
//            return ResponseEntity.ok(XxPayResponse.buildSuccess(jsonObject));
//        }
//
//        String status = params.getString("status");
//        if (status.equals("0")){
//            return ResponseEntity.ok(XxPayResponse.buildSuccess(status));
//        }else {
//            jsonObject.put("status", status);
//            jsonObject.put("payLink", paySiteUrl + orderId);
//            return ResponseEntity.ok(XxPayResponse.buildSuccess(jsonObject));
//        }
    }

    @RequestMapping("/api/update/{orderId}")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<?> updatestatusOrders(@PathVariable("orderId") String orderId, HttpServletRequest request) throws IOException {

        JSONObject param = getJsonParam(request);

        // 获取订单参数
        JSONObject redis_params = RedisUtil.getObject(orderId, JSONObject.class);

        String status = param.getString("status");

        // 订单状态一致就返回
//        if (redis_params.getString("status").equals(status)){
//            return ResponseEntity.ok(XxPayResponse.buildSuccess());
//        }

        long expire_time = RedisUtil.getExpire(redis_params.getString("orderId"));

        if (status.equals("1")){
            redis_params.put("status", status);
            redis_params.put("assistantId", param.getString("assistantId"));
            redis_params.put("pay_for_name", param.getString("pay_for_name"));
            redis_params.put("pay_method", param.getString("pay_method"));
            redis_params.put("expire_time", expire_time);
        }
        else if (status.equals("2")){
            redis_params.put("status", status);
            redis_params.put("imageurl", param.getString("imageurl"));
            redis_params.put("expire_time", expire_time);
        }
        else  if (status.equals("3") || status.equals("4")){
            redis_params.put("status", status);
        }
        else  if (status.equals("5") ){
            redis_params.put("card_pwd", param.getString("card_pwd"));
            redis_params.put("status", status);
        }


        RedisUtil.setString(redis_params.getString("orderId"), redis_params.toJSONString(), expire_time);     // 一个小时


        if (param.getString("asst_flag") == null){
            // 让socket通知码商更新
            Document conn = Jsoup.connect("http://127.0.0.1:8194/api/receive/ssskm/send_message_assistant/" + redis_params.getString("assistantId")).ignoreContentType(true).get();
        }


        return ResponseEntity.ok(XxPayResponse.buildSuccess());

    }


    /**
     * 图片上传到服务器文件夹中
     * @param
     * @return imgurl图片路径，success接口状态
     */
    @RequestMapping("/api/uploadimage")
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




    @RequestMapping("/api/createOrder")
    @ResponseBody
    public ResponseEntity<?> createOrder(HttpServletRequest request, ModelMap model) throws ServletException, IOException {
    	 
    	 // type目前两种类型,recharge:api充值,cashier:收银台充值
        String type = request.getParameter("type");
        String productId = request.getParameter("productId");
        String amount = request.getParameter("amount");
        String subject = request.getParameter("subject");
        String body = request.getParameter("body");
        String notifyUrl = request.getParameter("notifyUrl");
        String returnUrl = request.getParameter("returnUrl");
        String extra = request.getParameter("extra");
        String appId = request.getParameter("appId");
         String mchId=request.getParameter("mchId");
         
         String mchOrderNo=request.getParameter("mchOrderNo");
        
        BigDecimal amountB = new BigDecimal(amount);
        /*if(amountB.compareTo(new BigDecimal("10")) < 0){
        	return ResponseEntity.ok(new BizResponse(10001, "创建订单失败, 请输入大于10元的金额。"));
        }*/
        
        amountB = amountB.multiply(new BigDecimal("100")).setScale(0);
        
        MchInfo mchInfo = rpcCommonService.rpcMchInfoService.findByMchId(Long.valueOf(mchId));
        
        JSONObject retObj = createPayOrder(type, mchInfo.getMchId(), mchInfo.getPrivateKey(), appId, productId, amountB.toString(), subject, body, notifyUrl, returnUrl, extra,mchOrderNo,IPUtility.getClientIp(request));
        if(retObj == null) {
            return ResponseEntity.ok(new BizResponse(10001, "创建订单失败,没有返回数据"));
        }
        if("SUCCESS".equals(retObj.get("retCode"))) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retObj, mchInfo.getPrivateKey(), "sign");
            String retSign = (String) retObj.get("sign");
            if(checkSign.equals(retSign)) {
                return ResponseEntity.ok(XxPayPageRes.buildSuccess(retObj));
            }else {
                return ResponseEntity.ok(new BizResponse(10001, "创建订单失败,验证支付中心返回签名失败"));
            }
        }
        return ResponseEntity.ok(new BizResponse(10001, "创建订单失败," + retObj.getString("retMsg")));
          
      }
    
    

    /**
     * 调用XxPay支付系统,创建支付订单
     * @param type
     * @param mchId
     * @param key
     * @param appId
     * @param productId
     * @param amount
     * @param subject
     * @param body
     * @param notifyUrl
     * @return
     */
    JSONObject createPayOrder(String type, Long mchId, String key, String appId,
                              String productId, String amount, String subject, String body, String notifyUrl, 
                              String returnUrl, String extra,String mchOrderNo,String ip) {
        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", mchId);                               // 商户ID
        paramMap.put("appId", appId);                               // 应用ID
        paramMap.put("mchOrderNo", mchOrderNo);     // 商户订单号
        paramMap.put("productId", productId);                       // 支付产品
        paramMap.put("amount", amount);                             // 支付金额,单位分
        paramMap.put("currency", "cny");                            // 币种, cny-人民币
        paramMap.put("clientIp", ip);                 // 用户地址,微信H5支付时要真实的
        paramMap.put("device", "WEB");                              // 设备
        paramMap.put("subject", subject);                           // 商品标题
        paramMap.put("body", body);                                 // 商品内容
        paramMap.put("notifyUrl", notifyUrl);                       // 回调URL
        paramMap.put("returnUrl", returnUrl);                       // 跳转URl
        paramMap.put("param1", "");                                 // 扩展参数1
        paramMap.put("param2", "");                                 // 扩展参数2
        paramMap.put("extra", extra);                               // 附加参数
        // 生成签名数据
        String reqSign = PayDigestUtil.getSign(paramMap, key);
        paramMap.put("sign", reqSign);                              // 签名
        String reqData = "params=" + paramMap.toJSONString();
        _log.info("[xxpay] req:{}", reqData);
        String url = payConfig.getPayUrl() + "/pay/create_order?";
        // 发起Http请求下单
        String result = call4Post(url + reqData);
        _log.info("[xxpay] res:{}", result);
        JSONObject retObj = JSON.parseObject(result);
        return retObj;
    }
    
    
    /**
     * 发起HTTP/HTTPS请求(method=POST)
     * @param url
     * @return
     */
    public static String call4Post(String url) {
        try {
            URL url1 = new URL(url);
            if("https".equals(url1.getProtocol())) {
                return HttpClient.callHttpsPost(url);
            }else if("http".equals(url1.getProtocol())) {
                return HttpClient.callHttpPost(url);
            }else {
                return "";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    
    
    
 
}
