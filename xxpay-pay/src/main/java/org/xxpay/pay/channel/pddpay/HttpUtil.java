/**
 * Copyright (c) 2017 SeaFounder 版权所有
 * SeaFounder Co. Ltd. All rights reserved.
 * <p>
 * This software is the confidential and proprietary
 * information of SeaFounder Co. Ltd.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with SeaFounder Co. Ltd
 */
package org.xxpay.pay.channel.pddpay;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HttpUtil表示http请求工具类
 * @author BOCAI
 * @version 1.0.0
 * @since 2019/8/10
 * @history 1.0.0.0 2019/8/10 15:21 created by【BOCAI】
 */
public class HttpUtil {

    /**
     * 发送httpPost请求，并返回响应字符串
     * @param url 请求的url
     * @param dataMap 请求参数
     * @return 返回String类型参数
     */
    public static String post(String url, Map<String, String> dataMap) {
        return post(url, dataMap, "UTF-8");
    }

    /**
     * 发送httpPost请求，并返回响应字符串
     * @param url 请求的url
     * @param dataMap 请求参数
     * @param charset 请求的字符编码
     * @return 返回String类型参数
     */
    public static String post(String url, Map<String, String> dataMap, String charset) {
        String result = "";
        HttpPost httpPost = new HttpPost(url);

        try {

            if (dataMap != null) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();

                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }

                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps, charset);
                formEntity.setContentEncoding(charset);
                httpPost.setEntity(formEntity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {

            try {
                CloseableHttpResponse response = httpclient.execute(httpPost);
                HttpEntity entity = null;

                try {
                    entity = response.getEntity();
                    result = EntityUtils.toString(entity, charset);
                } finally {
                    EntityUtils.consume(entity);
                    response.close();
                }

            } finally {
                httpclient.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
