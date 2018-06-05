package com.emt.shoppay.sv.impl;

import com.emt.shoppay.sv.inter.IValidataSv;
import com.emt.shoppay.util.GoldConst;
import com.emt.shoppay.util.security.BaseCoder;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ValidataSvImpl implements IValidataSv {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Boolean ValidataParamData(String key, String tranData,
			String signData) {
		try {
			String secret = GoldConst.getKey(key);
			secret = tranData + secret;

			String md5Str = BaseCoder.MD5(secret).toUpperCase();
			return md5Str.equals(signData);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Boolean ValidataData(String busiid, Map<String, String> tranData,
			String signData) throws Exception {
		String secret = GoldConst.getKey(busiid);
		String prestr = this.createLinkString(tranData);
		prestr = prestr + secret;
		logger.debug("[ValidataSvImpl] MD5未加密以前：" + prestr);
		String sign = DigestUtils.md5Hex(prestr.getBytes("UTF-8")).toUpperCase();
		logger.debug("[ValidataSvImpl] MD5加密以后：" + sign);
		logger.debug("[ValidataSvImpl] 传入的SIGN：" + signData);
		return sign.equals(signData.trim());
	}

	private String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i).trim();
			String value = params.get(key).trim();

			if (i == keys.size() - 1) {
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr;
	}

	@Override
	public String getValidataString(String busiid, String json) {
		try {
			String secret = GoldConst.getKey(busiid);
			String secretStr = json + secret;
			return DigestUtils.md5Hex(secretStr.getBytes("UTF-8")).toUpperCase();
		} catch (Exception e) {
			return "";
		}
	}
}
