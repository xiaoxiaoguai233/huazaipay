package org.paydemo;

import com.alibaba.fastjson.JSONObject;
import org.paydemo.utils.HttpUtil;
import org.paydemo.utils.SignUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class main {

    public static String  mchId = "20000598";
    public static String appId = "1a7b280a76cf4af6a9e964f878b8db38";
    public static String mchKey = "WDZ3JSCUUR7T6M1HBVS4HWACUWEORDJFXXQ0MD7YRX8LTTTDG4TLXWAXM4ONFPDZQZRTIZAX3PBCBEJA4ZLHTIDXSM4TLKSZ1KOM2NCY5MFKZ6MQRQ0CISRMGXFCCMQ1";
    public static String payHost = "https://pay.slypay.top/api/pay/create_order";

    public static void main(String[] args) throws IOException {



        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mchId", mchId + "");  //商户ID
        params.put("appId", appId);  // 当调用微信小程序支付时,该appId必传，为小程序的appId
        params.put("productId", "8063");  //支付产品ID
        params.put("mchOrderNo", "P01202305292003589320000");   //商户订单号
        params.put("currency", "cny");              //币种
        params.put("amount", "10000");              //支付金额
        params.put("clientIp", "210.73.10.148");    //客户端IP
        params.put("device", "ios10.3.1");          //客户端设备
        params.put("returnUrl", "https://merchant.slypay.top/api/paydemo/return.htm");   //支付结果前端跳转URL
        params.put("notifyUrl", "https://merchant.slypay.top/api/paydemo/notify.htm");   //支付结果后台回调URL
        params.put("subject", "网络购物");      //商品主题
        params.put("body", "网络购物");             //商品描述信息
        params.put("param1", "");               //扩展参数1
        params.put("param2", "");               //扩展参数2
        params.put("channelUserId", "channelUserId");   //渠道用户ID,小程序支付时传openId
        params.put("extra", "extra");  //附加参数

        String sign = SignUtil.getSign(params, mchKey);  //	签名
//		String sign = PayDigestUtil.getSign(params, mchKey);  // 签名
        params.put("sign", sign);

        System.out.println(JSONObject.toJSONString(params));

        String post = HttpUtil.post(payHost, genUrlParams(params));

        System.out.println(post);

    }


    /** map 转换为  url参数 **/
    private static String genUrlParams(Map<String, Object> paraMap) {
        if(paraMap == null || paraMap.isEmpty()) return "";
        StringBuffer urlParam = new StringBuffer();
        Set<String> keySet = paraMap.keySet();
        int i = 0;
        for(String key:keySet) {
            urlParam.append(key).append("=").append(paraMap.get(key));
            if(++i == keySet.size()) break;
            urlParam.append("&");
        }
        return urlParam.toString();
    }
}
