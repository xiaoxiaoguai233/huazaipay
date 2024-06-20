package org.paydemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.paydemo.utils.HttpUtil;
import org.paydemo.utils.SignUtil;
import sun.security.provider.MD5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class TransMain {



    private static String encodingCharset = "UTF-8";


    public static void main(String[] args) throws IOException {

//        String mchKey = "R6HPJRARJNXZLY9UEM9HSKI4AL1YT2WXRACVH9NN0J1EW3A4EAIUWBJWIM3PSRPRKMTEW4QCGBGIYIOSUALEZVO2TSJJKCJFW4FYQC6FSBKXOBOKARE2TIB4XZSD33HZ";
//
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("mchId", "20000009");  //商户ID
//        params.put("appId", "b0bad2c161dc409f95e7873e68a6b641");  // 当调用微信小程序支付时,该appId必传，为小程序的appId
//        params.put("mchOrderNo", "P21202305292003589432008");   //商户订单号
//        params.put("amount", "10000");              //支付金额
//        params.put("accountName", "张三");              //币种
//        params.put("accountNo", "5122020200098541441");    //客户端IP
//
//        params.put("notifyUrl", "http://pay.dev6688.com/api/pay_notify/df/bfj");   //支付结果后台回调URL
//
//        params.put("remark", "代付100元");          //客户端设备
//        params.put("reqTime", "20221009171032");      //商品主题
//
//        String sign = SignUtil.getSign(params, mchKey);  //	签名
////		String sign = PayDigestUtil.getSign(params, mchKey);  // 签名
//        params.put("sign", sign);
//
//        System.out.println(JSONObject.toJSONString(params));


        String ApiKey = "MSwWkz23fK1UxueHZOaNR8ihpvgrdoVB";
        String out_trade_num = "P33884884499299200";
        int product_id = 56;
        String mobile = "18866667777";
        String userid = "4";
        int amount = 200;
        String notify_url = "http://pay.dev666888.com/api/pay_notify/df/bfj";
        String area = "湖南";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_num", out_trade_num);
        jsonObject.put("product_id", product_id);
        jsonObject.put("mobile", mobile);
        jsonObject.put("notify_url", notify_url);
        jsonObject.put("userid", userid);
        jsonObject.put("amount", amount);
        jsonObject.put("area", area);

        String url = "amount=" + amount + "&area=" + area + "&mobile=" + mobile + "&notify_url=" + notify_url + "&out_trade_num=" + out_trade_num + "&product_id=" + product_id
                + "&userid=" + userid + "&apikey=" + ApiKey;

        String sign = SignUtil.md5(url, encodingCharset).toUpperCase();


        jsonObject.put("sign", sign);

        System.out.println(url);
        System.out.println(sign);

        String post = post("http://v40.mengn.love/yrapi.php/index/recharge", genUrlParams(jsonObject));

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

        return jsonObject.toJSONString();
    }
}
