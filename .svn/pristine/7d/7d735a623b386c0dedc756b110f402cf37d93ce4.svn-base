package com.emt.shoppay.util;

import org.apache.commons.codec.digest.DigestUtils;

public class ValidationSign {
	/**
	 * 验证数据是否被篡改
	 * 
	 * @param busiid
	 *            业务ID
	 * @param tranData
	 *            Base64字符串
	 * @param signData
	 *            请求的sign
	 * @return true：验证通过，false：验证失败
	 * @throws Exception
	 */
	public static Boolean validationData(String busiid, String tranData,
			String signData) throws Exception {
		String secret = GoldConst.getKey(busiid);
		secret = tranData + secret;
		String md5Str = DigestUtils.md5Hex(secret).toUpperCase();
		return md5Str.equals(signData);
	}
}