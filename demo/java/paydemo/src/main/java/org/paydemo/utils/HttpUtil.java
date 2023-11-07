package org.paydemo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	
	/**
	 * <p><b>Description: </b>http post请求
	 * <p>2018年9月30日 上午11:32:16
	 * @param urlPath
	 * @param paramsStr
	 * @return
	 * @throws IOException
	 */
    public static String post(String urlPath, String paramsStr) throws IOException
    {
        //1, 得到URL对象 
        URL url = new URL(urlPath); 
        //2, 打开连接 
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
        //3, 设置提交类型 
        conn.setRequestMethod("POST"); 
        //4, 设置允许写出数据,默认是不允许 false 
        conn.setDoOutput(true); 
        conn.setDoInput(true);//当前的连接可以从服务器读取内容, 默认是true 
        //5, 获取向服务器写出数据的流 
        OutputStream os = conn.getOutputStream(); 
        //参数是键值队  , 不以"?"开始 
        os.write(paramsStr.getBytes()); 
        os.flush();
        //6, 获取响应的数据 
        //得到服务器写回的响应数据 
        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String str = br.readLine();

        JSONObject jsonObject = JSON.parseObject(str);

        System.out.println(jsonObject);

        String html = "";
        if(jsonObject.get("retCode").equals("SUCCESS")){
            html = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Payment</title>\n" +
                    "    <meta http-equiv=\"refresh\" content=\"0;url=" + jsonObject.get("payLink") + " \">" +
                    "</head>\n" +
                    "<body>\n" +
                    "</body>\n" +
                    "</html>";
        }
        return  html;
    }

}
