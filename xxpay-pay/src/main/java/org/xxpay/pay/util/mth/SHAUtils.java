package org.xxpay.pay.util.mth;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/* loaded from: classes2.dex */
public class SHAUtils {
    public static final String ENCODE_TYPE_HMAC_SHA_256 = "HmacSHA256";
    public static final String ENCODE_UTF_8_LOWER = "utf-8";
    public static final String ENCODE_UTF_8_UPPER = "UTF-8";
    private static final String SECRET = "bdoplLBGC+JP/oIf";
    private static String TAG = "SHAUtils";

    public static String getSHA256Str(String str) throws Exception {
        return getSHA256Str(SECRET, str);
    }

    public static String getSHA256Str(String str, String str2) throws Exception {
        String str3 = "";
        Mac mac = Mac.getInstance(ENCODE_TYPE_HMAC_SHA_256);
        mac.init(new SecretKeySpec(str.getBytes("UTF-8"), ENCODE_TYPE_HMAC_SHA_256));
        byte[] doFinal = mac.doFinal(str2.getBytes("UTF-8"));
        if (doFinal != null && doFinal.length >= 1) {
//            String encodeToString = Base64.encodeToString(doFinal,  Base64.NO_WRAP);

            String encodeToString = Base64.getEncoder().encodeToString(doFinal);
            return encodeToString;
        }
        return "";
    }

    private static String byteToHex(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(hexString);
        }
        return stringBuffer.toString();
    }
}