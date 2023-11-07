package org.xxpay.pay.channel.antpay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.XXPayUtil;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.mq.BaseNotify4MchPay;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * @author: dingzhiwei
 * @date: 18/3/1
 * @description: 蚂蚁支付接口
 */
@Service
public class AntpayPaymentService extends BasePayment {

    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    @Autowired

    private static final MyLog _log = MyLog.getLog(AntpayPaymentService.class);

    @Override
    public String getChannelName() {
        return AntPayConfig.CHANNEL_NAME;
    }

    /**
     * 支付
     *
     * @param payOrder
     * @return
     */
    @Override
    public JSONObject pay(PayOrder payOrder) {

        String channelId = payOrder.getChannelId();
        JSONObject retObj;
        switch (channelId) {
            case AntPayConfig.CHANNEL_NAME_ALIPAY_QR:
                retObj = doPayQrReq(payOrder, "alipay");
                break;
            default:
                retObj = buildRetObj(PayConstant.RETURN_VALUE_FAIL, "不支持的渠道[channelId=" + channelId + "]");
                break;
        }
        return retObj;
    }

    /**
     * @param payOrder
     * @return
     */
    public JSONObject doPayQrReq(PayOrder payOrder,String channel) {
        AntPayConfig topayConfig = new AntPayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        JSONObject paramMap = new JSONObject();
        String privateKey = topayConfig.getPrivateKey();

        try {
            String pay_orderid = payOrder.getPayOrderId(); // 20位订单号 时间戳+6位随机字符串组成
            String pay_notifyurl = payConfig.getNotifyUrl(getChannelName()); // 通知地址
            String pay_returnUrl = payConfig.getReturnUrl(getChannelName()); // 回调地址
            String appId= String.valueOf(payOrder.getChannelMchId());
            String payType= channel;
            String payRemark = String.valueOf(payOrder.getSubMchId())=="null"?"111111":String.valueOf(payOrder.getSubMchId());
            DecimalFormat df = new DecimalFormat("######0.00");
            String pay_amount = df.format(payOrder.getAmount().doubleValue()/100);
            String stringSignTemp = "appId="+appId+"&notifyUrl="+pay_notifyurl+"&orderSn="+pay_orderid+"&payRemark="+ payRemark +"&payType="+ payType+"&returnUrl="+pay_returnUrl+"&totalFee="+pay_amount+"&key="+privateKey;
            System.out.println("===" + stringSignTemp);
            String pay_md5sign = DigestUtils.shaHex(stringSignTemp).toUpperCase();
            HashMap<String, Object> param = new HashMap<>();
            param.put("appId", appId);
            param.put("notifyUrl", pay_notifyurl);
            param.put("orderSn", pay_orderid);
            param.put("payRemark", payRemark);
            param.put("payType", payType);
            param.put("returnUrl", pay_returnUrl);
            param.put("totalFee", pay_amount);
            param.put("paySign", pay_md5sign);
            String reqData = XXPayUtil.genUrlParams(paramMap);

            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(pay_orderid, payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), payOrder.getPayOrderId(), updateCount);
            StringBuffer payForm = new StringBuffer();
            String toPayUrl = topayConfig.getReqUrl();
            payForm.append("<form style=\"display: none\" action=\"" + toPayUrl + "\" method=\"post\">");
            payForm.append("<input name=\"appId\" value=\"" + appId+ "\" >");
            payForm.append("<input name=\"notifyUrl\" value=\"" + pay_notifyurl + "\" >");
            payForm.append("<input name=\"orderSn\" value=\"" + pay_orderid + "\" >");
            payForm.append("<input name=\"payRemark\" value=\""+payRemark + "\" >");
            payForm.append("<input name=\"payType\" value=\"" + payType + "\" >");
            payForm.append("<input name=\"returnUrl\" value=\"" + pay_returnUrl + "\" >");
            payForm.append("<input name=\"totalFee\" value=\"" + pay_amount + "\" >");
            payForm.append("<input name=\"paySign\" value=\"" + pay_md5sign + "\" >");
            payForm.append("<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >");
            payForm.append("</form>");
            payForm.append("<script>document.forms[0].submit();</script>");

            // 支付链接地址
            retObj.put("payOrderId", payOrder.getPayOrderId()); // 设置支付订单ID
            JSONObject payParams = new JSONObject();
            payParams.put("payUrl", payForm);
            String payJumpUrl = toPayUrl + "?" + reqData;
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


    public static void main(String[] args) throws Exception {
//        String appId = "774583846";
//        String notifyUrl = "http://www.baidu.com";
//        String orderSn = "15659402363891509583";
//        String payRemark = "tongda";
//        String payType = "alipay";
//        String returnUrl = "http://www.baidu.com";
//        String totalFee = "10.00";
//        String paySign = "";
//        String key = "6963303b28f5917964b5a72ba72620a95e56a57d";
//
//        String stringSignTemp = "appId="+appId+"&notifyUrl="+notifyUrl+"&orderSn="+orderSn+"&payRemark="+payRemark+"&payType="+payType+"&returnUrl="+returnUrl+"&totalFee="+totalFee+"&key="+key;
//        System.out.printf("++++++签名加密前："+stringSignTemp);
//
//        String sign = DigestUtils.shaHex(stringSignTemp).toUpperCase();
//        System.out.printf("======签名加密后："+sign);
//        HashMap<String,Object> param= new HashMap<>();
//        param.put("appId",appId);
//        param.put("notifyUrl",notifyUrl);
//        param.put("orderSn",orderSn);
//        param.put("payRemark",payRemark);
//        param.put("payType",payType);
//        param.put("returnUrl",returnUrl);
//        param.put("totalFee",totalFee);
//        param.put("paySign",sign);
//        System.out.println(param.toString());
//        String s = XXPayUtil.genUrlParams(param);
//        String url_ = TopayConfig.PAY_URL + "?" + s;
//        System.out.println(url_);
//        String  s1 = HttpClient.callHttpPost(url_,120);
//        System.out.printf(s1);

//        String orderSn ="P01201908162152484880000";
//        String payTime="1565978146";
//        String totalFee="1.00";
//        String paySign="270C41CB3F28B82843959578F7420BA120AAF1B8";
//        String appId="774583846";
//        String payRemark="111111";
//        String stringSignTemp ="appId="+appId+"&orderSn="+orderSn+"&payRemark="+payRemark+"&payTime="+payTime+"&totalFee="+totalFee+"&key="+"452a78ddd69f97f0bca39d9b1dc448ba53022a2d";;
//        String pay_md5sign = DigestUtils.shaHex(stringSignTemp).toUpperCase();
//        _log.info(pay_md5sign,"----",String.valueOf(pay_md5sign==paySign));
//        _log.info(String.valueOf(pay_md5sign.equals(paySign)));
    }

}
