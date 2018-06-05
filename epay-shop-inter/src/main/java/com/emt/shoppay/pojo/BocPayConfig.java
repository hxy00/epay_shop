package com.emt.shoppay.pojo;

import com.emt.shoppay.util.Config;
import com.emt.shoppay.util.Global;
import com.emt.shoppay.util.StringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.http.util.TextUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 中行支付配置
 * @author Administrator
 *
 */
public class BocPayConfig {
//	private static BocPayConfig bocPayConfig;
//	static Logger logger = LoggerFactory.getLogger(bocPayConfig.getClass());

	public static String merchantNoB2CYS = "104520159210003";
	public static String merchantNoB2BYS = "39040";

	public static String payType = "1";

	public static String curCode = "001";
	
	public static String signkeyPassword = "888888";//证书库密码

	public static String pgwPortalUrl = "https://ebspay.boc.cn/PGWPortal";
//	public static String pgwPortalUrl = "https://101.231.206.170/PGWPortal";
//	public static String pgwPortalUrl = "http://180.168.146.70/PGWPortal";
	
	/**
	 * 获取b2c商户号
	 * @param sysId
	 * @return
	 */
//	public static String getB2cMerNo(String sysId){
//		String merNo = "104520159210003";
//		switch (sysId) {
//		case "900001":
//			merNo = "104520159210003";
//			break;
//		case "400001":
//			merNo = "104520159210002";
//			break;
//		default:
//			break;
//		}
//		return merNo;
//	}
	
	/**
	 * 获取b2b商户号
	 * @param sysId
	 * @return
	 */
//	public static String getB2bMerNo(String sysId){
//		String merNo = "39040";
//		switch (sysId) {
//		case "900001":
//			merNo = "39040";
//			break;
//		case "400001":
//			merNo = "39024";
//			break;
//		default:
//			break;
//		}
//		return merNo;
//	}
	
	/**
	 * 获取证书路径 b2c
	 * @return
	 */
	public static String getKeystoreFileB2C(String pfxFilePathKey){
		String operatingSystem = Global.getConfig("epay.OS.switch");
		String keyStorePath = null;
		if("Linux".equals(operatingSystem)){
			keyStorePath = Config.getConfig("pay/boc/boc_conf_linux.properties", pfxFilePathKey);
		} else {
			keyStorePath = Config.getConfig("pay/boc/boc_conf_windows.properties", pfxFilePathKey);
		}
		return keyStorePath;
	}
	
	/**
	 * 获取验签证书
	 * @return
	 */
	public static String getCertificateFile(){
		String operatingSystem = Global.getConfig("epay.OS.switch");
		String bocnetcaPath = null;
		if("Linux".equals(operatingSystem)){
			bocnetcaPath = Config.getConfig("pay/boc/boc_conf_linux.properties", "mtds.bocnetca");
		} else {
			bocnetcaPath = Config.getConfig("pay/boc/boc_conf_windows.properties", "mtds.bocnetca");
		}
		return bocnetcaPath;
	}
	
	public static String getOrderTimeoutDate(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		date.setTime(date.getTime() + (1000 * 60) * 60);//当前时间往前1个小时
		return dateFormat.format(date);
	}
	
	public final static String B2B_DIRECT_VERSION = "1.0.1";
	public final static String SECURITY= "P7";
	
	public static final class MessageId{
		/**发送B2B保付订单实付请求(跨行)**/
		public final static String NB2BRealPayOrder = "0000116";
		/**查询付款银行信息**/
		public final static String NB2BQueryBankInfo = "0000117";
		/**B2B发送保付支付订单请求**/
		public final static String NB2BRecvOrder2 = "0000115";
		/**B2B发送直付支付订单请求**/
		public final static String NB2BRecvOrder = "0000110";
		/**B2B发送直付指定账号支付订单请求**/
		public final static String NB2BRecvAccountOrder = "0000118";
		/**B2B订单查询**/
		public final static String NB2BQueryOrder = "0000111";
		/**B2B商户交易查询**/
		public final static String NB2BQueryTrans = "0000112";
		/**B2B商户联机退款**/
		public final static String NB2BRefundOrder = "0000113";
		/**商户上传下载文件前取票**/
		public final static String NGetTicket = "0000310";
	}
	
