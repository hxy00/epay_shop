package com.emt.shoppay.sv.inter;

import java.util.Map;

public interface IValidataSv {
	/**
	 * 
	 * @param key 签名密钥，busiid
	 * @param tranData 传递的接口参数
	 * @param signData 签名
	 * @return 验证签名结果，true：验证通过，false：验证失败
	 */
	Boolean ValidataParamData(String key, String tranData, String signData);
	
	/**
	 * 接口1.0.0.2版本验证签名方法
	 * @param busiid
	 * @param tranData
	 * @param signData
	 * @return
	 */
	Boolean ValidataData(String busiid, Map<String, String> tranData, String signData) throws Exception;
	
	
	/**
	 * 接口返回数据时签名
	 * @param json
	 * @return
	 */
	String getValidataString(String busiid, String json);
}
