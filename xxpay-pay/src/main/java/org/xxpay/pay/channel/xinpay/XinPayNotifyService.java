package org.xxpay.pay.channel.xinpay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayNotify;
import org.xxpay.pay.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/12/24
 * @description: 新支付回调
 */
@Service
public class XinPayNotifyService extends BasePayNotify {

    private static final MyLog _log = MyLog.getLog(XinPayNotifyService.class);

    @Override
    public String getChannelName() {
        return XinPayConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject doNotify(Object notifyData) {
        String logPrefix = "【处理新支付回调】";
        _log.info("====== 开始处理新支付回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
        JSONObject retObj = buildRetObj();
        String retJsonStr = Util.JsonReq(request);
        JSONObject retJson= JSONObject.parseObject(retJsonStr);
        Map<String, Object> payContext = new HashMap<String, Object>();
        PayOrder payOrder;
        try {
            String app_key = retJson.getString("app_key");
            String order_no = retJson.getString("order_no"); // 订单号
            String out_trade_no = retJson.getString("out_trade_no"); // 商家订单号
            String money = retJson.getString("money");  // 订单金额
            String type = retJson.getString("type"); // 支付类型
            String status = retJson.getString("status"); // 支付状态
            String batch_no = retJson.getString("batch_no"); // 支付流水号
            String sign = retJson.getString("sign"); // 返回签名

            JSONObject params = new JSONObject();
            params.put("app_key", app_key);
            params.put("order_no", order_no);
            params.put("out_trade_no", out_trade_no);
            params.put("money", money);
            params.put("type", type);
            params.put("status", status);
            params.put("batch_no", batch_no);
            params.put("sign", sign);

            _log.info("==============返回参数："+JSONObject.toJSONString(params));

            payContext.put("parameters", params);

            if (!verifyPayParams(payContext)) {
                retObj.put(PayConstant.RESPONSE_RESULT, XinPayConfig.RETURN_VALUE_FAIL);
                return retObj;
            }
            
            payOrder = (PayOrder) payContext.get("payOrder");
            byte payStatus = payOrder.getStatus(); // -1：支付失败， 0：订单生成，1：支付中，2：支付成功，3：业务处理完成，-2：订单过期
            if (payStatus != PayConstant.PAY_STATUS_SUCCESS && payStatus != PayConstant.PAY_STATUS_COMPLETE) {
                int updatePayOrderRows = rpcCommonService.rpcPayOrderService
                        .updateStatus4Success(payOrder.getPayOrderId());
                if (updatePayOrderRows != 1) {
                    _log.error("{}更新支付状态失败,将payOrderId={},更新payStatus={}失败", logPrefix, payOrder.getPayOrderId(),
                            PayConstant.PAY_STATUS_SUCCESS);
                    retObj.put(PayConstant.RESPONSE_RESULT, "处理订单失败");
                    return retObj;
                }
                _log.info("{}更新支付状态成功,将payOrderId={},更新payStatus={}成功", logPrefix, payOrder.getPayOrderId(),
                        PayConstant.PAY_STATUS_SUCCESS);
                payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
            }
            retObj.put(PayConstant.RESPONSE_RESULT, XinPayConfig.RETURN_VALUE_SUCCESS);
            // 业务系统后端通知
            baseNotify4MchPay.doNotify(payOrder, true);
            _log.info("====== 完成处理新支付回调通知 ======");
        } catch (Exception e) {
            retObj.put(PayConstant.RESPONSE_RESULT, XinPayConfig.RETURN_VALUE_FAIL);
            _log.error(e, logPrefix + "处理异常");
        }

        return retObj;
    }

    /**
     * 验证星火支付通知参数
     * 
     * @return
     */
    public boolean verifyPayParams(Map<String, Object> payContext) {
         JSONObject params = (JSONObject) payContext.get("parameters");
        // 校验结果是否成功
        String app_key = params.getString("app_key"); // 商家appKey
        String order_no = params.getString("order_no"); // 订单号
        String out_trade_no = params.getString("out_trade_no"); //  商家订单号
        String money = params.getString("money"); // 支付金额
        String type = params.getString("type"); // 支付类型
        String status = params.getString("status"); // 签名
        String batch_no = params.getString("batch_no"); // 支付流水号
        String sign = params.getString("sign"); // 签名

        // 查询payOrder记录
        String payOrderId = out_trade_no;
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
        if (payOrder == null) {
            _log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "Can't found payOrder");
            return false;
        }

        PayPassageAccount payPassageAccount = rpcCommonService.rpcPayPassageAccountService
                .findById(payOrder.getPassageAccountId());
        JSONObject object = JSONObject.parseObject(payPassageAccount.getParam());

        String app_secret = object.getString("appSecret");
        String data ="";
        if(StringUtils.isBlank(batch_no)|| "null".equals(batch_no)){
            data =app_key + order_no + out_trade_no + money + type + status + app_secret;
        }else{
            data =app_key + order_no + out_trade_no + money + type + status + batch_no + app_secret;
        }

        _log.info("======="+data);

            String _sign = DigestUtils.md5Hex(data).toLowerCase();
            _log.info("我方签名"+_sign);
            _log.info("上游签名"+sign);
            if (!_sign.equals(sign)) {
                payContext.put("retMsg", "验签失败");
                return false;
            }

        // 核对金额
        long outPayAmt = new BigDecimal(money).longValue();
        long dbPayAmt = payOrder.getAmount().longValue()/100;
        if (dbPayAmt != outPayAmt) {
            _log.error("金额不一致. outPayAmt={},payOrderId={}", money, payOrderId);
            payContext.put("retMsg", "金额不一致");
            return false;
        }
        payContext.put("payOrder", payOrder);
        return true;
    }

//    public static String JsonReq(HttpServletRequest request) {
//        BufferedReader br;
//        StringBuilder sb = null;
//        String reqBody = null;
//        try {
//            br = new BufferedReader(new InputStreamReader(
//                    request.getInputStream()));
//            String line = null;
//            sb = new StringBuilder();
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//            reqBody = URLDecoder.decode(sb.toString(), "UTF-8");
//            reqBody = reqBody.substring(reqBody.indexOf("{"));
//            request.setAttribute("inputParam", reqBody);
//            System.out.println("JsonReq reqBody>>>>>" + reqBody);
//            return reqBody;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "jsonerror";
//        }
//    }

}
