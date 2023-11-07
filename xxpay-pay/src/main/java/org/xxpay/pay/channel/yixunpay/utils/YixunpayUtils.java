package org.xxpay.pay.channel.yixunpay.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.xxpay.pay.channel.bolepay.util.BolePayUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * 易迅工具类
 */

public class YixunpayUtils {

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param, String unicode) {
        OutputStreamWriter out = null;
        BufferedReader read = null;
        String result = "";
        try {

            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", unicode);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // conn.setRequestProperty("Content-Type", "application/xml");//x-www-form-urlencoded
            // conn.setRequestProperty("Content-Length", String.valueOf(param.length()));
            conn.setConnectTimeout(1000);

            // 发送POST请求必须设置如下两行
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), unicode);
            // 发送请求参数
            out.write(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            read = new BufferedReader(new InputStreamReader(conn.getInputStream(), unicode));
            String line;
            while ((line = read.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (read != null) {
                    read.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * map 转换成key=value& 字符串
     * 
     * @param sortedMap
     * @return
     */
    public static String mapToStringAndTrim(SortedMap<String, String> sortedMap) {
        StringBuffer sb = new StringBuffer();
        Iterator it = sortedMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = entry.getKey().toString().trim();
            if (entry.getValue() == null) {
                continue;
            }
            String value = entry.getValue().toString().trim();
            if (!"".equals(value) && value != null) {
                sb.append(key + "=" + value + "&");
            }
        }
        return sb.substring(0, sb.length() - 1);
    }

    public static String getRandomString(int length) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
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

}
