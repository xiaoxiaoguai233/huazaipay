package org.xxpay.pay.util;

import com.alibaba.fastjson.JSONObject;
import org.xxpay.core.common.constant.PayConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: dingzhiwei
 * @date: 17/12/25
 * @description:
 */
public class Util {

    public static boolean retIsSuccess(JSONObject retObj) {
        if(retObj == null) return false;
        return retObj.getBooleanValue(PayConstant.RETURN_PARAM_RETCODE);
    }

    // 构建威富通数据包
    public static String buildSwiftpayAttach(Map params) {
        JSONObject object = new JSONObject();
        object.put("bank_type", params.get("bank_type"));
        object.put("trade_type", params.get("trade_type"));
        return object.toString();
    }

    /**
     * 构建服务端跳转表单
     * @param jumpUrl
     * @param params
     * @return
     */
    public static String buildJumpForm(String jumpUrl, String params) {
        StringBuffer jumpForm = new StringBuffer();
        jumpForm.append("<form id=\"xxpay_jump\" name=\"xxpay_jump\" action=\""+jumpUrl+"\" method=\"post\">");
        jumpForm.append("<input type=\"hidden\" name=\"params\" id=\"params\" value=\""+params+"\">");
        jumpForm.append("<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >");
        jumpForm.append("</form>");
        jumpForm.append("<script>document.forms[0].submit();</script>");
        return jumpForm.toString();
    }
    
    
    /**
     * 从request获取请求中的json数据
     * @param request
     * @return String
     * */
    public static String JsonReq(HttpServletRequest request) {
        BufferedReader br;
        StringBuilder sb = null;
        String reqBody = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    request.getInputStream()));
            String line = null;
            sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            reqBody = URLDecoder.decode(sb.toString(), "UTF-8");
            reqBody = reqBody.substring(reqBody.indexOf("{"));
            request.setAttribute("inputParam", reqBody);
            System.out.println("JsonReq reqBody>>>>>" + reqBody);
            return reqBody;
        } catch (IOException e) {
            e.printStackTrace();
            return "jsonerror";
        }
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

    public static String generateOrderId(){
        String keyup_prefix=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String keyup_append=String.valueOf(new Random().nextInt(899999)+100000);
        String pay_orderid=keyup_prefix+keyup_append;//订单号
        return pay_orderid;
    }
    public static String generateTime(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

}
