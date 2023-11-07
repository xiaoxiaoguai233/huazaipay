package org.xxpay.pay.channel.jwwx;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.DateUtils;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.XXPayUtil;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.mq.BaseNotify4MchPay;

import com.alibaba.fastjson.JSONObject;

 
 /**
 * @author: dingzhiwei
 * @date: 18/3/1
 * @description: 个付通支付接口
 */
@Service
public class JwwxPaymentService extends BasePayment {

    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    private static final MyLog _log = MyLog.getLog(JwwxPaymentService.class);

    @Override
    public String getChannelName() {
        return JwwxpayConfig.CHANNEL_NAME;
    }

    /**
     * 支付
     * @param payOrder
     * @return
     */
    @Override
    public JSONObject pay(PayOrder payOrder) {
        String channelId = payOrder.getChannelId();
        JSONObject retObj;
        switch (channelId) {
            case JwwxpayConfig.CHANNEL_NAME_WXPAY_QR :
                retObj = doPayQrReq(payOrder, "wechat");
                break;
             default:
                retObj = buildRetObj(PayConstant.RETURN_VALUE_FAIL, "不支持的渠道[channelId="+channelId+"]");
                break;
        }
        return retObj;
    }

    /**
     * 查询订单
     * @param payOrder
     * @return
    
    @Override
    public JSONObject query(PayOrder payOrder) {

        String logPrefix = "【个付通订单查询】";
        String payOrderId = payOrder.getPayOrderId();

        _log.info("{}开始查询个付通订单,payOrderId={}", logPrefix, payOrderId);
        JwwxpayConfig jwwxpayConfig = new JwwxpayConfig(getPayParam(payOrder));
  
         JSONObject retObj = buildRetObj();
        retObj.put("status", 1);    // 支付中
        String status = "";
        try {
      		String currentTime = DateUtils.getCurrentTimeStrDefault();
     		String createTime=DateUtils.getTimeStrDefault(payOrder.getCreateTime());
     		String merchant=jwwxpayConfig.getMchId();
     		String orderNo=payOrder.getPayOrderId();
     		String key=jwwxpayConfig.getKey();
     		
    		Map<String, String> parameters = new TreeMap<String, String>();
    		parameters.put("merchant", merchant);
    		parameters.put("orderNo", orderNo);
    		parameters.put("currentTime", currentTime);
    		parameters.put("createTime",createTime );

    		StringBuffer sb = new StringBuffer();
    		sb.append("createTime=" + createTime);
    		sb.append("&currentTime=" + currentTime);
    		sb.append("&merchant=" +  merchant);
    		sb.append("&orderNo=" + orderNo);
    		String _sign = DigestUtils.md5Hex(sb.toString() + "#" + key);
    		parameters.put("sign", _sign);
            String queryUrl = jwwxpayConfig.getReqUrl().replace("/pay", "") + "/query?";
            String result = XXPayUtil.call4Post(queryUrl + parameters);
               _log.info("{}返回数据:{}", logPrefix, result);
            JSONObject resObj = JSONObject.parseObject(result);
            status = resObj.getString("status");
        } catch (Exception e) {
            _log.error(e, "");
        }
        if("3".equals(status)) {
            retObj.put("status", 2);    // 成功
        }else if("1".equals(status)) {
            retObj.put("status", 1);    // 支付中
        }
        return retObj;
    } */

    /**
     * 微信扫码支付
     * @param payOrder
     * @return
     */
    public JSONObject doPayQrReq(PayOrder payOrder, String channel) {
        JwwxpayConfig jwwxConfig = new JwwxpayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        JSONObject paramMap = new JSONObject();
         try {
             String reqData = XXPayUtil.genUrlParams(paramMap);
            _log.info("[{}]请求数据:{}", getChannelName(), reqData);
            _log.info("jwwxConfig:{}",JSONObject.toJSONString(jwwxConfig));

     		String merchant =jwwxConfig.getMchId();
    		String notifyUrl = payConfig.getNotifyUrl(getChannelName());
    		String orderNo =payOrder.getPayOrderId();
    		String payType = "wxpay";
    		String remark ="wxpay";
     		String currentTime = DateUtils.getCurrentTimeStrDefault();
    		String key =jwwxConfig.getKey();
    		String payUrl = jwwxConfig.getReqUrl();
    		String returnUrl=payConfig.getReturnUrl(getChannelName());
    		DecimalFormat    df   = new DecimalFormat("######0.00");   

    		String amount=df.format(payOrder.getAmount().doubleValue()/100);

    		StringBuffer sb = new StringBuffer();
    		sb.append("amount=" + amount);
    		sb.append("&currentTime=" + currentTime);
    		sb.append("&merchant=" + merchant);
    		sb.append("&notifyUrl=" + notifyUrl);
    		sb.append("&orderNo=" + orderNo);
    		sb.append("&payType=" + payType);
    		if (StringUtils.isNotBlank(remark)) {
    			sb.append("&remark=" + remark);
    		}
     		_log.info("jwwxConfig.key:{}", jwwxConfig.getKey());
    		
    		sb.append("&returnUrl=" + returnUrl);
     		_log.info("sb:{}",sb.toString() + "#" + key);
      		String _sign = DigestUtils.md5Hex(sb.toString() + "#" + key);
 
             int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(),"");
               _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), updateCount);
 
                StringBuffer payForm = new StringBuffer();
                String toPayUrl = jwwxConfig.getReqUrl();
                payForm.append("<form style=\"display: none\" action=\""+payUrl+"/\" method=\"post\">");
                payForm.append("<input name=\"version\" value=\"1\" >");
                payForm.append("<input name=\"amount\" value=\""+amount+"\" >");
                payForm.append("<input name=\"currentTime\" value=\""+currentTime+"\" >");
                 payForm.append("<input name=\"merchant\" value=\""+merchant+"\" >");
                payForm.append("<input name=\"notifyUrl\" value=\""+notifyUrl+"\" >");
                payForm.append("<input name=\"orderNo\" value=\""+orderNo+"\" >");
                payForm.append("<input name=\"payType\" value=\""+payType+"\" >");
                payForm.append("<input name=\"remark\" value=\""+remark+"\" >");
                payForm.append("<input name=\"returnUrl\" value=\""+returnUrl+"\" >");
                payForm.append("<input name=\"sign\" value=\""+_sign+"\" >");
                 payForm.append("<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >");
                payForm.append("</form>");
                payForm.append("<script>document.forms[0].submit();</script>");

                // 支付链接地址
                retObj.put("payOrderId", payOrder.getPayOrderId()); // 设置支付订单ID
                JSONObject payParams = new JSONObject();
                payParams.put("payUrl", payForm);
                String payJumpUrl = toPayUrl + "?amount=" + amount+ "&currentTime=" + currentTime +
                        "&merchant=" + merchant+"&notifyUrl="+notifyUrl+"&notifyUrl="+notifyUrl+"&orderNo="+orderNo
                        +"&payType="+payType+"&remark="+remark+"&returnUrl="+returnUrl+"&sign="+_sign;
                payParams.put("payJumpUrl", payJumpUrl);
                payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);
                retObj.put("payParams", payParams);
                retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);
                return retObj;
          
           
        } catch (Exception e) {
            _log.error(e, "");
            retObj.put("errDes", "操作失败!");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            return retObj;
        }
    }
    
    
    
 
 

}
