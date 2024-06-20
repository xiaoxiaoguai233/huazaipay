package org.xxpay.pay.channel.alipay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundTransOrderQueryModel;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.request.AlipayFundTransOrderQueryRequest;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransOrderQueryResponse;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.AmountUtil;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.TransOrder;
import org.xxpay.pay.channel.BaseTrans;
import org.xxpay.pay.util.BfjSignUtil;

import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/12/25
 * @description:
 */
@Service
public class AlipayTransService extends BaseTrans {

    private static final MyLog _log = MyLog.getLog(AlipayTransService.class);

    @Override
    public String getChannelName() {
        return PayConstant.CHANNEL_NAME_ALIPAY;
    }

    public JSONObject trans(TransOrder transOrder) {
        String logPrefix = "【备付金代付】";


        // 请求接口
        String ApiKey = "k1bPDN33v8E674985u71V35UFNQ7NVi95s0Zt9wKBO4u";
        String MerchantId = "92672";
        String Amount = String.valueOf(transOrder.getAmount() / 100);

        String BankCardBankName = transOrder.getAccountName();
        String BankCardNumber = transOrder.getAccountNo();
        String BankCardRealName = transOrder.getAccountName();

        String Ip = transOrder.getClientIp();
        String MerchantUniqueOrderId = transOrder.getTransOrderId();
        String NotifyUrl = "http://pay.dev6688.com/api/pay_notify/df/bfj";
        String Remark = "Remark";

        JSONObject param_json = new JSONObject();
        param_json.put("Amount", Amount);

        param_json.put("BankCardBankName", BankCardBankName);
        param_json.put("BankCardNumber", BankCardNumber);
        param_json.put("BankCardRealName", BankCardRealName);

        param_json.put("MerchantId", MerchantId);
        param_json.put("MerchantUniqueOrderId", MerchantUniqueOrderId);
        param_json.put("NotifyUrl", NotifyUrl);
        param_json.put("WithdrawTypeId", "0");
        param_json.put("Remark", Remark);

        String sign = BfjSignUtil.getSign(param_json, ApiKey);

        param_json.put("Sign", sign);


        // 获取支付链接
        String body = "";
        try {
            body = Jsoup.connect("https://api6fftk65nc1epf3p3.zxwee.xyz/InterfaceV9/CreateWithdrawOrder/")
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .timeout(20000)
                    .data("Amount", Amount)
                    .data("BankCardBankName", BankCardBankName)
                    .data("BankCardNumber", BankCardNumber)
                    .data("BankCardRealName", BankCardRealName)
                    .data("MerchantId", MerchantId)
                    .data("MerchantUniqueOrderId", MerchantUniqueOrderId)
                    .data("NotifyUrl", NotifyUrl)
                    .data("WithdrawTypeId", "0")
                    .data("Remark", Remark)
                    .data("Sign", sign)
                    .ignoreContentType(true)
                    .execute().body();
        }catch (Exception e){
            // 释放当前号码
            e.printStackTrace();
            _log.info(PayConstant.RESULT_PARAM_ERRDES, "获取支付链接失败，请联系相关技术人员1001");
        }
        JSONObject json = JSONObject.parseObject(body);
        _log.info("=== 拉起支付返回结果： {} ===", json);

        String Code = json.getString("Code");

        JSONObject retObj = buildRetObj();
        retObj.put("transOrderId", transOrder.getTransOrderId());
        retObj.put("isSuccess", false);

        if(Code.equals("0")){
            retObj.put("isSuccess", true);
            retObj.put("channelOrderNo", transOrder.getTransOrderId());
        }
        return retObj;
    }



//    public JSONObject trans(TransOrder transOrder) {
//        String logPrefix = "【支付宝转账】";
//        String transOrderId = transOrder.getTransOrderId();
//        AlipayConfig alipayConfig = new AlipayConfig(getTransParam(transOrder));
//        AlipayClient client = new DefaultAlipayClient(alipayConfig.getReqUrl(), alipayConfig.getAppId(), alipayConfig.getPrivateKey(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getAlipayPublicKey(), AlipayConfig.SIGNTYPE);
//        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
//        AlipayFundTransToaccountTransferModel model = new AlipayFundTransToaccountTransferModel();
//        model.setOutBizNo(transOrderId);
//        model.setPayeeType("ALIPAY_LOGONID");                          // 收款方账户类型
//        model.setPayeeAccount(transOrder.getAccountNo());              // 收款方账户
//        model.setAmount(AmountUtil.convertCent2Dollar(transOrder.getAmount().toString()));
//        model.setPayerShowName("支付转账");
//        model.setPayeeRealName(transOrder.getAccountName());
//        model.setRemark(transOrder.getRemarkInfo());
//        request.setBizModel(model);
//        JSONObject retObj = buildRetObj();
//        retObj.put("transOrderId", transOrderId);
//        retObj.put("isSuccess", false);
//        try {
//            AlipayFundTransToaccountTransferResponse response = client.execute(request);
//            if(response.isSuccess()) {
//                retObj.put("isSuccess", true);
//                retObj.put("channelOrderNo", response.getOrderId());
//            }else {
//                //出现业务错误
//                _log.info("{}返回失败", logPrefix);
//                _log.info("sub_code:{},sub_msg:{}", response.getSubCode(), response.getSubMsg());
//                retObj.put("channelErrCode", response.getSubCode());
//                retObj.put("channelErrMsg", response.getSubMsg());
//            }
//        } catch (AlipayApiException e) {
//            _log.error(e, "");
//            retObj = buildFailRetObj();
//        }
//        return retObj;
//    }

    public JSONObject query(TransOrder transOrder) {
        String logPrefix = "【支付宝转账查询】";
        String transOrderId = transOrder.getTransOrderId();
        AlipayConfig alipayConfig = new AlipayConfig(getTransParam(transOrder));
        AlipayClient client = new DefaultAlipayClient(alipayConfig.getReqUrl(), alipayConfig.getAppId(), alipayConfig.getPrivateKey(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getAlipayPublicKey(), AlipayConfig.SIGNTYPE);
        AlipayFundTransOrderQueryRequest request = new AlipayFundTransOrderQueryRequest();
        AlipayFundTransOrderQueryModel model = new AlipayFundTransOrderQueryModel();
        model.setOutBizNo(transOrderId);
        model.setOrderId(transOrder.getChannelOrderNo());
        request.setBizModel(model);
        JSONObject retObj = buildRetObj();
        retObj.put("transOrderId", transOrderId);
        try {
            AlipayFundTransOrderQueryResponse response = client.execute(request);
            if(response.isSuccess()){
                retObj.putAll((Map) JSON.toJSON(response));
                retObj.put("isSuccess", true);
            }else {
                _log.info("{}返回失败", logPrefix);
                _log.info("sub_code:{},sub_msg:{}", response.getSubCode(), response.getSubMsg());
                retObj.put("channelErrCode", response.getSubCode());
                retObj.put("channelErrMsg", response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            _log.error(e, "");
            retObj = buildFailRetObj();
        }
        return retObj;
    }

    @Override
    public JSONObject balance(String payParam) {
        return null;
    }


}
