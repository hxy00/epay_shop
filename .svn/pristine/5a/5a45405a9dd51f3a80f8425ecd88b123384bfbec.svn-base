/**
 * 
 */
package com.emt.shoppay.util;

import java.util.*;

/**
 * @author Eddy
 * 
 */
public class ToolsUtil {
	/**
	 * 将Map转换为XML
	 * 
	 * @param map
	 * @param version
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static String mapToXml(Map<String, String> map, String version,
			String charset) throws Exception {
		List<String> list = new ArrayList<String>(map.keySet());
		StringBuffer xml = new StringBuffer();
		if (version == null && charset == null) {
			xml.append("<xml>");
			for (int i = 0; i < list.size(); i++) {
				String key = list.get(i).trim();
				String value = map.get(key).trim();
				xml.append("<" + key + ">");
				xml.append(value);
				xml.append("</" + key + ">");
			}
			xml.append("</xml>");
			return xml.toString();
		} else {
			xml.append("<?xml version=\"" + version + "\" encoding=\""
					+ charset + "\"?>");
			for (int i = 0; i < list.size(); i++) {
				String key = list.get(i).trim();
				String value = map.get(key).trim();
				xml.append("<" + key + ">");
				xml.append(value);
				xml.append("</" + key + ">");
			}
			xml.append("</xml>");
			return xml.toString();
		}
	}
	
	/**
	 * 将Map转换为XML
	 * 
	 * @param map
	 * @param version
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static String mapObjToXml(Map<String, Object> map, String version,
			String charset) throws Exception {
		List<String> list = new ArrayList<String>(map.keySet());
		StringBuffer xml = new StringBuffer();
		if (version == null && charset == null) {
			xml.append("<xml>");
			for (int i = 0; i < list.size(); i++) {
				String key = list.get(i).trim();
				String value = map.get(key).toString().trim();
				xml.append("<" + key + ">");
				xml.append(value);
				xml.append("</" + key + ">");
			}
			xml.append("</xml>");
			return xml.toString();
		} else {
			xml.append("<?xml version=\"" + version + "\" encoding=\""
					+ charset + "\"?>");
			for (int i = 0; i < list.size(); i++) {
				String key = list.get(i).trim();
				String value = map.get(key).toString().trim();
				xml.append("<" + key + ">");
				xml.append(value);
				xml.append("</" + key + ">");
			}
			xml.append("</xml>");
			return xml.toString();
		}
	}

	/**
	 * 将Map格式化为key=value&key=value....的格式 Eddy 2015年12月30日
	 * 
	 * @param params
	 * @return
	 * 
	 */
	public static String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1) {
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr;
	}

	/**
	 * 移除特定的Key Eddy 2015年12月30日
	 * 
	 * @param map
	 *            原Map
	 * @param removeKeys
	 *            需要移除的键集合
	 * @param isRemoveEmpty
	 *            是否需要移除空项（null或者""）
	 * @return
	 * @throws Exception
	 * 
	 */
	public static Map<String, String> paraFilter(Map<String, String> map,
			List<String> removeKeys, boolean isRemoveEmpty) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		if (map == null || map.size() <= 0) {
			return result;
		}
		for (String key : map.keySet()) {
			String value = map.get(key);
			if (removeKeys.contains(key)) {
				continue;
			}

			if (isRemoveEmpty && (null == value || "".equals(value))) {
				continue;
			}
			result.put(key, value);
		}
		return result;
	}

	public static Integer toInteger(String str) {
		if (null == str || "".equals(str)) {
			return null;
		}
		Double d = Double.parseDouble(str);
		return d.intValue();
	}

	public static String mapToJson(Map<String, String> map) {
		List<String> list = new ArrayList<String>(map.keySet());
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("{");
		for (int i = 0; i < list.size(); i++) {
			String key = list.get(i).trim();
			String value = map.get(key).trim();
			strBuff.append("\"" + key + "\":\"" + value + "\"");
			if (i < list.size() - 1) {
				strBuff.append(",");
			}
		}
		strBuff.append("}");
		return strBuff.toString();
	}
	
	public static String mapObjToJson(Map<String, Object> map) {
		List<String> list = new ArrayList<String>(map.keySet());
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("{");
		for (int i = 0; i < list.size(); i++) {
			String key = list.get(i).trim();
			String value = map.get(key) + "";
			strBuff.append("\"" + key + "\":\"" + value + "\"");
			if (i < list.size() - 1) {
				strBuff.append(",");
			}
		}
		strBuff.append("}");
		return strBuff.toString();
	}

	public static String getResultParam(String interfaceName,
			String interfaceVersion, String qId, String busiid,
			Map<String, String> tranData, String signData) {
		String json = mapToJson(tranData);
		json = Base64Util.encodeBase64(json);
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("interfaceName=" + interfaceName + "&");
		strBuff.append("interfaceVersion=" + interfaceVersion + "&");
		strBuff.append("qId=" + qId + "&");
		strBuff.append("busiid=" + busiid + "&");
		strBuff.append("tranData=" + json + "&");
		strBuff.append("signData=" + signData);
		return strBuff.toString();
	}
}