package com.emt.shoppay.sv0;

import com.emt.shoppay.pojo.SSLClient;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 建行支付-http post
 * 
 * @ClassName: CcbClientUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author huangdafei
 * @date 2016年12月12日 下午4:53:05
 * 
 */
public class CcbClientUtil {
	public static String httpPost(String url, Map paramMap) {
		return CcbClientUtil.httpPost(url, paramMap, "GB2312");
	}
	
	public static String httpPost(String url, Map paramMap, String code) {
		String content = null;
		if (url == null || url.trim().length() == 0 || paramMap == null
				|| paramMap.isEmpty())
			return null;
		HttpClient httpClient = new HttpClient();
		httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803 Fedora/3.5.2-2.fc11 Firefox/3.5.2");//
		
		
		PostMethod method = new PostMethod(url);
		Iterator it = paramMap.keySet().iterator();
		

		while (it.hasNext()) {
			String key = it.next() + "";
			Object o = paramMap.get(key);
			if (o != null && o instanceof String) {
				method.addParameter(new NameValuePair(key, o.toString()));
			}
			if (o != null && o instanceof String[]) {
				String[] s = (String[]) o;
				if (s != null)
					for (int i = 0; i < s.length; i++) {
						method.addParameter(new NameValuePair(key, s[i]));
					}
			}
		}
		try {
			
			int statusCode = httpClient.executeMethod(method);
			
			System.out.println("httpClientUtils::statusCode="+statusCode);

			content = new String(method.getResponseBody(), code);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(method!=null)method.releaseConnection();
			method = null;
			httpClient = null;
		}
		return content;

	}
	
	/**
	 * 建行https post
	 * 
	 * @Title: httpsPost
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param url
	 * @param @param map
	 * @param @param code
	 * @param @return 参数说明
	 * @return String 返回类型
	 * @throws
	 */
	public static String httpsPost(String url, Map map, String code) {  
		org.apache.http.client.HttpClient httpClient = null;  
        HttpPost httpPost = null;  
        String result = null;  
        try{
            httpClient = new SSLClient();
            httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803 Fedora/3.5.2-2.fc11 Firefox/3.5.2");//
            
            httpPost = new HttpPost(url);  
            //设置参数  
            List<org.apache.http.NameValuePair> list = new ArrayList<org.apache.http.NameValuePair>();  
            Iterator<?> iterator = map.entrySet().iterator();  
            while(iterator.hasNext()){  
                Entry<String,String> elem = (Entry<String, String>) iterator.next();  
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));  
            }  
            if(list.size() > 0){  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, code);  
                httpPost.setEntity(entity);  
            }  
            HttpResponse response = httpClient.execute(httpPost);  
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity, code);  
                }  
            }  
        }catch(Exception ex){  
            ex.printStackTrace();  
        }  
        return result;  
    }
}
