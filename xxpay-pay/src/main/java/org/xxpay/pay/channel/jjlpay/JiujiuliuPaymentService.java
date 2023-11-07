package org.xxpay.pay.channel.jjlpay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.XXPayUtil;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.channel.ssfpay.utils.SsfpayUtils;
import org.xxpay.pay.util.HttpClientUtils;
import org.xxpay.pay.util.Util;

import java.text.DecimalFormat;
import java.util.*;

@Service
public class JiujiuliuPaymentService extends BasePayment {
    private static final MyLog _log = MyLog.getLog(JiujiuliuPaymentService.class);

    @Override
    public String getChannelName() {
        return JiujiuliuConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject pay(PayOrder payOrder) {
        String channelId = payOrder.getChannelId();
        JSONObject retObj;
        switch (channelId) {
            case JiujiuliuConfig.CHANNEL_NAME_WXPAY_QR:
                retObj = doPayQrReq(payOrder, 408);
                break;
            case JiujiuliuConfig.CHANNEL_NAME_ALIPAY_QR:
                retObj = doPayQrReq(payOrder, 409);
                break;
            case JiujiuliuConfig.CHANNEL_NAME_ALIPAY_ZZQR:
                retObj = doPayQrReq(payOrder, 411);
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
    public JSONObject doPayQrReq(PayOrder payOrder, Integer channel) {
        JiujiuliuConfig jiujiuliuConfig = new JiujiuliuConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        String AuthorizationURL = jiujiuliuConfig.getReqUrl();  // "http://tpay.szspzc.xyz/api/pay/gopay"
        String merchantId = jiujiuliuConfig.getAPP_ID();
        String keyValue = jiujiuliuConfig.getAPP_KEY();
        //--------------------
        try {
            Integer mchid = Integer.parseInt(merchantId);
            String timestamp = String.valueOf(new Date().getTime() / 1000);
            Random random = new Random();
            int nonceTemp = Math.abs(random.nextInt());
            String nonce = String.valueOf(nonceTemp);

            Integer paytype = channel; // 支付类型
            String out_trade_no = payOrder.getPayOrderId(); // 20位订单号 时间戳+6位随机字符串组成
            String goodsname = "tongda"; // 商品名称
            DecimalFormat df = new DecimalFormat("######0.00");
            String total_fee = df.format(payOrder.getAmount().doubleValue() / 100);
            String notify_url = payConfig.getNotifyUrl(getChannelName());  // 通知地址
            String return_url = payConfig.getReturnUrl(getChannelName());  // 回调地址
            String remark = "null".equals(String.valueOf(payOrder.getSubMchId())) ? "" : String.valueOf(payOrder.getSubMchId());  // 	订单说明
            String requestip = payOrder.getClientIp();   // 	终端用户发起请求IP
            TreeMap<String, Object> map = new TreeMap<String, Object>();
            map.put("mchid", mchid);
            map.put("timestamp", timestamp);
            map.put("nonce", nonce);
            map.put("paytype", paytype);
            map.put("out_trade_no", out_trade_no);
            map.put("goodsname", goodsname);
            map.put("total_fee", total_fee);
            map.put("notify_url", notify_url);
            map.put("return_url", return_url);
            map.put("remark", remark);
            map.put("requestip", requestip);

            String signTemp = this.doSignString(map) + "key=" + keyValue;
            _log.info("加密前字符串：{}",signTemp);
            String pay_md5sign = Util.md5(signTemp).toLowerCase();
            _log.info("加密后：{}",pay_md5sign);
            JSONObject data = new JSONObject();
            JSONObject param = new JSONObject();
            data.put("paytype", paytype);
            data.put("out_trade_no", out_trade_no);
            data.put("goodsname", goodsname);
            data.put("total_fee", total_fee);
            data.put("notify_url", notify_url);
            data.put("return_url", return_url);
            data.put("requestip", requestip);
            param.put("mchid", mchid);
            param.put("timestamp", timestamp);
            param.put("nonce", nonce);
            param.put("sign", pay_md5sign);
            param.put("data", data);
            String reqUrl = jiujiuliuConfig.getReqUrl();
            _log.info("请求上游通道996参数{}",param.toJSONString());
            String result = HttpClientUtils.postToJSON(reqUrl, param.toJSONString());
            JSONObject res = JSONObject.parseObject(result);
            if (!"0".equals(res.get("error").toString())) {
                _log.error(res.get("msg").toString(), "");
                retObj.put("errDes", "上游返回失败!");
                retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
                return retObj;
            }
            JSONObject resData = JSONObject.parseObject(res.get("data").toString());
            String toPayUrl = resData.get("payurl").toString();

            String reqData = JSONObject.toJSONString(param);
            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), payOrder.getPayOrderId(), updateCount);
            StringBuffer payForm = new StringBuffer();

            payForm.append("<form style=\"display: none\" action=\"" + toPayUrl + "\" method=\"post\"></form>");
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


    private static String doSignString(TreeMap<String, Object> map) {
        Set keySet = map.keySet();
        Iterator it = keySet.iterator();
        StringBuilder strTemp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next().toString();
            if (StringUtils.isNotBlank(String.valueOf(map.get(key))))
                strTemp.append(key + "=" + map.get(key) + "&");
        }
        return strTemp.toString();
    }


    public static void main(String[] args) throws Exception {
        String AuthorizationURL = "http://tpay.szspzc.xyz/api/pay/gopay";

        String keyValue = "y4b7oo8e67t9972qx121yds9zarwh4w6";

        Integer mchid = 20346;
        String timestamp = String.valueOf(new Date().getTime() / 1000);
        Random random = new Random();
        int nonceTemp = Math.abs(random.nextInt());
        String nonce = String.valueOf(nonceTemp);

        Integer paytype = 408; // 支付类型
        String out_trade_no = SsfpayUtils.generateOrderId(); // 20位订单号 时间戳+6位随机字符串组成
        String goodsname = "aaaaa"; // 商品名称
        DecimalFormat df = new DecimalFormat("######0.00");
        String total_fee = df.format(20000 / 100);
        String notify_url = "www.baidu.com";  // 通知地址
        String return_url = "www.baidu.com";  // 回调地址
        String remark = "11";  // 	订单说明
        String requestip = "14.155.18.58";   // 	终端用户发起请求IP
        TreeMap<String, Object> map = new TreeMap<String, Object>();
        map.put("mchid", mchid);
        map.put("timestamp", timestamp);
        map.put("nonce", nonce);
        map.put("paytype", paytype);
        map.put("out_trade_no", out_trade_no);
        map.put("goodsname", goodsname);
        map.put("total_fee", total_fee);
        map.put("notify_url", notify_url);
        map.put("return_url", return_url);
        map.put("remark", remark);
        map.put("requestip", requestip);
        String signTemp = doSignString(map) + "key=" + keyValue;
        String pay_md5sign = Util.md5(signTemp).toLowerCase();
//        JSONObject data = new JSONObject();
//        JSONObject param = new JSONObject();
        TreeMap<String,Object> data = new TreeMap<>();
        TreeMap<String,Object> param = new TreeMap<>();
        data.put("paytype", paytype);
        data.put("out_trade_no", out_trade_no);
        data.put("goodsname", goodsname);
        data.put("total_fee", total_fee);
        data.put("notify_url", notify_url);
        data.put("return_url", return_url);
        data.put("remark", remark);
        data.put("requestip", requestip);

        param.put("mchid", mchid);
        param.put("timestamp", timestamp);
        param.put("nonce", nonce);
        param.put("sign", pay_md5sign);
        param.put("data", data);

        System.out.println(signTemp + "-----");

        System.out.println(param.toString());
//        String post = HttpClientUtils.post(AuthorizationURL, param);
        String post = HttpClientUtils.postToJSON(AuthorizationURL, JSONObject.toJSONString(param));




        System.out.println(post);

    }


}
