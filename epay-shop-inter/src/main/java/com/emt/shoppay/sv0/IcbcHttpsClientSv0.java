package com.emt.shoppay.sv0;

import com.emt.shoppay.pojo.IcbcConfig;
import com.emt.shoppay.sv.impl.BaseSvImpl;
import com.emt.shoppay.util.Config;
import com.emt.shoppay.util.Global;
import com.emt.shoppay.util.StringUtils;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;


public class IcbcHttpsClientSv0 extends BaseSvImpl {
    //根据操作系统设置读取证书路径
    private static String operatingSystem = Global.getConfig("epay.OS.switch");
    private static String flag = operatingSystem.equals("Linux") ? "/" : "";

    private final static String httpUrl = "https://corporbank.icbc.com.cn:446/servlet/ICBCINBSEBusinessServlet";
    // 客户端密钥库
//	private final static String sslKeyStorePath = flag + SystemUtil.getClassPath() + "pay/icbc/mtysb2c.jks";
//	private final static String sslKeyStorePathShop = flag + SystemUtil.getClassPath() + "pay/icbc/shopb2c.jks";
    private final static String sslKeyStorePassword = "123456";
    private final static String sslKeyStoreType = "JKS"; // 密钥库类型，有JKS PKCS12等
    // 客户端信任的证书
//	private final static String sslTrustStore = flag + SystemUtil.getClassPath() + "pay/icbc/mtysb2c.jks";
//	private final static String sslTrustStoreShop = flag + SystemUtil.getClassPath() + "pay/icbc/shopb2c.jks";
    private final static String sslTrustStorePassword = "123456";


    private SSLContext getSSLContext(String jksFilePath) throws Exception{
        SSLContext sslContext = null;

        String sslKeyStorePathShop = IcbcConfig.getJksFilePath(operatingSystem, jksFilePath);
        String sslTrustStoreShop = IcbcConfig.getJksFilePath(operatingSystem, jksFilePath);;

		if (StringUtils.isEmpty(sslKeyStorePathShop) || StringUtils.isEmpty(sslTrustStoreShop)) {
			throw new Exception("查询所需的证书参数为空！");
		}

        System.setProperty("javax.net.ssl.keyStore", sslKeyStorePathShop);
        System.setProperty("javax.net.ssl.keyStorePassword", sslKeyStorePassword);
        System.setProperty("javax.net.ssl.keyStoreType", sslKeyStoreType);
        System.setProperty("javax.net.ssl.trustStore", sslTrustStoreShop);
        System.setProperty("javax.net.ssl.trustStorePassword", sslTrustStorePassword);

        try {
            KeyStore kstore = KeyStore.getInstance(sslKeyStoreType);
            kstore.load(new FileInputStream(sslKeyStorePathShop), sslKeyStorePassword.toCharArray());
            KeyManagerFactory keyFactory = KeyManagerFactory.getInstance("sunx509");
            keyFactory.init(kstore, sslKeyStorePassword.toCharArray());
            KeyStore tstore = KeyStore.getInstance("jks");
            tstore.load(new FileInputStream(sslTrustStoreShop), sslTrustStorePassword.toCharArray());
            TrustManager[] tm;
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("sunx509");
            tmf.init(tstore);
            tm = tmf.getTrustManagers();
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(keyFactory.getKeyManagers(), tm, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    /**
     *
     * @Title: getHttpsClientPost
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param orderNum  订单编号
     * @param tranDate  交易时间
     * @return
     * @return String    返回类型
     * @throws
     */
    @SuppressWarnings("deprecation")
    public String getHttpsClientPost(String orderNum, String tranDate, String jksFlePath) {
        try {
            SSLContext sslContext = getSSLContext(jksFlePath);
            if(sslContext == null){
                return "";
            }
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803 Fedora/3.5.2-2.fc11 Firefox/3.5.2");
            SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext);
            Scheme sch = new Scheme("https", 8443, socketFactory);
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);
            HttpPost httpPost = new HttpPost(httpUrl);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();

            nvps.add(new BasicNameValuePair("APIName", "EAPI"));
            nvps.add(new BasicNameValuePair("APIVersion", "001.001.002.001"));//b2b 001.001.001.001; b2c 001.001.002.001 、 001.001.005.001
            nvps.add(new BasicNameValuePair("MerReqData", getRequestXml(orderNum, tranDate)));

            //httpPost.setHeader(name, value);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            HttpResponse httpResponse = httpClient.execute(httpPost);

            //httpResponse.getEntity().

            String spt = System.getProperty("line.separator");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            StringBuffer stb = new StringBuffer();
            String line = null;
            while ((line = buffer.readLine()) != null) {
                stb.append(line);
            }
            buffer.close();
            return URLDecoder.decode(stb.toString(), "gb2312");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getRequestXml(String orderNum, String tranDate) throws Exception {
//    	String ShopCode = getValue(dbExtend, "ShopCode");
//    	String ShopAccount = getValue(dbExtend, "ShopAccount");
    	if (StringUtils.isEmpty(orderNum) || StringUtils.isEmpty(tranDate)) {
			throw new Exception("查询时orderNum或tranDate为空！");
		}
        StringBuffer stringBuilder = new StringBuffer();
        stringBuilder.append("<?xml  version=\"1.0\" encoding=\"GBK\" standalone=\"no\" ?>");
        stringBuilder.append("<ICBCAPI>");
        stringBuilder.append("<in>");
        stringBuilder.append("<orderNum>"+orderNum+"</orderNum>");//订单号20151126164059001
        stringBuilder.append("<tranDate>"+tranDate+"</tranDate>");//交易日期 20151126164059
        stringBuilder.append("<ShopCode>2402EE20110012</ShopCode>");//商家号码
        stringBuilder.append("<ShopAccount>2402003409000081534</ShopAccount>");//云商账号
        stringBuilder.append("</in>");
        stringBuilder.append("</ICBCAPI>");
        return stringBuilder.toString();
    }

}
