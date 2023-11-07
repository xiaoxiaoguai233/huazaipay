package org.paydemo.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * <p><b>Title: </b>SignUtil.java
 * <p><b>Description: </b>签名工具类
 * @version V1.0
 * <p>
 */
public class SignUtil {
	
	private static String encodingCharset = "UTF-8";
	
	/**
	 * <p><b>Description: </b>计算签名摘要
	 * <p>2018年9月30日 上午11:32:46
	 * @param map 参数Map
	 * @param key 商户秘钥
	 * @return
	 */
	public static String getSign(Map<String,Object> map, String key){
		ArrayList<String> list = new ArrayList<String>();
		for(Map.Entry<String,Object> entry:map.entrySet()){
			if(null != entry.getValue() && !"".equals(entry.getValue())){
				if(entry.getValue() instanceof JSONObject) {
					list.add(entry.getKey() + "=" + getSortJson((JSONObject) entry.getValue()) + "&");
				}else {
					list.add(entry.getKey() + "=" + entry.getValue() + "&");
				}
			}
		}
		int size = list.size();
		String [] arrayToSort = list.toArray(new String[size]);
		Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size; i ++) {
			sb.append(arrayToSort[i]);
		}
		String result = sb.toString();
		result += "key=" + key;
		System.out.println("Sign Before MD5:" + result);
		result = md5(result, encodingCharset).toUpperCase();
		System.out.println("Sign Result:" + result);;
		return result;
	}

	public static String getSortJson(JSONObject obj){
		SortedMap map = new TreeMap();
		Set<String> keySet = obj.keySet();
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			Object vlaue = obj.get(key);
			map.put(key, vlaue);
		}
		return JSONObject.toJSONString(map);
	}
	
	
	/**
	 * <p><b>Description: </b>MD5
	 * <p>2018年9月30日 上午11:33:19
	 * @param value
	 * @param charset
	 * @return
	 */
	public static String md5(String value, String charset) {
		MessageDigest md = null;
		try {
			byte[] data = value.getBytes(charset);
			md = MessageDigest.getInstance("MD5");
			byte[] digestData = md.digest(data);
			return toHex(digestData);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String toHex(byte input[]) {
		if (input == null)
			return null;
		StringBuffer output = new StringBuffer(input.length * 2);
		for (int i = 0; i < input.length; i++) {
			int current = input[i] & 0xff;
			if (current < 16)
				output.append("0");
			output.append(Integer.toString(current, 16));
		}

		return output.toString();
	}

}
