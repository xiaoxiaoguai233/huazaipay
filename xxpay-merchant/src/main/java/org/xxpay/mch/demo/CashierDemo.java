package org.xxpay.mch.demo;

import com.alibaba.fastjson.JSONObject;
import org.xxpay.core.common.util.PayDigestUtil;
import org.xxpay.core.common.util.XXPayUtil;

import java.util.HashMap;

public class CashierDemo {
    public static void main(String[] args) {

//        HashMap<String, Object> param = new HashMap<>();
//        param.put("appId", "7ca36fb15e8943b79d098ce8a36aec0a");
//        param.put("mchId", "20000000");
//        param.put("subMchId", "0001");
//        param.put("ctime", System.currentTimeMillis()/1000);
//        String sign = PayDigestUtil.getSign(param, "D4SZ8TQK1Z8UPYMOLSKQQPMWYKVXW8IAHBMNJEFXJLCYPF7AWKCTKN1SXWS82ZNPMOBRFEGCK5TOGOQKPC59LP0FHIP6TU5GZ5TZXHHJ7YDGHSWP2URHZX1YUKPUMPAM");
//        param.put("sign", sign);
//        String reqData = XXPayUtil.genUrlParams(param);
//        String url = "http://localhost:3020/api/cashier/common/build?" + reqData;
//        String url = "http://localhost:3020/api/cashier/common/build?" + reqData;

//        HashMap<String, Object> param = new HashMap<>();
//        param.put("appId", "46461bf3b82f4667bd8c28cce12cb51e");
//        param.put("mchId", "20000002");
//        param.put("subMchId", "0001");
//        param.put("ctime", System.currentTimeMillis()/1000);
//        String sign = PayDigestUtil.getSign(param, "GOB2DBFHDYOTUHXFWUGL5YHOW4X14WWRG8WNRB1RMLBP6DLSQMQGWJIYTUHXITFJHQSIEQ8Y7KEV6OHMJX7NB88QITYLAOCMUCTUMYZUDDKERM2IDJUTDUWIBGWNHLMO");
//        param.put("sign", sign);
//        String reqData = XXPayUtil.genUrlParams(param);
//        String url = "http://localhost:3020/api/cashier/common/build?" + reqData;
//        String url = "http://localhost:3020/api/cashier/common/build?" + reqData;

//        HashMap<String, Object> param = new HashMap<>();
//        param.put("appId", "113629740f924a749f6b24f5febc7f77");
//        param.put("mchId", "20000001");
//        param.put("subMchId", "111");
//        param.put("ctime", System.currentTimeMillis()/1000);
//        String sign = PayDigestUtil.getSign(param, "VCGCB4V0SUSAEF3PZ1JHAFAS4XPDZPZU8ZGCIOFIDTS3JQT4QXYFXVAWZUW63CFFRO4OMC1BUMIAIKJACEIVTZSH9Q4ZVK5EV6YHX28OPYKYPHCYHTSD0AJXQLWRYHFU");
//        param.put("sign", sign);
//        String reqData = XXPayUtil.genUrlParams(param);
//        String url = "http://pay.pay188.vip/api/cashier/common/build?" + reqData;
//
//
        HashMap<String, Object> param = new HashMap<>();
        param.put("appId", "113629740f924a749f6b24f5febc7f77");
        param.put("mchId", "20000001");
        param.put("subMchId", "111");
        param.put("ctime", System.currentTimeMillis()/1000);
        String sign = PayDigestUtil.getSign(param, "VCGCB4V0SUSAEF3PZ1JHAFAS4XPDZPZU8ZGCIOFIDTS3JQT4QXYFXVAWZUW63CFFRO4OMC1BUMIAIKJACEIVTZSH9Q4ZVK5EV6YHX28OPYKYPHCYHTSD0AJXQLWRYHFU");
        param.put("sign", sign);
        String reqData = XXPayUtil.genUrlParams(param);



//        HashMap<String, Object> param = new HashMap<>();
//        param.put("appId", "7ca36fb15e8943b79d098ce8a36aec0a");
//        param.put("mchId", "20000000");
//        param.put("subMchId", "222");
//        param.put("ctime", System.currentTimeMillis()/1000);
//        String sign = PayDigestUtil.getSign(param, "D4SZ8TQK1Z8UPYMOLSKQQPMWYKVXW8IAH");
//        param.put("sign", sign);
//        String reqData = XXPayUtil.genUrlParams(param);
        String url = "http://pay.pay188.vip/api/cashier/common/build?" + reqData;
        String result = XXPayUtil.call4Post(url);
        JSONObject resObj = JSONObject.parseObject(result);




        System.out.println(resObj.toJSONString());
    }
}
