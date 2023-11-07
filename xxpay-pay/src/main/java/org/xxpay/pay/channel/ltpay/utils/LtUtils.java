package org.xxpay.pay.channel.ltpay.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.xxpay.pay.channel.bolepay.util.BolePayUtils;

import com.alibaba.fastjson.JSONObject;

public class LtUtils {
	
	
	public static String generateTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	public static String generateOrderId(){
        String keyup_prefix=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String keyup_append=String.valueOf(new Random().nextInt(899999)+100000);
        String pay_orderid=keyup_prefix+keyup_append;//订单号
        return pay_orderid;
    }
	
	public static String md5(String str) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] byteDigest = md.digest();
            int i;
            //字符数组转换成字符串
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < byteDigest.length; offset++) {
                i = byteDigest[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            // 32位加密
            return buf.toString().toUpperCase();
            // 16位的加密
             //return buf.toString().substring(8, 24).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	
	public static void JsonToMap(JSONObject stObj, Map<String, String> resultMap) {
        if (stObj == null) {
            return;
        }
        Set<String> strings = stObj.keySet();
       for (String s :strings){
            String key = s;
            Object value = stObj.get(key);
            if (value != null && StringUtils.isNotBlank(value.toString()))
                resultMap.put(key, value.toString());
        }
    }
	
	
	public static String getMd5Sign(TreeMap<String, String> signParam, String app_key) {
        StringBuffer stringBuffer = new StringBuffer();
        signParam.remove("sign");
        for (String s : signParam.keySet()) {
            stringBuffer.append(s + "=" + signParam.get(s) + "&");
        }
        stringBuffer.append("key=" + app_key);
        String s = "";
        try {
            s = BolePayUtils.md5(stringBuffer.toString()).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }

}
