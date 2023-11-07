package org.xxpay.pay.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 依赖的jar包有：commons-lang-2.6.jar、httpclient-4.3.2.jar、httpcore-4.3.1.jar、commons
 * -io-2.4.jar
 *
 * @author zhaoyb
 */
public class HttpClientUtils {
    /**
     * 处理get请求.
     *
     * @param url 请求路径
     * @return json
     * 通知商城回调  被动回调
     */
    public String get(String url) {
        // 实例化httpclient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 实例化get方法
        HttpGet httpget = new HttpGet(url);
        //DefaultHttpClient client = new DefaultHttpClient(new PoolingClientConnectionManager());

        // 请求结果
        CloseableHttpResponse response = null;
        String content = "";
        try {
            // 执行get方法
            response = httpclient.execute(httpget);
            if (response.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(content);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 处理post请求.
     *
     * @param url    请求路径
     * @param params 参数
     * @return json
     * <p>
     * 通知商城回调  被动回调
     */
    public static String post(String url, Map<String, String> params) {
        // 实例化httpClient
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 实例化post方法
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
        httpPost.setHeader("Content-Type",  "application/x-www-form-urlencoded;charset=UTF-8");
        // 处理参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }

        // 结果
        CloseableHttpResponse response = null;
        String content = "";
        try {
            // 提交的参数
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(nvps, "UTF-8");
            // 将参数给post方法
            httpPost.setEntity(uefEntity);
            // 执行post方法
            response = httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(content);
            } else {
                content = "{\"errorCode\":\"" + response.getStatusLine().getStatusCode() + "\"}";
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    //------------------------------------------------------------------------------------------------------------//

    /**
     * 处理get请求.
     *
     * @param url 请求路径
     * @return json
     * 通知商城回调  被动回调
     */
    public static String ActiveGet(String url) {
        // 实例化httpclient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 实例化get方法
        HttpGet httpget = new HttpGet(url);
        //DefaultHttpClient client = new DefaultHttpClient(new PoolingClientConnectionManager());

        // 请求结果
        CloseableHttpResponse response = null;
        String content = "";
        try {
            // 执行get方法
            response = httpclient.execute(httpget);
            if (response.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(content);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }


    /**
     * 处理post请求.
     *
     * @param url    请求路径
     * @param params 参数
     * @return json
     * <p>
     * 通知商城回调  主动通知
     */
    public static String ActivePost(String url, Map<String, String> params) {
        System.out.println("ActivePost打印请求地址：" + url);
        // 实例化httpClient
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 实例化post方法
        HttpPost httpPost = new HttpPost(url);
        if (StringUtils.isNotBlank(params.get("Cookie"))) {
            httpPost.setHeader("Cookie", params.get("Cookie"));
            /*httpPost.setHeader("User-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");*/
        }
        // 处理参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }

        // 结果
        CloseableHttpResponse response = null;
        String content = "";
        try {
            // 提交的参数
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(nvps, "UTF-8");
            // 将参数给post方法
            httpPost.setEntity(uefEntity);
            // 执行post方法
            response = httpclient.execute(httpPost);
			/*for (int i = 0; i < c.length; i++) {
				System.out.println("打印返回头："+c[i]);
			}*/
            if (response.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(response.getEntity());
                if (StringUtils.isBlank(content)) {
                    content = "{\"errorCode\":\"返回为空\"}";
                }
                System.out.println("response-content:" + content);
            } else {
                content = "{\"errorCode\":\"" + response.getStatusLine().getStatusCode() + "\"}";
                System.out.println("response-content:" + content);
            }
        } catch (ClientProtocolException e) {
            content = "{\"errorCode\":\"连接异常\"}";
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            //e.printStackTrace();
        } catch (IOException e) {
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            content = "{\"errorCode\":\"连接异常\"}";
            //e.printStackTrace();
        }
        return content;
    }


    public static void main(String[] args) throws Exception {
//		HttpClientUtils hd = new HttpClientUtils();
//		Map<String, String> map = new HashMap<>();
//		map.put("userName", "18671313669");
//		map.put("passWord", "18671313669");
//		String post = hd.post("http://192.168.1.121:8080/hyqPayApp/appInterface/user/login", map);
//		System.out.println(post);
//		postTogateWay();

    }

    public static String cookieVal = "";

    public static void Get(String url_get, String str_param_url, String charset, String cookie) throws IOException {
        // 拼凑get请求的URL字串，使用URLEncoder.encode对特殊和不可见字符进行编码
        // String getURL = GET_URL + "?username=" + URLEncoder.encode("fat man",
        // "utf-8");
        String getURL = url_get + "?" + str_param_url;
        URL getUrl = new URL(getURL);
        // 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
        // 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();

        if (cookie != null) {
            // 发送cookie信息上去，以表明自己的身份，否则会被认为没有权限
            System.out.println("set cookieVal = [" + cookie + "]");
            connection.setRequestProperty("Cookie", cookie);
        }

        // 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到
        // 服务器
        connection.connect();
        // 取得输入流，并使用Reader读取
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
        System.out.println("Contents of get request:");
        String lines;
        while ((lines = reader.readLine()) != null) {
            System.out.println(lines);
        }
        // println(" ")
        reader.close();
        // 断开连接
        connection.disconnect();
    }

    public static String Post(String url_post, String str_param_body, String cookies)
            throws IOException {
        URL url = new URL(url_post);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true); //设置连接输出流为true,默认false (post 请求是以流的方式隐式的传递参数)  
        connection.setDoInput(true); // 设置连接输入流为true  
        connection.setRequestMethod("POST"); // 设置请求方式为post  
        connection.setUseCaches(false); // post请求缓存设为false  
        connection.setInstanceFollowRedirects(true); //// 设置该HttpURLConnection实例是否自动执行重定向  
        // 设置请求头里面的各个属性 (以下为设置内容的类型,设置为经过urlEncoded编码过的from参数)  
        // application/x-javascript text/xml->xml数据 application/x-javascript->json对象 application/x-www-form-urlencoded->表单数据  
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Cookie", "wzhj_app=wanzhongPay");

        connection.connect();

        // 创建输入输出流,用于往连接里面输出携带的参数,(输出内容为?后面的内容)  
        DataOutputStream dataout = new DataOutputStream(connection.getOutputStream());
        String parm = URLEncoder.encode(str_param_body, "utf-8"); //URLEncoder.encode()方法  为字符串进行编码
        dataout.writeBytes(parm);
        dataout.flush();
        dataout.close(); // 重要且易忽略步骤 (关闭流,切记!)             
        // 连接发起请求,处理服务器响应  (从连接获取到输入流并包装为bufferedReader)  
        BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder(); // 用来存储响应数据             
        // 循环读取流,若不到结尾处  
        while ((line = bf.readLine()) != null) {
            sb.append(bf.readLine());
        }
        bf.close();    // 重要且易忽略步骤 (关闭流,切记!)  
        connection.disconnect(); // 销毁连接  
        System.out.println(sb.toString());
        String sbs = sb.toString();
        return sbs;
    }

    public static String postToJSON(String url, String params) throws Exception {

        String charset = "UTF-8";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json;charset=UTF-8");
        post.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
        StringEntity se = new StringEntity(params);
        se.setContentEncoding(charset);
        se.setContentType("application/json");
        post.setEntity(se);
        CloseableHttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        String resData = EntityUtils.toString(entity);
        System.out.println(resData);
        client.close();
        return resData;


    }


}
