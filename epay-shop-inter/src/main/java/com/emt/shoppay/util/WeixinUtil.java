package com.emt.shoppay.util;

import com.alibaba.druid.util.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WeixinUtil {
	public static Logger logger = LoggerFactory.getLogger(WeixinUtil.class);

	public static Map<String, String> parse_wap_b2c(String protocolXML){
		Map<String, String> mapResult = new HashMap<String, String>();
		
		try{
			Document doc = (Document) DocumentHelper.parseText(protocolXML);
			Element books = doc.getRootElement();
			
			Iterator Elements = books.elementIterator();
			
			while (Elements.hasNext()){
				Element element = (Element) Elements.next();
				if( !StringUtils.isEmpty( element.getTextTrim() )){
                    mapResult.put(element.getName(), element.getTextTrim());
                }
			}
		}catch(Exception e){
			
		}
		return mapResult;
	}

	/**
	 * weixin http request
	 * @param reqUrl
	 * @param xmlParam
	 * @return
	 */
	public static String httpReq(String reqUrl, String xmlParam){
		OutputStreamWriter out = null;
		try{
			URL url = new URL(reqUrl);
			logger.debug("[httpReq]请求地址：" + reqUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(30000); // 设置连接主机超时（单位：毫秒)
			conn.setReadTimeout(30000); // 设置从主机读取数据超时（单位：毫秒)
			conn.setDoOutput(true); // post请求参数要放在http正文内，顾设置成true，默认是false
			conn.setDoInput(true); // 设置是否从httpUrlConnection读入，默认情况下是true
			conn.setUseCaches(false); // Post 请求不能使用缓存
			// 设定传送的内容类型是可序列化的java对象(如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestMethod("POST");// 设定请求的方法为"POST"，默认是GET
			conn.setRequestProperty("Content-Length", xmlParam.length() + "");
			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			logger.debug("[httpReq]xml：" + xmlParam);
			out.write(xmlParam);
			out.flush();
			out.close();

			logger.debug("[httpReq]请求返回：" + HttpURLConnection.HTTP_OK);
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new Exception("订单查询失败，查询返回代码:" + conn.getResponseCode());
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = "";
			StringBuffer strBuf = new StringBuffer();
			while ((line = in.readLine()) != null) {
				strBuf.append(line).append("\n");
			}
			in.close();
			String resultXml = strBuf.toString();
			return resultXml;
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
