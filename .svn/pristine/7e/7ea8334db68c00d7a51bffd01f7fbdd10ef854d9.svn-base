package com.emt.shoppay.util.security;

import com.emt.shoppay.sv0.Encodes;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 
 * @category 基础加密组件
 * 
 * @author 胡坤桢
 * @date 2015-10-22
 * @version 1.0
 */

public abstract class BaseCoder {
    public static final String KEY_SHA = "SHA";
    public static final String KEY_SHA1 = "SHA-1";
    public static final String KEY_MD5 = "MD5";
    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_INTERATIONS = 1024;
    public static final int SALT_SIZE = 8;

    /**
     * MAC算法可选以下多种算法
     * 
     * <pre>
     * HmacMD5 
     * HmacSHA1 
     * HmacSHA256 
     * HmacSHA384 
     * HmacSHA512
     * </pre>
     */
    public static final String KEY_MAC = "HmacMD5";

    /**
     * BASE64解密
     * 
     * @param key
     * @return
     * @throws IOException
     */
    public static byte[] decryptBASE64(String key) throws IOException {
        return (new sun.misc.BASE64Decoder()).decodeBuffer(key);
    }

    /**
     * BASE64加密
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptBASE64(byte[] key) {
        return (new sun.misc.BASE64Encoder()).encodeBuffer(key);
    }

    /**
     * MD5加密
     * 
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] encryptMD5(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
        md5.update(data);
        return md5.digest();
    }

    /**
     * SHA加密
     * 
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] encryptSHA(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
        sha.update(data);
        return sha.digest();
    }

    /**
     * SHA1加密
     * 
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] encryptSHA1(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance(KEY_SHA1);
        sha.update(data);
        return sha.digest();
    }

    /**
     * 初始化HMAC密钥
     * 
     * @return
     * @throws NoSuchAlgorithmException
     * @throws Exception
     */
    public static String initMacKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);
        SecretKey secretKey = keyGenerator.generateKey();
        return encryptBASE64(secretKey.getEncoded());
    }

    /**
     * HMAC加密
     * 
     * @param data
     * @param key
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws Exception
     */
    public static byte[] encryptHMAC(byte[] data, String key) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), KEY_MAC);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return mac.doFinal(data);

    }

    /**
     * MD5加密
     * 
     * @param s
     * @return 十六进制 String
     */
    public final static String MD5(String s) {
        return DigestUtils.md5Hex(s);
    }

    /**
     * 字符串转换unicode
     */
    public static String string2Unicode(String string) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);

            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }

    /**
     * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
     */
    public static String entryptPassword(String plainPassword) {
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
        return Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword);
    }
    
    /**
     * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
     */
    public static String getSalt(int salt_size) {
        byte[] salt = Digests.generateSalt(salt_size);       
        return Encodes.encodeHex(salt);
    }  
    
    /**
	 * 验证密码
	 * @param plainPassword 明文密码
	 * @param password 密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		byte[] salt = Encodes.decodeHex(password.substring(0,16));
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
	}
}
