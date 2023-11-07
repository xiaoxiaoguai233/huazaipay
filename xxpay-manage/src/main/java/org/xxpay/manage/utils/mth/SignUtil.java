package org.xxpay.manage.utils.mth;

import com.alibaba.fastjson.JSONObject;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/* loaded from: classes2.dex */
public class SignUtil {

    public static JSONObject sign(Request request) {
        if (request == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        RequestBody body = request.body();
        StringBuilder sb = null;
        if (request.method().equals("POST") || request.method().equals("PUT")) {
            if (body != null) {
                if (body instanceof FormBody) {
                    sb = new StringBuilder();
                    FormBody formBody = (FormBody) body;
                    for (int i = 0; i < formBody.size(); i++) {
                        sb.append(formBody.encodedName(i));
                        sb.append(formBody.encodedValue(i));
                    }
                } else {
                    sb = new StringBuilder();
                    Buffer buffer = new Buffer();
                    try {
                        body.writeTo(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Charset forName = Charset.forName("UTF-8");
                    MediaType contentType = body.contentType();
                    if (contentType != null) {
                        forName = contentType.charset(Charset.forName("UTF-8"));
                    }
                    if (forName != null) {
                        sb.append(buffer.readString(forName));
                    }
                }
            }
        } else if (request.method().equals("GET") || request.method().equals("DELETE")) {
            sb = new StringBuilder();
            String url = request.url().url().toString();
            if (url.contains("?")) {
                try {
                    sb.append(URLDecoder.decode(url.substring(url.indexOf("?") + 1).replaceAll("&", "").replaceAll("=", ""), "UTF-8"));
                } catch (UnsupportedEncodingException e2) {
                    e2.printStackTrace();
                }
            }
        }
        if (sb != null) {
            String valueOf = String.valueOf(System.currentTimeMillis());
            sb.append(valueOf);
            String sb2 = sb.toString();
            jsonObject.put("x-qqw-request-nonce", valueOf);
            try {
                String sHA256Str = SHAUtils.getSHA256Str(sb2);
                jsonObject.put("x-qqw-request-sign", sHA256Str);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        return jsonObject;
    }


    public static void main(String[] args) {

        String json = "[{\"orderItems\":[{\"giftCardMoney\":\"10000\",\"giftCardPic\":\"https://img-openroad.quanqiuwa.com/image/d4160d65-3c8d-4d73-b1a3-56a8c266052f.png\",\"giftCardPrice\":\"10000\",\"giftCardTemplateId\":\"114\",\"quantity\":1}],\"orderSource\":\"4\",\"orderType\":\"13\",\"storeId\":\"50\"}]";

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), json);

//        Request request = new Request.Builder()
//                .url("https://nb.quanqiuwa.com/api/orders/app/orders")
//                .post(body)
//                .build();

        Request request = new Request.Builder()
                .url("https://nb.quanqiuwa.com/api/orders/app/orders")
                .get()
                .build();


        Request.Builder newBuilder = request.newBuilder();


        SignUtil.sign(request);

        System.out.println(newBuilder.get());

    }
}
