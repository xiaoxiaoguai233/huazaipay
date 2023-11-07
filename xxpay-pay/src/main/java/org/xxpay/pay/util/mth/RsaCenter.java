package org.xxpay.pay.util.mth;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/* loaded from: classes.dex */
public class RsaCenter {
    private static final String CIPHER_TYPE = "RSA/None/PKCS1Padding";
    private static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
    private static final int MAX_DECRYPT_BLOCK = 128;
    private static final int MAX_ENCRYPT_BLOCK = 117;
    public static final String PUBLIC_KEY;
    private static final String RSA = "RSA";
    public static final String master = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCQfdKCy7ZYd+Lnw1gSAkbo7zmjgSXa+WsNIAK7yyltoMgQk/9IxFsvLf5QxRoA/lmtL5ljrOOoaFNqhAZytNnQJkCpvksi7nDpoCp3+u7bcuyIbR4sSR162t1+jwkgjYP42uk7wXvjZGWHzAemJz5afAGvuFR3ziDwPCQrci0oEQIDAQAB";
    public static final String test = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIoCD0sx/kpIEdTXsana1z2bQMe7j1UhrK2f6K8jIXEaz6dbMq/TykZKCUT4ARjh+0RB0I3Q432bS7r5vl5IibYI7DrNw3BPlalguoBQcT7uSAXkbBeKErTNBS46ftC+b60h3MpkVeuzQavskIugCoYKHmLlJrcJdSwseojkhF4wIDAQAB";

    static {
        PUBLIC_KEY = false ? test : master;
    }

    public static String encryptByPublicKey(String str) {
        return encryptByPublicKey(str, PUBLIC_KEY);
    }

    public static String encryptByPublicKey(String str, String str2) {
        byte[] doFinal;
        byte[] bArr = new byte[0];
        try {
            byte[] bytes = str.getBytes();
            Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
            cipher.init(1, getPublicKey(str2));
            int length = bytes.length;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i = 0;
            int i2 = 0;
            while (true) {
                int i3 = length - i;
                if (i3 <= 0) {
                    break;
                }
                if (i3 > 117) {
                    doFinal = cipher.doFinal(bytes, i, 117);
                } else {
                    doFinal = cipher.doFinal(bytes, i, i3);
                }
                byteArrayOutputStream.write(doFinal, 0, doFinal.length);
                i2++;
                i = i2 * 117;
            }
            bArr = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(bArr));
    }

    public static PublicKey getPublicKey(String str) {
        try {
            return KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(str.getBytes())));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {

        System.out.println(RsaCenter.encryptByPublicKey(MD5Util.encrypt("")));
    }
}