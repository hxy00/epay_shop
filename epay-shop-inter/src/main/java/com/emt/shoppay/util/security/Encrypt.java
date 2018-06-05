package com.emt.shoppay.util.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @category  专用于讯鸟云短信发送通道
 * @author dely
 *
 */
public class Encrypt {
	private static String SHA1(String decript) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.update(decript.getBytes());
		byte messageDigest[] = digest.digest();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++) {
			String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
			if (shaHex.length() < 2) {
				hexString.append(0);
			}
			hexString.append(shaHex);
		}
		return hexString.toString();
	}

	public static String sign(String key, String secret) throws NoSuchAlgorithmException {
		String[] tmpArr = { SHA1(key), secret };
		Arrays.sort(tmpArr);
		return SHA1(tmpArr[0] + tmpArr[1]);
	}

}
