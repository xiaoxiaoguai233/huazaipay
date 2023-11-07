package org.xxpay.pay.channel.ssfpay;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayNotify;
import org.xxpay.pay.channel.gepay.GepayConfig;
import org.xxpay.pay.channel.ssfpay.utils.SsfpayUtils;
import org.xxpay.pay.util.Util;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: carter
 * @date: 2019-07-31
 * @description: 随手付回调
 */
@Service
public class SsfPayNotifyService extends BasePayNotify {

    private static final MyLog _log = MyLog.getLog(SsfPayNotifyService.class);

    @Override
    public String getChannelName() {
        return SsfpayConfig.CHANNEL_NAME;
    }
    
   

    @Override
    public JSONObject doNotify(Object notifyData) {
        String logPrefix = "【处理随手付回调】";
        _log.info("====== 开始处理随手付回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
        String retJsonStr = Util.JsonReq(request);//返回的json
        JSONObject retObj = buildRetObj();
        Map<String, Object> payContext = new HashMap<String, Object>();
        PayOrder payOrder;
        String respString = SsfpayConfig.RETURN_VALUE_FAIL;
        try {
            payContext.put("parameters", retJsonStr);
            if (!verifyPayParams(payContext)) {
                retObj.put(PayConstant.RESPONSE_RESULT, GepayConfig.RETURN_VALUE_FAIL);
                return retObj;
            }
            
            payOrder = (PayOrder) payContext.get("payOrder");
            
                
            byte payStatus = payOrder.getStatus(); //-1：支付失败，  0：订单生成，1：支付中，2：支付成功，3：业务处理完成，-2：订单过期
            if (payStatus != PayConstant.PAY_STATUS_SUCCESS && payStatus != PayConstant.PAY_STATUS_COMPLETE) {
                int updatePayOrderRows = rpcCommonService.rpcPayOrderService.updateStatus4Success(payOrder.getPayOrderId());
                if (updatePayOrderRows != 1) {
                    _log.error("{}更新支付状态失败,将payOrderId={},更新payStatus={}失败", logPrefix, payOrder.getPayOrderId(), PayConstant.PAY_STATUS_SUCCESS);
                    retObj.put(PayConstant.RESPONSE_RESULT, "处理订单失败");
                    return retObj;
                }
                _log.error("{}更新支付状态成功,将payOrderId={},更新payStatus={}成功", logPrefix, payOrder.getPayOrderId(), PayConstant.PAY_STATUS_SUCCESS);
                payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
            }
            // 业务系统后端通知
            baseNotify4MchPay.doNotify(payOrder, true);
            _log.info("====== 完成处理随手付回调通知 ======");
            respString = SsfpayConfig.RETURN_VALUE_SUCCESS;
                

        } catch (Exception e) {
            _log.error(e, logPrefix + "处理异常");
        }
        retObj.put(PayConstant.RESPONSE_RESULT, respString);
        return retObj;

    }
    
    /**
     * 验证随手付通知参数
     *
     * @return
     */
    public boolean verifyPayParams(Map<String, Object> payContext) {
        //JSONObject params = (JSONObject) payContext.get("parameters");
        JSONObject params = JSONObject.parseObject((String)payContext.get("parameters"));
        HashMap<String, String> signParam = new HashMap<String, String>();
        SsfpayUtils.JsonToMap(params, signParam);
        
        Object code = params.get("code");
        Object code_msg = params.get("code_msg");
        Object mch_id = params.get("mch_id");
        Object order_id = params.get("out_trade_no");//平台ID
        Object out_trade_no = params.get("order_id");//上游ID
        Object pay_type = params.get("pay_type");
        Object total_fee = params.get("total_fee");
        Object traid = params.get("traid");
        Object sign = params.get("sign");

        // 查询payOrder记录
        String payOrderId = order_id.toString();
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
        if (payOrder == null) {
            _log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "Can't found payOrder");
            return false;
        }

        PayPassageAccount payPassageAccount = rpcCommonService.rpcPayPassageAccountService.findById(payOrder.getPassageAccountId());
        JSONObject object = JSONObject.parseObject(payPassageAccount.getParam());
        String APP_KEY = object.getString("APP_KEY");
        
        StringBuffer sb = new StringBuffer();
        sb.append("code=" + code.toString());
        code_msg = URLEncoder.encode(code_msg.toString());
        sb.append("&code_msg=" + code_msg.toString());
        sb.append("&mch_id=" + mch_id.toString());
        sb.append("&order_id=" + out_trade_no.toString());//08011856137043275
        sb.append("&out_trade_no=" + order_id.toString());////P01201908011856080660010
        sb.append("&pay_type=" + pay_type.toString());
        sb.append("&total_fee=" + total_fee.toString());
        if(traid != null) sb.append("&traid=" + traid.toString());
        sb.append("&key=" + APP_KEY);
        String validSign = "";
        try {
            validSign = SsfpayUtils.md5(sb.toString()).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //System.out.println(validSign);
        if (!validSign.equals(sign)){
            payContext.put("retMsg", "验签失败");
            return false;
        }

        //核对金额
        long outPayAmt = new BigDecimal(total_fee.toString()).longValue();
        long dbPayAmt = payOrder.getAmount().longValue();
        if (dbPayAmt != outPayAmt) {
            _log.error("金额不一致. outPayAmt={},payOrderId={}", total_fee.toString(), payOrderId);
            payContext.put("retMsg", "金额不一致");
            return false;
        }
        
        //更新三方单号
        payOrder.setChannelOrderNo(order_id.toString());
        payContext.put("payOrder", payOrder);
        return true;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        
        StringBuffer sb = new StringBuffer();
        sb.append("code=" + "1");
        String code_msg = URLEncoder.encode("支付成功");
        sb.append("&code_msg=" + code_msg);
        sb.append("&mch_id=" + "S10003101");
        sb.append("&order_id=" + "08011856137043275");
        sb.append("&out_trade_no=" + "P01201908011856080660010");
        sb.append("&pay_type=" + "101");
        sb.append("&total_fee=" + "3000");
        sb.append("&traid=" + "20190801185624561001");
        sb.append("&key=" + "6217a447791d5120ad06720b7fd94ef5");
        String sign = SsfpayUtils.md5(sb.toString()).toUpperCase();
        
        System.out.println("sign: "+ sign);
    }

}
