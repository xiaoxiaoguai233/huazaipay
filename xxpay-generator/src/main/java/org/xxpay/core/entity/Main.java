package org.xxpay.core.entity;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        String enstr;
        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        String content = "";
        String smsContent="【淘宝网】验证码2676554020.您已成功订购士泉百货店提供的五金/工具&gt;&gt;安全检查设备&gt;&gt;车底检查镜,共1份,有效期2019-09-07~2019-09-07,如有疑问,请联系卖家.查看券详情：http://m.tb.cn/a.1sRa 查看安装门店地址及订单详情：http://tb.cn/zTtJsdy [风险提示：提供验证码即视为确认收货！]";
        try{
            content = URLEncoder.encode(smsContent, "UTF-8");
        }catch (Exception e){
            return;
        }
        //post方式提交的数据
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", content);
            String mobile = "1065505775012101";
            jsonObject.put("sendernum", mobile);
            String name = "阿明ace";
            jsonObject.put("name", name);
            String code = "2695013597";
            jsonObject.put("code", code);
            jsonObject.put("key", "123456");
            enstr = jsonObject.toString();
//            enstr = AES.encrypt(json);
        } catch (JSONException e) {
            return;
        }
        FormBody formBody = new FormBody.Builder()
                .add("data", enstr)
                .add("sign", md5(enstr + "client!!!"))
                .build();
        final Request request = new Request.Builder()
                .url("http://47.244.167.17/api/app/newsms.html")//请求的url
                .post(formBody)
                .build();
        final String[] result = new String[1];
        //创建/Call
        Response execute = okHttpClient.newCall(request).execute();
        String string = execute.body().string();
        System.out.println(string);



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
}
