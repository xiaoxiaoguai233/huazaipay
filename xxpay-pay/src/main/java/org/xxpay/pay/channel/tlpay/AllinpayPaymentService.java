package org.xxpay.pay.channel.tlpay;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.XXPayUtil;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.pay.channel.BasePayment;
import org.xxpay.pay.util.Util;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

@Service
public class AllinpayPaymentService extends BasePayment {
    private static final MyLog _log = MyLog.getLog(AllinpayPaymentService.class);

    @Override
    public String getChannelName() {
        return AllinpayConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject pay(PayOrder payOrder) {
        String channelId = payOrder.getChannelId();
        JSONObject retObj;
        switch (channelId) {
            case AllinpayConfig.CHANNEL_NAME_WXPAY_QR:
                retObj = doPayQrReq(payOrder, "W01");
                break;
            case AllinpayConfig.CHANNEL_NAME_ALIPAY_QR:
                retObj = doPayQrReq(payOrder, "A01");
                break;
            case AllinpayConfig.CHANNEL_NAME_QQPAY_QR:
                retObj = doPayQrReq(payOrder, "Q01");
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
    public JSONObject doPayQrReq(PayOrder payOrder, String paytype) {
        AllinpayConfig allinpayConfig = new AllinpayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        String cusid = allinpayConfig.getCUS_ID();
        String appid = allinpayConfig.getAPP_ID();
        String keyValue = allinpayConfig.getAPP_KEY();
        TreeMap<String,Object> paramsMap = new TreeMap<String,Object>();
        try {
            Random random = new Random();
            int randomemp = Math.abs(random.nextInt());
            String randomstr = String.valueOf(randomemp);
            String trxamt = String.valueOf(payOrder.getAmount()); // 付款金额
            String reqsn = payOrder.getPayOrderId(); // 20位订单号 时间戳+6位随机字符串组成
            String notify_url = payConfig.getNotifyUrl(getChannelName());  // 通知地址
            String returl = payConfig.getReturnUrl(getChannelName());  // 回调地址
            paramsMap.put("cusid", cusid);
            paramsMap.put("appid", appid);
            paramsMap.put("version", "12");
            paramsMap.put("trxamt", trxamt);
            paramsMap.put("reqsn", reqsn);
            paramsMap.put("randomstr", randomstr);
            paramsMap.put("body", "tongdapay");
            paramsMap.put("notify_url",notify_url);
            paramsMap.put("returl",returl);
            paramsMap.put("key",keyValue);
            String signTemp = this.doSignString(paramsMap);
            String pay_md5sign = Util.md5(signTemp);
            _log.info("加密后：{}",pay_md5sign);
            paramsMap.put("toPayUrl", allinpayConfig.getReqUrl());
            paramsMap.put("sign", pay_md5sign);
            _log.info("请求上游通道通联支付参数{}",paramsMap);
            String param = XXPayUtil.genUrlParams(paramsMap);
            String jumpurl = payConfig.getPayUrl() + "/channel/tlpay?" + param;
            String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
            String qrfileName = System.currentTimeMillis() + ".png";
            String qrcode = "static/qrcode/" + qrfileName;
            String imgPath = path + qrcode;
            encoderQRCode(jumpurl, imgPath);
            int updateCount = rpcCommonService.rpcPayOrderService.updateStatus4Ing(payOrder.getPayOrderId(), payOrder.getPayOrderId());
            _log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), payOrder.getPayOrderId(), updateCount);
            // 支付链接地址
            retObj.put("payOrderId", payOrder.getPayOrderId()); // 设置支付订单ID
            JSONObject payParams = new JSONObject();
            payParams.put("codeImgUrl", payConfig.getPayUrl().replace("/api","")+"/qrcode/"+qrfileName);
            payParams.put("payMethod", PayConstant.PAY_METHOD_CODE_IMG);
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
                strTemp.append(key + "=" + map.get(key).toString() + "&");
        }
        String str = strTemp.toString();
        return str.substring(0,str.length()-1);
    }


    public static void main(String[] args) throws Exception {
    }

    /**
     * 生成二维码(QRCode)图片
     * @param content
     * @param imgPath
     */
    public void encoderQRCode(String content, String imgPath) {
        int width=300;
        int height=300;

        String format="png";
        //这里如果你想自动跳转的话，需要加上https://

        HashMap hits=new HashMap();
        hits.put(EncodeHintType.CHARACTER_SET, "utf-8");//编码
        //纠错等级，纠错等级越高存储信息越少
        hits.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        //边距
        hits.put(EncodeHintType.MARGIN, 2);

        try {
            BitMatrix bitMatrix=new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height,hits);
            Path path=new File(imgPath).toPath();
            MatrixToImageWriter.writeToPath(bitMatrix, format, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
