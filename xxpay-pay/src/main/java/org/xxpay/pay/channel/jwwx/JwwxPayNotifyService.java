package org.xxpay.pay.channel.jwwx;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.PayDigestUtil;
import org.xxpay.core.entity.MchInfo;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayNotify;
import org.xxpay.pay.channel.gepay.GepayConfig;
import org.xxpay.pay.service.PayOrderService;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/12/24
 * @description: 个付通支付回调
 */
@Service
public class JwwxPayNotifyService extends BasePayNotify {

    private static final MyLog _log = MyLog.getLog(JwwxPayNotifyService.class);
    
 
    @Override
    public String getChannelName() {
        return JwwxpayConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject doNotify(Object notifyData) {
        String logPrefix = "【处理个付通支付回调】";
        _log.info("====== 开始处理个付通支付回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
        JSONObject retObj = buildRetObj();
        Map<String, Object> payContext = new HashMap<String, Object>();
        PayOrder payOrder=new PayOrder();
        String respString = JwwxpayConfig.RETURN_VALUE_FAIL;
        try {
            JSONObject params = new JSONObject();
            
        String accFlag = request.getParameter("accFlag"); //账号所属（1平台、2商户）
        String accName = request.getParameter("accName"); //收款账号（微信账号、支付宝账号等）
        String amount = request.getParameter("amount"); //充值金额（单位元，两位小数）
        String createTime = request.getParameter("createTime"); //创建时间 ( 格式为：yyyyMMddHHmmss )
        String currentTime = request.getParameter("currentTime"); //当前时间 ( 格式为：yyyyMMddHHmmss )
        String merchant = request.getParameter("merchant"); //商户号
        String orderNo = request.getParameter("orderNo"); //充值订单号
        String payFlag = request.getParameter("payFlag"); //支付状态 ( 1未支付，2已支付，3已关闭 )
        String payTime = request.getParameter("payTime"); //支付时间 ( 格式为：yyyyMMddHHmmss )
        String payType = request.getParameter("payType"); //支付类型（alipay=支付宝）
        String remark = request.getParameter("remark"); //备注信息
        String systemNo = request.getParameter("systemNo"); //系统订单号
        String sign = request.getParameter("sign"); //签名
        
  
            
             params.put("accFlag",accFlag);
             params.put("accName",accName);
             params.put("amount",amount);
             params.put("createTime",createTime);
             params.put("currentTime", currentTime);
             params.put("merchant",merchant);
             params.put("orderNo",orderNo);
             params.put("payFlag",payFlag);
             params.put("payTime",payTime);
             params.put("payType",payType);
             params.put("remark",remark);
             params.put("systemNo",systemNo);
             params.put("sign",sign);
             
             _log.info("params:{}", JSONObject.toJSONString(params));
             payContext.put("parameters", params);
             
            

             _log.info("payOrder:{}", JSONObject.toJSONString(payOrder));


             
              if(!verifyPayParams(payContext)) {
                retObj.put(PayConstant.RESPONSE_RESULT, GepayConfig.RETURN_VALUE_FAIL);
                return retObj;
            }
              payOrder.setPayOrderId(orderNo);
              payOrder.setStatus(Integer.valueOf(payFlag).byteValue());
               payOrder.setChannelOrderNo(systemNo);
              
          
          
            // 处理订单
            byte payStatus = payOrder.getStatus(); // -1：支付失败， 0：订单生成，1：支付中，2：支付成功，3：业务处理完成，-2：订单过期
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
            _log.info("====== 完成处理个付通支付回调通知 ======");
            respString = JwwxpayConfig.RETURN_VALUE_SUCCESS;
        } catch (Exception e) {
            _log.error(e, logPrefix + "处理异常");
        }
        retObj.put(PayConstant.RESPONSE_RESULT, respString);
        return retObj;
    }

    /**
     * 验证个付通支付通知参数
     * @return
     */
    public boolean verifyPayParams(Map<String, Object> payContext) {
        JSONObject params = (JSONObject) payContext.get("parameters");
        // 校验结果是否成功
         String orderNo = params.getString("orderNo");		// 商户订单号

        // 查询payOrder记录
        String payOrderId =params.getString("orderNo");	
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
        if (payOrder == null) {
            _log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "Can't found payOrder");
            return false;
        }
          // 验证签名
        String accFlag = params.getString("accFlag"); //账号所属（1平台、2商户）
        String accName = params.getString("accName"); //收款账号（微信账号、支付宝账号等）
        String amount = params.getString("amount"); //充值金额（单位元，两位小数）
        String createTime = params.getString("createTime"); //创建时间 ( 格式为：yyyyMMddHHmmss )
        String currentTime = params.getString("currentTime"); //当前时间 ( 格式为：yyyyMMddHHmmss )
        String merchant = params.getString("merchant"); //商户号
         String payFlag = params.getString("payFlag"); //支付状态 ( 1未支付，2已支付，3已关闭 )
        String payTime = params.getString("payTime"); //支付时间 ( 格式为：yyyyMMddHHmmss )
        String payType = params.getString("payType"); //支付类型（alipay=支付宝）
        String remark = params.getString("remark"); //备注信息
        String systemNo = params.getString("systemNo"); //系统订单号
        String sign = params.getString("sign"); //签名
        
        PayPassageAccount payPassageAccount = rpcCommonService.rpcPayPassageAccountService.findById(payOrder.getPassageAccountId());
        JSONObject object = JSONObject.parseObject(payPassageAccount.getParam());

        
        if("2".equals(payFlag)) {
      	  String mySign = "accFlag="+accFlag +"&accName="+accName +"&amount="+amount +"&createTime="+createTime +"&currentTime="+currentTime +"&merchant="+merchant +"&orderNo="+orderNo +"&payFlag="+payFlag +"&payTime="+payTime +"&payType="+payType;
      	
      	  if (StringUtils.isNotBlank(remark)) {
      		  mySign = mySign +"&remark=" + remark;
      		}
            mySign = mySign + "&systemNo="+systemNo;
            //步骤3、把字段字符串通过“#"号与商户密钥拼接，得到最终的加密字符串
       	  String _sign = DigestUtils.md5Hex(mySign.toString() + "#" + object.getString("key"));
      	  if(!_sign.equals(sign)) {
      	      payContext.put("retMsg", "验签失败");
              return false;
      	  }
       }

        // 核对金额
        long outPayAmt = new BigDecimal(amount).multiply(new BigDecimal(100)).longValue();
        long dbPayAmt = payOrder.getAmount().longValue();
        if (dbPayAmt != outPayAmt) {
            _log.error("金额不一致. outPayAmt={},payOrderId={}", amount, payOrderId);
            payContext.put("retMsg", "金额不一致");
            return false;
        }
        payContext.put("payOrder", payOrder);
        return true;
    }

}
