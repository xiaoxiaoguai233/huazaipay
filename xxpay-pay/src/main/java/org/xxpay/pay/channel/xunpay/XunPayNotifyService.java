package org.xxpay.pay.channel.xunpay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.sl.usermodel.FreeformShape;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.channel.BasePayNotify;
import org.xxpay.pay.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/12/24
 * @description: 讯付回调
 */
@Service
public class XunPayNotifyService extends BasePayNotify {

    private static final MyLog _log = MyLog.getLog(XunPayNotifyService.class);

    @Override
    public String getChannelName() {
        return XunPayConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject doNotify(Object notifyData) {
        String logPrefix = "【处理讯付回调】";
        _log.info("====== 开始处理讯付回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
        JSONObject retObj = buildRetObj();
        String retJsonStr = Util.JsonReq(request);
        JSONObject retJson = JSONObject.parseObject(retJsonStr);
        Map<String, Object> payContext = new HashMap<String, Object>();
        PayOrder payOrder;
        try {

            _log.info("==============返回参数：" + JSONObject.toJSONString(retJson));

            payContext.put("parameters", retJson);

            if (!verifyPayParams(payContext)) {
                retObj.put(PayConstant.RESPONSE_RESULT, XunPayConfig.RETURN_VALUE_FAIL);
                return retObj;
            }

            payOrder = (PayOrder) payContext.get("payOrder");
            byte payStatus = payOrder.getStatus(); // -1：支付失败， 0：订单生成，1：支付中，2：支付成功，3：业务处理完成，-2：订单过期
            if (payStatus != PayConstant.PAY_STATUS_SUCCESS && payStatus != PayConstant.PAY_STATUS_COMPLETE) {
                int updatePayOrderRows = rpcCommonService.rpcPayOrderService
                        .updateStatus4Success(payOrder.getPayOrderId());
                if (updatePayOrderRows != 1) {
                    _log.error("{}更讯付状态失败,将payOrderId={},更新payStatus={}失败", logPrefix, payOrder.getPayOrderId(),
                            PayConstant.PAY_STATUS_SUCCESS);
                    retObj.put(PayConstant.RESPONSE_RESULT, "处理订单失败");
                    return retObj;
                }
                _log.info("{}更讯付状态成功,将payOrderId={},更新payStatus={}成功", logPrefix, payOrder.getPayOrderId(),
                        PayConstant.PAY_STATUS_SUCCESS);
                payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
            }
            retObj.put(PayConstant.RESPONSE_RESULT, XunPayConfig.RETURN_VALUE_SUCCESS);
            // 业务系统后端通知
            baseNotify4MchPay.doNotify(payOrder, true);
            _log.info("====== 完成处理讯付回调通知 ======");
        } catch (Exception e) {
            retObj.put(PayConstant.RESPONSE_RESULT, XunPayConfig.RETURN_VALUE_FAIL);
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
        String msg = params.getString("msg"); // 签名
        String ordernum = params.getString("ordernum"); // 订单号
        String respcd = params.getString("respcd"); // 签名
        String transTime = params.getString("transTime"); // 签名
        String xfmcode = params.getString("xfmcode"); // 签名
        String sign = params.getString("sign"); // 签名

        // 查询payOrder记录
        String payOrderId = ordernum;
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
        String data = msg + ordernum + respcd + transTime + xfmcode + app_secret;


        _log.info("=======" + data);
        if(!"00".equals(respcd))
        {
            return false;
        }
        String _sign = DigestUtils.md5Hex(data).toLowerCase();
        _log.info("我方签名" + _sign);
        _log.info("上游签名" + sign);
        if (!_sign.equals(sign)) {
            payContext.put("retMsg", "验签失败");
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
