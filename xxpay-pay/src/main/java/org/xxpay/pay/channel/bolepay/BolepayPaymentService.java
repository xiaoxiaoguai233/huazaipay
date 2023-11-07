package org.xxpay.pay.channel.bolepay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.XXPayUtil;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.channel.bolepay.util.BolePayUtils;
import org.xxpay.pay.util.HttpClientUtils;
import org.xxpay.pay.mq.BaseNotify4MchPay;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;

@Service
public class BolepayPaymentService extends BasePayment {


    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    private static final MyLog _log = MyLog.getLog(BolepayPaymentService.class);

    @Override
    public String getChannelName() {
        return BoleConfig.CHANNEL_NAME;
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
            case BoleConfig.CHANNEL_NAME_WXPAY_QR:
                retObj = doPayQrReq(payOrder, "902");
                break;
            case BoleConfig.CHANNEL_NAME_ALIPAY_QR:
                retObj = doPayQrReq(payOrder, "903");
                break;
            default:
                retObj = buildRetObj(PayConstant.RETURN_VALUE_FAIL, "不支持的渠道[channelId=" + channelId + "]");
                break;
        }
        return retObj;
    }

    /**
     * 查询订单
     *
     * @param payOrder
     * @return
     */
    @Override
    public JSONObject query(PayOrder payOrder) {

        String logPrefix = "【个付通订单查询】";
        String payOrderId = payOrder.getPayOrderId();

        _log.info("{}开始查询个付通订单,payOrderId={}", logPrefix, payOrderId);
        BoleConfig boleConfig = new BoleConfig(getPayParam(payOrder));
        JSONObject paramMap = new JSONObject();
        paramMap.put("pay_memberid", boleConfig.getAPP_ID());
        paramMap.put("pay_orderid", payOrderId);
        String _sign = DigestUtils.md5Hex("pay_memberid=" + "190809516" + "&pay_orderid=" + payOrderId + "&key="  + boleConfig.getAPP_KEY());
        paramMap.put("pay_md5sign", _sign);
        JSONObject retObj = buildRetObj();
        retObj.put("status", 1);    // 支付中
        Integer status = 0;
        Integer code = 0;

        try {
            String reqData = XXPayUtil.genUrlParams(paramMap);
            _log.info("{}请求数据:{}", logPrefix, reqData);
            String url = "http://www.79483.cn/Pay_Trade_query.html";
            String result = XXPayUtil.call4Post(url + "?" + reqData);
            _log.info("{}返回数据:{}", logPrefix, result);
            JSONObject resObj = JSONObject.parseObject(result);
            code = resObj.getInteger("code");
            status = resObj.getInteger("data");
            if (code == 200) {
                if (1 == status) {
                    retObj.put("status", 2);    // 成功
                } else if (4 == status) {
                    retObj.put("status", 1);    // 支付中
                }
            } else {
                retObj.put("message", result);
            }

        } catch (Exception e) {
            _log.error(e, "");
        }

        return retObj;
    }

    /**
     * @param payOrder
     * @return
     */
    public JSONObject doPayQrReq(PayOrder payOrder, String channel) {
        BoleConfig boleConfig = new BoleConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        JSONObject paramMap = new JSONObject();


        //-------------------
        String AuthorizationURL =boleConfig.getReqUrl();
//        String AuthorizationURL = "http://www.79483.cn/Pay_Index.html";
        String merchantId = boleConfig.getAPP_ID();
//        String keyValue = "k8jn6i0fxva92ytmhpt8kt5bzmttw9c0";
        String keyValue = boleConfig.getAPP_KEY();

        //--------------------
        try {
            String pay_memberid = merchantId;//商户id
            String pay_orderid = payOrder.getPayOrderId();//20位订单号 时间戳+6位随机字符串组成
            String pay_applydate = BolePayUtils.generateTime();//yyyy-MM-dd HH:mm:ss
            String pay_notifyurl = payConfig.getNotifyUrl(getChannelName());;//通知地址
            String pay_callbackurl = payConfig.getNotifyUrl(getChannelName());;//回调地址
            DecimalFormat df   = new DecimalFormat("######0.00");
            String pay_amount = df.format(payOrder.getAmount().doubleValue()/100);;
            String pay_attach = "";
            String pay_productname = "话费充值";
            String pay_productnum = "";
            String pay_productdesc = "";
            String pay_producturl = "";

            String stringSignTemp = "pay_amount=" + pay_amount + "&pay_applydate=" + pay_applydate + "&pay_bankcode=" + channel + "&pay_callbackurl=" + pay_callbackurl + "&pay_memberid=" + pay_memberid + "&pay_notifyurl=" + pay_notifyurl + "&pay_orderid=" + pay_orderid + "&key=" + keyValue + "";
            System.out.println("==="+stringSignTemp);
            String pay_md5sign = BolePayUtils.md5(stringSignTemp).toUpperCase();
            HashMap<String,String> param= new HashMap<>();
            param.put("pay_memberid",merchantId);
            param.put("pay_orderid",pay_orderid);
            param.put("pay_applydate",pay_applydate);
            param.put("pay_bankcode",channel);
            param.put("pay_notifyurl",pay_notifyurl);
            param.put("pay_callbackurl",pay_callbackurl);
            param.put("pay_amount",pay_amount);
            param.put("pay_productname",pay_productname);
            param.put("pay_productnum",pay_productnum);
            param.put("pay_producturl",pay_producturl);
            param.put("pay_productdesc",pay_productdesc);
            param.put("pay_md5sign",pay_md5sign);


            String reqData = XXPayUtil.genUrlParams(paramMap);


            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), payOrder.getPayOrderId(), updateCount);
            StringBuffer payForm = new StringBuffer();
            String toPayUrl = boleConfig.getReqUrl();
            payForm.append("<form style=\"display: none\" action=\"" + toPayUrl + "\" method=\"post\">");
            payForm.append("<input name=\"pay_memberid\" value=\"" + pay_memberid + "\" >");
            payForm.append("<input name=\"pay_orderid\" value=\"" + pay_orderid + "\" >");
            payForm.append("<input name=\"pay_applydate\" value=\"" + pay_applydate + "\" >");
            payForm.append("<input name=\"pay_bankcode\" value=\"" + channel + "\" >");
            payForm.append("<input name=\"pay_notifyurl\" value=\"" + pay_notifyurl + "\" >");
            payForm.append("<input name=\"pay_callbackurl\" value=\"" + pay_callbackurl + "\" >");
            payForm.append("<input name=\"pay_amount\" value=\"" + pay_amount + "\" >");
            payForm.append("<input name=\"pay_productname\" value=\"" + pay_productname + "\" >");
            payForm.append("<input name=\"pay_productnum\" value=\"" + pay_productnum + "\" >");
            payForm.append("<input name=\"pay_producturl\" value=\"" + pay_producturl + "\" >");
            payForm.append("<input name=\"pay_productdesc\" value=\"" + pay_productdesc + "\" >");
            payForm.append("<input name=\"pay_md5sign\" value=\"" + pay_md5sign + "\" >");
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


    public static void main(String[] agrs) throws NoSuchAlgorithmException {
        String AuthorizationURL = "http://www.79483.cn/Pay_Index.html";
        String merchantId = "190809516";
        String keyValue = "k8jn6i0fxva92ytmhpt8kt5bzmttw9c0";
        String Channelid = "wx";
        String Moneys = "100";  //金额
        String pay_bankcode = null;
        if (Channelid.equals("wx")) {
            pay_bankcode = "902";   //'银行编码
        } else if (Channelid.equals("zfb")) {
            pay_bankcode = "903";   //'银行编码
        }
        String pay_memberid = merchantId;//商户id
        String pay_orderid = BolePayUtils.generateOrderId();//20位订单号 时间戳+6位随机字符串组成
        String pay_applydate = BolePayUtils.generateTime();//yyyy-MM-dd HH:mm:ss
        String pay_notifyurl = "www.baidu.com";//通知地址
        String pay_callbackurl = "www.baidu.com";//回调地址
        String pay_amount = Moneys;
        String pay_attach = "";
        String pay_productname = "100元话费充值";
        String pay_productnum = "";
        String pay_productdesc = "";
        String pay_producturl = "";
        String stringSignTemp = "pay_amount=" + pay_amount + "&pay_applydate=" + pay_applydate + "&pay_bankcode=" + pay_bankcode + "&pay_callbackurl=" + pay_callbackurl + "&pay_memberid=" + pay_memberid + "&pay_notifyurl=" + pay_notifyurl + "&pay_orderid=" + pay_orderid + "&key=" + keyValue + "";
        System.out.println(stringSignTemp+"-----");
        String pay_md5sign = BolePayUtils.md5(stringSignTemp);
        HashMap<String,String> param= new HashMap<>();
        param.put("pay_memberid",merchantId);
        param.put("pay_orderid",pay_orderid);
        param.put("pay_applydate",pay_applydate);
        param.put("pay_bankcode",pay_bankcode);
        param.put("pay_notifyurl",pay_notifyurl);
        param.put("pay_callbackurl",pay_callbackurl);
        param.put("pay_amount",Moneys);
        param.put("pay_productname",pay_productname);
        param.put("pay_productnum",pay_productnum);
        param.put("pay_producturl",pay_producturl);
        param.put("pay_productdesc",pay_productdesc);
        param.put("pay_md5sign",pay_md5sign);
        System.out.println(param.toString());
        HttpClientUtils.post(AuthorizationURL,param);

    }


}