	/**
	 * 构建B2B支付参数
	 * @param map
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String bocPayB2BBuildXml(Map<String, String> map)
			throws UnsupportedEncodingException {
		String orderNote = MapUtils.getString(map, "orderNote");
		String backNotifyUrl = MapUtils.getString(map, "backNotifyUrl");
		String frontNotifyUrl = MapUtils.getString(map, "frontNotifyUrl");
		String closeTime = MapUtils.getString(map, "closeTime");
		String subMerchantInfo = MapUtils.getString(map, "subMerchantInfo");
		
		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		strBuf.append("<request version=\"1.0\">");
			strBuf.append("<head>");
				strBuf.append("<requestTime>" + MapUtils.getString(map, "requestTime") + "</requestTime>");
			strBuf.append("</head>");
			strBuf.append("<body>");
				strBuf.append("<orderNo>" + MapUtils.getString(map, "orderNo") + "</orderNo>");
				strBuf.append("<currency>" + MapUtils.getString(map, "currency") + "</currency>");
				strBuf.append("<amount>" + MapUtils.getString(map, "orderAmount") + "</amount>");
				strBuf.append("<orderTime>" + MapUtils.getString(map, "orderTime") + "</orderTime>");
				strBuf.append("<payerBankEpsbtp>" + MapUtils.getString(map, "payerBankEpsbtp") + "</payerBankEpsbtp>");
				
				if (!TextUtils.isEmpty(orderNote)) {
					orderNote = orderNote.replaceAll(" ", "");//替换空格
					//过滤特殊符号
					String regEx = "[`~!@#$%^&*+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"; 
					Pattern p = Pattern.compile(regEx); 
					Matcher m = p.matcher(orderNote);
					orderNote = m.replaceAll("-").trim();
					
					strBuf.append("<orderNote>" + orderNote + "</orderNote>");
				}
				if (!TextUtils.isEmpty(backNotifyUrl)) {
					strBuf.append("<backNotifyUrl>" + backNotifyUrl + "</backNotifyUrl>");
				}
				if (!TextUtils.isEmpty(frontNotifyUrl)) {
					strBuf.append("<frontNotifyUrl>" + frontNotifyUrl + "</frontNotifyUrl>");
				}
				if (!TextUtils.isEmpty(closeTime)) {
					strBuf.append("<closeTime>" + closeTime + "</closeTime>");
				}
				if (!TextUtils.isEmpty(subMerchantInfo)) {
					strBuf.append("<subMerchantInfo>" + subMerchantInfo + "</subMerchantInfo>");
				}
			strBuf.append("</body>");
		strBuf.append("</request>");

		return strBuf.toString();
	}
	
	/**
	 * 构建B2B查询参数
	 * @param map
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String bocQueryB2BBuildXml(Map<String, String> map)
			throws UnsupportedEncodingException {
		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		strBuf.append("<request version=\"1.0\">");
			strBuf.append("<head>");
				strBuf.append("<requestTime>" + MapUtils.getString(map, "requestTime") + "</requestTime>");
			strBuf.append("</head>");
			strBuf.append("<body>");
				strBuf.append("<orderNos>" + MapUtils.getString(map, "orderNos") + "</orderNos>");
			strBuf.append("</body>");
		strBuf.append("</request>");

		return strBuf.toString();
	
	}
	
	/**
	 * 解析xml-b2c
	 * @param xml
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMapByXml(String xml) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Document doc = (Document) DocumentHelper.parseText(xml);
			Element books = doc.getRootElement();
			Iterator<?> Elements = books.elementIterator();
			while (Elements.hasNext()) {
				Element element = (Element) Elements.next();
				if ("header".equals(element.getName())) {
					Map<String, String> hMap = new HashMap<String, String>();
					List<?> subElements = element.elements();
					for (int i = 0; i < subElements.size(); i++) {
                        Element ele = (Element) subElements.get(i);
                        hMap.put(ele.getName(), ele.getTextTrim());
                    }
                    resultMap.put("header", hMap);
				}
				if ("body".equals(element.getName())) {
					Map<String, String> bMap = new HashMap<String, String>();
					Element elChild = element.element("orderTrans");
					List<Element> lstChildEle = elChild.elements();
					//得到全部子节点
                    for (int i = 0; i < lstChildEle.size(); i++) {
                        Element ele = (Element) lstChildEle.get(i);
                        bMap.put(ele.getName(), ele.getTextTrim());
                    }
                    resultMap.put("body", bMap);
				}
			}
		} catch (Exception e) {
			System.err.println("BOC查询xml解析没有节点数据：" + e.getMessage());
		}
		return resultMap;
	}
	
	/**
	 * 解析xml-b2b
	 * @param xml
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMapByXml(String xml, String business) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Document doc = (Document) DocumentHelper.parseText(xml);
			Element books = doc.getRootElement();
			Iterator<?> Elements = books.elementIterator();
			while (Elements.hasNext()) {
				Element element = (Element) Elements.next();
				if ("header".equals(element.getName())) {
					Map<String, String> hMap = new HashMap<String, String>();
					List<?> subElements = element.elements();
					for (int i = 0; i < subElements.size(); i++) {
                        Element ele = (Element) subElements.get(i);
                        hMap.put(ele.getName(), ele.getTextTrim());
                    }
                    resultMap.put("header", hMap);
				}
				if ("body".equals(element.getName())) {
					Map<String, String> bMap = new HashMap<String, String>();
					Element elChild = element.element("order");
					List<Element> lstChildEle = elChild.elements();
					//得到全部子节点
                    for (int i = 0; i < lstChildEle.size(); i++) {
                        Element ele = (Element) lstChildEle.get(i);
                        bMap.put(ele.getName(), ele.getTextTrim());
                    }
                    resultMap.put("body", bMap);
				}
			}
		} catch (Exception e) {
			System.err.println("BOC查询xml解析没有节点数据：" + e.getMessage());
		}
		return resultMap;
	}

	public static String getMessage(String code){
		String returnMessage = "未知参数";
		if (StringUtils.isEmpty(code)) {
			return returnMessage;
		}
		switch (code) {
			case "0":
				returnMessage = "初始";
				break;
			case "1":
				returnMessage = "成功";
				break;
			case "2":
				returnMessage = "失败";
				break;
			case "3":
				returnMessage = "银行处理中";
				break;
			case "4":
				returnMessage = "扣款成功";
				break;
			default:
				break;
		}
		return returnMessage;
	}
}
