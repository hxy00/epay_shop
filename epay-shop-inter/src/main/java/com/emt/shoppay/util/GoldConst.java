package com.emt.shoppay.util;

import java.util.HashMap;
import java.util.Map;

public class GoldConst {

	private static Map<String, String> mapValue = new HashMap<String, String>();
	
	static{
		mapValue.put("10001", "57974ae1-d962-46ec-aeb2-b3107b312df6-2cc7ca66-2080-42a1-adac-d27cf2f5a0ea");	
	}
	
	public static String getKey(String busiid){
		return mapValue.get(busiid) == null? "" : mapValue.get(busiid).toString();
	}
}
