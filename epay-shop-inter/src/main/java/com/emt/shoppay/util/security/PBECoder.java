package com.emt.shoppay.util.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;
import java.util.Random;

/**
 * @category  PBE安全编码组件
 * 
 * @author 胡坤桢
 * @version 1.0
 * @since 1.0
 */
public abstract class PBECoder extends BaseCoder {
	/**
	 * 支持以下任意一种算法
	 * 
	 * <pre>
	 * PBEWithMD5AndDES 
	 * PBEWithMD5AndTripleDES 
	 * PBEWithSHA1AndDESede
	 * PBEWithSHA1AndRC2_40
	 * </pre>
	 */
	public static final String ALGORITHM = "PBEWITHMD5andDES";

	/**
	 * 盐初始化
	 * 
	 * @return
	 * @throws Exception
	 */
	public static byte[] initSalt() throws Exception {
		byte[] salt = new byte[8];
		Random random = new Random();
		random.nextBytes(salt);
		return salt;
	}

	/**
	 * 转换密钥<br>
	 * 
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private static Key toKey(String password) throws Exception {
		PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey secretKey = keyFactory.generateSecret(keySpec);

		return secretKey;
	}

	/**
	 * 加密
	 * 
	 * @param data
	 *            数据
	 * @param password
	 *            密码
	 * @param salt
	 *            盐
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, String password, byte[] salt) throws Exception {

		Key key = toKey(password);

		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

		return cipher.doFinal(data);

	}

	/**
	 * 解密
	 * 
	 * @param data
	 *            数据
	 * @param password
	 *            密码
	 * @param salt
	 *            盐
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, String password, byte[] salt) throws Exception {

		Key key = toKey(password);

		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

		return cipher.doFinal(data);

	}

	public static void test() throws Exception {
		String inputStr = "abc";
		System.err.println("原文: " + inputStr);
		byte[] input = inputStr.getBytes();

		String pwd = "efg";
		System.err.println("密码: " + pwd);

		byte[] salt = PBECoder.initSalt();

		byte[] data = PBECoder.encrypt(input, pwd, salt);

		System.err.println("加密后: " + PBECoder.encryptBASE64(data));

		byte[] output = PBECoder.decrypt(data, pwd, salt);
		String outputStr = new String(output);

		System.err.println("解密后: " + outputStr);
		// assertEquals(inputStr, outputStr);
	}
}
