package com.emt.shoppay.util;

import cn.com.infosec.icbc.ReturnValue;

public class Base64Util {
	/**
	 * Base64解码
	 * 
	 * @param base64String
	 *            Base64密文
	 * @return Base64解码以后的明文
	 * @throws Exception
	 */
	public static String decodeBase64(String base64String, String encoding)
			throws Exception {
		String strList[] = base64String.split(" ");

		String newBase64String = String.join("+", strList);

		byte[] bytes = ReturnValue.base64dec(newBase64String.getBytes());
		String retStr = new String(bytes, encoding);
		return retStr;
	}

	/**
	 * base64加密
	 * 
	 * @param str
	 *            明文
	 * @return Base64密文
	 */
	public static String encodeBase64(String str) {
		byte[] bytes = ReturnValue.base64enc(str.getBytes());
		return new String(bytes);
	}

	/**
	 * base64加密
	 *
	 * @param str
	 *            明文
	 * @return Base64密文
	 * @throws Exception
	 */
	public static String encodeBase64(String str, String encoding)
			throws Exception {
		byte[] bytes = ReturnValue.base64enc(str.getBytes(encoding));
		return new String(bytes);
	}
	
	public static String encodeBase64(byte[] b){
		byte[] bytes = ReturnValue.base64enc(b);
		return new String(bytes);
	}
}
