package org.xxpay.pay.channel.ssfpay.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;


/**
 * 随手付工具类
 */
public class SsfpayUtils {

    public static String generateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static String generateOrderId() {
        String keyup_prefix = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String keyup_append = String.valueOf(new Random().nextInt(899999) + 100000);
        String pay_orderid = keyup_prefix + keyup_append;// 订单号
        return pay_orderid;
    }

    public static String md5(String str) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] byteDigest = md.digest();
            int i;
            // 字符数组转换成字符串
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
            // return buf.toString().substring(8, 24).toUpperCase();
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
        for (String s : strings) {
            String key = s;
            Object value = stObj.get(key);
            if (value != null && StringUtils.isNotBlank(value.toString()))
                resultMap.put(key, value.toString());
        }
    }

    public static String getSign(Map<String, String> resmap, String paternerKey) throws UnsupportedEncodingException {
        return getMD5(createSign(resmap, false) + "&key=" + paternerKey).toUpperCase();
    }
    
    public static String getMD5(String content) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(content.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } 

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }

        return md5StrBuff.toString();
    }
    
    public static String createSign(Map<String, String> resmap, boolean encode) throws UnsupportedEncodingException {
        Set<String> keysSet = resmap.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);
        StringBuffer temp = new StringBuffer();
        boolean first = true;
        for (Object key : keys) {
            if (key == null || StringUtil.isEmpty(resmap.get(key))) // 鍙傛暟涓虹┖涓嶅弬涓庣鍚�
                continue;
            if (first) {
                first = false;
            } else {
                temp.append("&");
            }
            temp.append(key).append("=");
            Object value = resmap.get(key);
            String valueStr = "";
            if (null != value) {
                valueStr = value.toString();
            }
            if (encode) {
                temp.append(URLEncoder.encode(valueStr, "UTF-8"));
            } else {
                temp.append(valueStr);
            }
        }
        return temp.toString();
    }

}
