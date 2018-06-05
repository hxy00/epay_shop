package com.emt.shoppay.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {
	public static String doPost(String postUrl, Map<String, Object> params)
			throws Exception {
		String queryString = getQueryString(params, "=", "&");
		return postData(postUrl, queryString, "POST");
	}

	public static String doPost2(String postUrl, Map<String, String> params)
			throws Exception {
		String queryString = getQueryString2(params, "=", "&");
		return postData(postUrl, queryString, "POST");
	}

	public static String doGet(String getUrl, Map<String, String> params) throws Exception {
		String queryString = "";
		if (params != null && params.size() > 0)
			queryString = getQueryString2(params, "=", "&");
		return postData(getUrl, queryString, "GET");
	}

	public static String getQueryString(Map<String, Object> params,
			String split1, String split2) {
		List<String> list = new ArrayList<String>(params.keySet());
		String queryString = "";
		for (int i = 0; i < list.size(); i++) {
			String key = list.get(i);
			String value = params.get(key).toString().trim();

			if (i == list.size() - 1) {
				queryString = queryString + key + split1 + value;
			} else {
				queryString = queryString + key + split1 + value + split2;
			}
		}
		return queryString;
	}

	public static String getQueryString2(Map<String, String> params,
			String split1, String split2) {
		List<String> list = new ArrayList<String>(params.keySet());
		String queryString = "";
		for (int i = 0; i < list.size(); i++) {
			String key = list.get(i);
			String value = params.get(key).trim();

			if (i == list.size() - 1) {
				queryString = queryString + key + split1 + value;
			} else {
				queryString = queryString + key + split1 + value + split2;
			}
		}
		return queryString;
	}

	private static String postData(String postUrl, String queryString,
			String method) throws Exception {
		URL url = new URL(postUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(30000); // 设置连接主机超时（单位：毫秒)
		conn.setReadTimeout(30000); // 设置从主机读取数据超时（单位：毫秒)
		conn.setDoOutput(true); // post请求参数要放在http正文内，顾设置成true，默认是false
		conn.setDoInput(true); // 设置是否从httpUrlConnection读入，默认情况下是true
		conn.setUseCaches(false); // Post 请求不能使用缓存
		// 设定传送的内容类型是可序列化的java对象(如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestMethod(method);// 设定请求的方法为"POST"，默认是GET
		conn.setRequestProperty("Content-Length", queryString.length() + "");
		String encode = "utf-8";
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(),
				encode);
		out.write(queryString);
		out.flush();
		out.close();
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			return null;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "UTF-8"));
		String line = "";
		StringBuffer strBuf = new StringBuffer();
		while ((line = in.readLine()) != null) {
			strBuf.append(line).append("\n");
		}
		in.close();
		return strBuf.toString();
	}
	
	
	
	public static String getResultUrl(String url, Map<String, Object> params){
		String queryString = getQueryString(params, "=", "&");
		if(url.contains("?")){
			return url += "&" + queryString;
		}
		return url += "?" + queryString;
	}
}
