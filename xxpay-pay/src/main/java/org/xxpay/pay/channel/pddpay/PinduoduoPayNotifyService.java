package org.xxpay.pay.channel.pddpay;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MD5Util;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayNotify;
import org.xxpay.pay.channel.gepay.GepayConfig;
import org.xxpay.pay.channel.ltpay.utils.LtUtils;
import org.xxpay.pay.channel.ssfpay.utils.SsfpayUtils;
import org.xxpay.pay.util.HttpClientUtils;
import org.xxpay.pay.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author: carter
 * @date: 2019-07-31
 * @description: 拼多多回调
 */
@Service
public class PinduoduoPayNotifyService extends BasePayNotify {

    private static final MyLog _log = MyLog.getLog(PinduoduoPayNotifyService.class);

    @Override
    public String getChannelName() {
        return PinDuoDuoPayConfig.CHANNEL_NAME;
    }



    @Override
    public JSONObject doNotify(Object notifyData) {
        String logPrefix = "【处理拼多多回调】";
        _log.info("====== 开始处理拼多多回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
//        String retJsonStr = Util.JsonReq(request);//返回的json
        TreeMap<String,String> params= new TreeMap<>();
        JSONObject retObj = buildRetObj();
        Map<String, Object> payContext = new TreeMap<>();
        PayOrder payOrder;
        String respString = PinDuoDuoPayConfig.RETURN_VALUE_FAIL;

        //从request获取参数，并转换为jsonObject对象
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String s : parameterMap.keySet()) {
            params.put(s, parameterMap.get(s)[0]);
        }
        try {//这里又把object put到payContext对象中去
            _log.info(JSONObject.toJSONString(params)+"------------");
            payContext.put("parameters", params);
            //验证拼多多通知参数，如果返回false，在这里就返回报错信息
            if (!verifyPayParams(payContext)) {
                retObj.put(PayConstant.RESPONSE_RESULT, GepayConfig.RETURN_VALUE_FAIL);
                return retObj;
            }
            //刚才验证参数的时候就更新了三方订单，现在获取该订单
            payOrder = (PayOrder) payContext.get("payOrder");

            byte payStatus = payOrder.getStatus(); //-1：支付失败，  0：订单生成，1：支付中，2：支付成功，3：业务处理完成，-2：订单过期
            //判断单号状态，如果不为成功状态，且不为完成状态。就在这里更新状态为成功。
            if (payStatus != PayConstant.PAY_STATUS_SUCCESS && payStatus != PayConstant.PAY_STATUS_COMPLETE) {
                //更新数据库的订单状态为成功
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
            _log.info("====== 完成处理拼多多回调通知 ======");
            respString = PinDuoDuoPayConfig.RETURN_VALUE_SUCCESS;


        } catch (Exception e) {
            _log.error(e, logPrefix + "处理异常");
        }
        retObj.put(PayConstant.RESPONSE_RESULT, respString);
        return retObj;

    }

    /**
     * 验证拼多多通知参数
     *
     * @return
     */
    public boolean verifyPayParams(Map<String, Object> payContext) {
        //JSONObject params = (JSONObject) payContext.get("parameters");
        TreeMap<String,String> params = (TreeMap<String,String>)payContext.get("parameters");
        HashMap<String, String> signParam = new HashMap<String, String>();

        //传进来的订单中获取我们需要的参数
        Object callbacks = params.get("callbacks");
        Object sign = params.get("sign");
        String out_trade_no = params.get("out_trade_no");


        //校验结果是否成功
        if(!"CODE_SUCCESS".equals(callbacks)) {
            _log.error("返回状态值错误. callbacks={}, ", callbacks);
            payContext.put("retMsg", "notify data failed");
            return false;
        }

        // 查询payOrder记录,如果为空返回错误信息
        String payOrderId = out_trade_no.toString();
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
        if (payOrder == null) {
            _log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "Can't found payOrder");
            return false;
        }

        //用通道id查询通道信息，然后获取到key的值
        PayPassageAccount payPassageAccount = rpcCommonService.rpcPayPassageAccountService.findById(payOrder.getPassageAccountId());
        JSONObject object = JSONObject.parseObject(payPassageAccount.getParam());
        String APP_KEY = object.getString("APP_KEY");
        System.out.println("app_key:"+APP_KEY);
        //准备所有文档要求的字段，我们自己用md5加密验算一遍sign值。

        StringBuilder stringBuilder = new StringBuilder();
        params.remove("sign");
        for(String s:params.keySet())
        {
            stringBuilder.append(s).append("=").append(params.get(s)).append("&");
        }
        stringBuilder.append("key="+APP_KEY);
        String validSign="";
        //算sign值
        validSign = MD5Util.string2MD5(stringBuilder.toString()).toUpperCase();
        System.out.println("validSign:"+validSign);
        //如果他们返回的sign值跟我们算出来的不一致，就说明这个数据有问题，返回失败
        if (!validSign.equals(sign)){
            payContext.put("retMsg", "验证失败,签名不一致");
            return false;
        }

        //核对订单金额跟上游返回的金额是否一致，不一致直接返回报错
        String amount = params.get("amount");
        long outPayAmt = new BigDecimal(amount).longValue();
        long dbPayAmt = payOrder.getAmount().longValue()/100l;
        if (dbPayAmt != outPayAmt) {
            _log.error("金额不一致. outPayAmt={},payOrderId={}", amount.toString(), payOrderId);
            payContext.put("retMsg", "金额不一致");
            return false;
        }

        //当所有的校验条件都通过后，更新三方单号到payContext对象中，返回给上级
        payOrder.setChannelOrderNo(payOrderId);
        payContext.put("payOrder", payOrder);
        return true;
    }



//
//    public static void main(String[] agrs) throws NoSuchAlgorithmException {
//        String AuthorizationURL = "http://localhost:3020/notify/PingDuoDuo/notify_res.htm";
//
//        StringBuffer sb = new StringBuffer();
//        sb.append("callbacks=" + "CODE_SUCCESS");
//        sb.append("&appid=" + "20190916642956");
//        sb.append("&pay_type=" + "129");
//        sb.append("&success_url=" + "http://www.baidu.com/");
//        sb.append("&error_url=" + "http://www.baidu.com/");
//        sb.append("&out_trade_no=" + "P01201907271606153870001");
//        sb.append("&amount=" + "1000");//交易金额
//        sb.append("&amount_true=" +"1000");
//        sb.append("&key=" + "pjr9cjpfnm23vc1aanpw4m00rwo2jrag");
//        String pay_md5sign = LtUtils.md5(sb.toString()).toUpperCase();
//        System.out.println( "----------------------------: "+sb.toString());
//        System.out.println( "----------------------------: "+pay_md5sign);
//
//        HashMap<String,String> mav= new HashMap<>();
//        mav.put("callbacks", "CODE_SUCCESS");
//        mav.put("appid" , "20190916642956");
//        mav.put("pay_type", "129");
//        mav.put("success_url", "http://www.baidu.com/");
//        mav.put("error_url", "http://www.baidu.com/");
//        mav.put("out_trade_no",  "P01201907271606153870001");
//        mav.put("amount", "1000");
//        mav.put("amount_true", "1000");
//        mav.put("sign", pay_md5sign);
//        System.out.println(mav.toString());
//        HttpClientUtils.post(AuthorizationURL,mav);
//
//
//    }

}
