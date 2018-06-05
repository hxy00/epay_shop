package com.emt.shoppay.util.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

/**
 * http 请求和应答
 * 
 * @author hkz
 *
 */
public class HttpClientEx {

    public static void main(String[] args) throws Exception {
        Map<String, String> rqParams = new HashMap<String, String>();
        rqParams.put("name", "hukz");
        rqParams.put("pwd", "123456");
        String str = remotePost("/examples/servlets/servlet/RequestInfoExample", rqParams);
        System.out.println(str);
    }

    private static Charset charset = Charset.forName("UTF-8");

    public static String HttpFluentPost(String url, Map<String, String> Pair) throws IOException {
        // 构建参数
        Form f = Form.form();
        for (String key : Pair.keySet()) {
            f.add(key, Pair.get(key));
        }

        return Request.Post(url)
                .connectTimeout(30 * 1000)
                .socketTimeout(30 * 1000)
                .useExpectContinue()
                .version(HttpVersion.HTTP_1_1)
        // .bodyString("Important stuff", ContentType.DEFAULT_TEXT) //数据流的方式提交
                .bodyForm(f.build(), charset) // Consts.UTF_8
                .execute().returnContent().toString();
    }

    public static String HttpFluentGet(String url) throws IOException {

        return Request.Get(url)
                .connectTimeout(30 * 1000)
                .socketTimeout(30 * 1000).
                useExpectContinue()
                .version(HttpVersion.HTTP_1_1)
                .execute().returnContent().toString();
    }

    /**
     * 远程调用服务POST方法
     * 
     * @param url
     *            服务地址
     * @param map
     *            传递参数
     * @return 服务器返回点JSON字符串
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String remotePost(String url, Map<String, String> map) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String httpEntityContent = "";
        HttpPost httppost = new HttpPost(url);
        try {
            List<NameValuePair> formparams = setHttpParams(map);
            UrlEncodedFormEntity param = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(param);
            CloseableHttpResponse response = httpclient.execute(httppost);
            httpEntityContent = GetHttpEntityContent(response);
            response.close();
        } finally {
            httpclient.close();
            httppost.abort();
        }
        return httpEntityContent;
    }

    /**
     * HTTP DELETE方法进行删除操作
     * 
     * @param url
     * @param map
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String remoteDelete(String url, Map<String, String> map) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpDelete httpdelete = new HttpDelete();
        List<NameValuePair> formparams = setHttpParams(map);
        String param = URLEncodedUtils.format(formparams, "UTF-8");
        httpdelete.setURI(URI.create(url + "?" + param));
        HttpResponse response = httpclient.execute(httpdelete);
        String httpEntityContent = GetHttpEntityContent(response);
        httpdelete.abort();
        return httpEntityContent;
    }

    /**
     * HTTP PUT方法进行修改操作
     * 
     * @param url
     * @param map
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String remotePut(String url, Map<String, String> map) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPut httpput = new HttpPut(url);
        List<NameValuePair> formparams = setHttpParams(map);
        UrlEncodedFormEntity param = new UrlEncodedFormEntity(formparams, "UTF-8");
        httpput.setEntity(param);
        HttpResponse response = httpclient.execute(httpput);
        String httpEntityContent = GetHttpEntityContent(response);
        httpput.abort();
        return httpEntityContent;
    }

    /**
     * 远程调用服务GET方法
     * 
     * @param url
     *            服务地址
     * @param map
     *            传递参数，参数中如有中文需要客户端转码
     * @return 服务器返回点JSON字符串
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String remoteGet(String url, Map<String, String> map) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet();
        List<NameValuePair> formparams = setHttpParams(map);
        String param = URLEncodedUtils.format(formparams, "UTF-8");
        httpget.setURI(URI.create(url + "?" + param));
        HttpResponse response = httpclient.execute(httpget);
        String httpEntityContent = GetHttpEntityContent(response);
        httpget.abort();
        return httpEntityContent;
    }

    /**
     * 设置请求参数
     * 
     * @param map
     * @return
     */
    private static List<NameValuePair> setHttpParams(Map<String, String> map) {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        Set<Map.Entry<String, String>> set = map.entrySet();
        for (Map.Entry<String, String> entry : set) {
            formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return formparams;
    }

    /**
     * 获得响应HTTP实体内容
     * 
     * @param response
     * @return
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private static String GetHttpEntityContent(HttpResponse response) throws IOException, UnsupportedEncodingException {
        HttpEntity entity = response.getEntity();
        StringBuilder sb = new StringBuilder();
        if (entity != null) {
            InputStream is = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            try {
                String line = br.readLine();
                while (line != null) {
                    sb.append(line + "\n");
                    line = br.readLine();
                }
            } finally {
                br.close();
            }
        }
        return sb.toString();
    }
}
