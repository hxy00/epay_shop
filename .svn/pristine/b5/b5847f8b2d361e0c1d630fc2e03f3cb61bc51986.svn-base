package com.emt.shoppay.util;

import com.emt.shoppay.sv0.PropertiesLoader;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 软件开发工具包 配制
 * 
 * @author xuyaowen
 * 
 */
public class Config {

	/**
	 * 保存全局属性值
	 */
	private static Map<String, String> map = Maps.newHashMap();

	/**
	 * 属性文件加载对象
	 */
	private static PropertiesLoader loader; // = new PropertiesLoader("pay/icbc/icbc_conf.properties");

	/**
	 * 获取配置
	 */
	public static String getConfig(String resourcesPaths, String key) {
		loader = new PropertiesLoader(resourcesPaths);
		String value = map.get(key);
		if (value == null) {
			value = loader.getProperty(key);
			map.put(key, value != null ? value : StringUtils.EMPTY);
		}
		return value;
	}
}
