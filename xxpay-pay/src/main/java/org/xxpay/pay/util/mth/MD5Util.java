package org.xxpay.pay.util.mth;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* loaded from: classes2.dex */
public class MD5Util {
    public static String encrypt(String str) {
        StringBuffer stringBuffer = new StringBuffer(32);
        try {
            for (byte b : MessageDigest.getInstance("MD5").digest(str.getBytes("gb2312"))) {
                stringBuffer.append(org.apache.commons.lang3.StringUtils.leftPad(Integer.toHexString(b & 255), 2, '0'));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
        }
        return stringBuffer.toString();
    }

    public static String getMessageDigest(byte[] bArr) {
        char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(bArr);
            byte[] digest = messageDigest.digest();
            char[] cArr2 = new char[digest.length * 2];
            int i = 0;
            for (byte b : digest) {
                int i2 = i + 1;
                cArr2[i] = cArr[(b >> 4) & 15];
                i = i2 + 1;
                cArr2[i2] = cArr[b & 15];
            }
            return new String(cArr2);
        } catch (Exception unused) {
            return null;
        }
    }

    public static void main(String[] args) {

        System.out.println(MD5Util.encrypt("123456"));
    }
}


