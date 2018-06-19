package com.emt.shoppay.sv.impl;

import com.abc.pay.client.JSON;
import com.abc.pay.client.ebus.QueryOrderRequest;
import com.emt.shoppay.acp.sdk.SDKConfig;
import com.emt.shoppay.acp.sdk.SDKUtil;
import com.emt.shoppay.dao.inter.IEpayOrderDetailDao;
import com.emt.shoppay.dao.inter.IEpayOrderOperRecordDao;
import com.emt.shoppay.dao.inter.IEpayParaConfigDao;
import com.emt.shoppay.pojo.*;
import com.emt.shoppay.pojo.aliQuery.AlipaySubmit;
import com.emt.shoppay.pojo.aliQuery.AlipayXmlForDOM4J;
import com.emt.shoppay.pojo.aliQuery.ReturnCode;
import com.emt.shoppay.pojo.bocQuery.httpClient.HttpProtocolHandler;
import com.emt.shoppay.pojo.bocQuery.httpClient.HttpRequest;
import com.emt.shoppay.pojo.bocQuery.httpClient.HttpResponse;
import com.emt.shoppay.pojo.bocQuery.httpClient.HttpResultType;
import com.emt.shoppay.pojo.query.AbcReturnCode;
import com.emt.shoppay.pojo.query.BocReturnCode;
import com.emt.shoppay.pojo.query.CcbReturnCode;
import com.emt.shoppay.pojo.query.IcbcReturnCode;
import com.emt.shoppay.sv.inter.IPayQueryApiSv;
import com.emt.shoppay.sv0.*;
import com.emt.shoppay.util.Global;
import com.emt.shoppay.util.StringUtils;
import com.emt.shoppay.util.ToolsUtil;
import com.emt.shoppay.util.WeixinUtil;
import com.emt.shoppay.util.json.JSONObject;
import com.emt.shoppay.util.security.BaseCoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 支付订单查询接口
 */
@Service
public class PayQueryApiSvImpl implements IPayQueryApiSv {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource	
	private IEpayOrderOperRecordDao iEpayOrderOperRecordDao;

	@Resource
	private IEpayOrderDetailDao iEpayOrderDetailDao;


	@Override
	public Map<String, String> queryFromABC(String sysId, String orderId) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			String PayTypeID = ABCPayConfig.payTypeID;//MapUtils.getString(dbExtend, "PayTypeID");
			String QueryDetail = ABCPayConfig.queryDetail;//MapUtils.getString(dbExtend, "QueryDetail");
			String configIndex = ABCPayConfig.configIndex;//MapUtils.getString(dbExtend, "configIndex");

			if ("".equals(PayTypeID) || "".equals(QueryDetail) || "".equals(configIndex)) {
				logger.debug("[queryFromABC] 获取查询参数为空");
				throw new Exception("获取查询参数为空");
			}

			QueryOrderRequest queryOrderRequest = new QueryOrderRequest();
			queryOrderRequest.queryRequest.put("PayTypeID", PayTypeID);
			queryOrderRequest.queryRequest.put("OrderNo", orderId);
			queryOrderRequest.queryRequest.put("QueryDetail", QueryDetail);//0：状态查询；1：详细查询

			int index = Integer.valueOf(configIndex);
			JSON json = queryOrderRequest.extendPostRequest(index);
			if (null == json) {
				logger.debug("[queryFromABC]没有查询到数据");
				resultMap.put("orderId", orderId);
				resultMap.put("status", "-1");
				resultMap.put("comment", "没有查询到数据");
				return resultMap;
			}
			String ReturnCode = json.GetKeyValue("ReturnCode");
			String ErrorMessage = json.GetKeyValue("ErrorMessage");
			logger.debug("[queryFromABC] ReturnCode   = [" + ReturnCode + "]");
			logger.debug("[queryFromABC] ErrorMessage = [" + ErrorMessage + "]");

			String order = json.GetKeyValue("Order");
			//BASE64 解码
			String retData = new String(BaseCoder.decryptBASE64(order), "GBK");

			if (ReturnCode.equals("0000") && null != retData) {
				JSONObject jsonObject = new JSONObject(retData);
				String orderNo 		= jsonObject.getString("OrderNo");
				String status 		= jsonObject.getString("Status");//01:未支付 02:无回应 03:已请款 04:成功 05:已退款 07:授权确认成功 00:授权已取消 99:失败
				String _orderDate 	= jsonObject.getString("OrderDate");
				String _orderTime 	= jsonObject.getString("OrderTime");
				String payTypeID 	= jsonObject.getString("PayTypeID");

				resultMap.put("orderId", orderNo);
				resultMap.put("status", status);
				resultMap.put("tranDate", _orderDate + _orderTime);
				resultMap.put("comment", AbcReturnCode.getMessage(status));
				resultMap.put("notifyData", retData);
				resultMap.put("tranSerialNo", payTypeID);

				return resultMap;
			} else {
				resultMap.put("orderId", orderId);
				resultMap.put("status", "-3");
				resultMap.put("comment", ErrorMessage);
				return resultMap;
			}
		} catch (Exception e) {
			resultMap.put("orderId", orderId);
			resultMap.put("status", "-2");
			resultMap.put("comment", "出现异常" + e.getMessage());
			return resultMap;
		}
	}

	@Override
	public Map<String, String> queryFromAlipay(String sysId, String orderId) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			//把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			//支付宝交易号(查询时支付宝交易号与商户网站订单号不能同时为空)
			String trade_no = "";

			String service = AlipayConfig.queryService;//MapUtils.getString(dbExtend, "service");
			String partner = AlipayConfig.partner;//MapUtils.getString(dbExtend, "partner");
			String charset = AlipayConfig.inputCharset;//MapUtils.getString(dbExtend, "_input_charset");
			String key = AlipayConfig.key;//MapUtils.getString(dbExtend, "key");

			if ("".equals(service) || "".equals(partner) || "".equals(charset) || "".equals(key)) {
				logger.debug("[queryFromAlipay] 获取查询参数为空");
				throw new Exception("获取查询参数为空");
			}

			sParaTemp.put("service", service);
			sParaTemp.put("partner", partner);
			sParaTemp.put("_input_charset", charset);
			sParaTemp.put("trade_no", trade_no);
			sParaTemp.put("out_trade_no", orderId);

			//建立请求
			String resultXml = AlipaySubmit.buildRequest("", "", sParaTemp, key);
			logger.debug("[queryFromAlipay]支付宝返回数据如下：{}", resultXml);
			if (null == resultXml || "".equals(resultXml)) {
				logger.debug("[queryFromAlipay]没有查询到数据");
				resultMap.put("orderId", orderId);
				resultMap.put("status", "-1");
				resultMap.put("comment", "没有查询到数据");
				return resultMap;
			}
			Map<String, Object> xmlMap = AlipayXmlForDOM4J.getMapByXml(resultXml.trim());
			logger.debug("[queryFromAlipay]查询支付状态结果：{},xmlMap:{}", xmlMap);
			if (xmlMap == null || xmlMap.size() <= 0) {
				logger.debug("[queryFromAlipay]没有查询到数据");
				resultMap.put("orderId", orderId);
				resultMap.put("status", "-1");
				resultMap.put("comment", "没有查询到数据");
				return resultMap;
			}
			String is_success = xmlMap.get("is_success").toString();
			logger.debug("[queryFromAlipay]is_success：{}", is_success);
			if ("T".equals(is_success)) {
				Map<String, String> request = (Map<String, String>) xmlMap.get("request");
				Map<String, String> response = (Map<String, String>) xmlMap.get("response");

				String amount 			= response.get("price").toString();
				String gmt_payment 		= response.get("gmt_payment").toString();
				String trade_status 	= response.get("trade_status").toString();
				trade_no 				= response.get("trade_no").toString();

				String status = ReturnCode.getCode(trade_status);
				String comment = ReturnCode.getMessage(trade_status);

				resultMap.put("orderId", orderId);
				resultMap.put("status", trade_status);
				resultMap.put("tranDate", gmt_payment);
				resultMap.put("comment", comment);
				resultMap.put("notifyData", resultXml);
				resultMap.put("tranSerialNo", trade_no);

				return resultMap;
			} else {
				resultMap.put("orderId", orderId);
				resultMap.put("status", "-3");
				resultMap.put("comment", "is_success = " + is_success);
				return resultMap;
			}
		} catch (Exception e) {
			resultMap.put("orderId", orderId);
			resultMap.put("status", "-2");
			resultMap.put("comment", "出现异常" + e.getMessage());
			return resultMap;
		}
	}

	@Override
	public Map<String, String> queryFromBoc(String sysId, String orderId) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			String merchantNo = BocPayConfig.merchantId;//MapUtils.getString(dbExtend, "merchantNo");
			String signkeyPassword = BocPayConfig.signkeyPassword;//MapUtils.getString(dbExtend, "signkeyPassword");

			//加签
			StringBuilder plainTextBuilder =  new StringBuilder();
			plainTextBuilder.append(merchantNo).append(":").append(orderId);
			String plainText = plainTextBuilder.toString();
			logger.debug("[queryFromBoc][plainText]=["+plainText+"]");
			byte plainTextByte[] = plainText.getBytes("UTF-8");
			//获取私钥证书
			BocPKCSTool tool = BocPKCSTool.getSigner(BocPayConfig.getKeystoreFileB2C(), signkeyPassword, signkeyPassword, "PKCS7");
			//签名
			String signData = tool.p7Sign(plainTextByte);

			logger.info("---------- CommonQueryOrder send message ----------");
			logger.info("[merchantNo]=[" + merchantNo + "]");
			logger.info("[orderNos]=[" + orderId + "]");
			logger.info("[signData]=[" + signData + "]");

			String action = BocPayConfig.pgwPortalUrl + "/CommonQueryOrder.do";
			logger.info("[action]=[" + action + "]");

			// 发送查询请求并获取反馈结果
			//这种官方提供的SB写法，总会报错：PKIX path building failed
//			HttpClient httpClient = new DefaultHttpClient();
//			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
//			formParams.add(new BasicNameValuePair("merchantNo", merchantNo));
//			formParams.add(new BasicNameValuePair("orderNos", orderId));
//			formParams.add(new BasicNameValuePair("signData", signData));
//			HttpEntity entity = new UrlEncodedFormEntity(formParams, "UTF-8");
//			HttpPost post = new HttpPost(action);
//			post.setEntity(entity);
//			HttpResponse postRes = httpClient.execute(post);
//			HttpEntity entityResult = postRes.getEntity();
//			String resultXml =  EntityUtils.toString(entityResult);

			//解决PKIX path building failed:的问题
			Map<String, String> sPara = new HashMap<>();
			sPara.put("merchantNo", merchantNo);
			sPara.put("orderNos", orderId);
			sPara.put("signData", signData);

			HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
			HttpRequest request = new HttpRequest(HttpResultType.BYTES);
			//设置编码集
			request.setCharset("UTF-8");
			request.setParameters(BocPayConfig.generatNameValuePair(sPara));
			request.setUrl(action);
			HttpResponse response = httpProtocolHandler.execute(request);
			if (response == null) {
				throw new Exception("请求查询，响应对象为空");
			}

			String resultXml = response.getStringResult();
			logger.debug("[queryFromBoc]中行查询返回数据如下：{}", resultXml);
			if (TextUtils.isEmpty(resultXml)) {
				logger.debug("[queryFromBoc]没有查询到数据");
				resultMap.put("orderId", orderId);
				resultMap.put("status", "-1");
				resultMap.put("comment", "没有查询到数据");
				return resultMap;
			}
			Map<String, Object> xmlMap = BocPayConfig.getMapByXml(resultXml.trim());
			logger.debug("[queryFromBoc]查询支付状态结果：{},xmlMap:{}", xmlMap);
			if (xmlMap == null || xmlMap.size() <= 0) {
				logger.debug("[queryFromBoc]没有查询到数据");
				resultMap.put("orderId", orderId);
				resultMap.put("status", "-1");
				resultMap.put("comment", "没有查询到数据");
				return resultMap;
			}

			Map<String, String> hMap = (Map<String, String>) MapUtils.getObject(xmlMap, "header");// xmlMap.get("header");
			Map<String, String> bMap = (Map<String, String>) MapUtils.getObject(xmlMap, "body");//(Map<String, String>) xmlMap.get("body");

			String hdlSts = MapUtils.getString(hMap, "hdlSts");
			logger.debug("[queryFromBoc]hdlSts：{}", hdlSts);
			if ("A".equals(hdlSts)) {
				String orderStatus = MapUtils.getString(bMap, "orderStatus");
				logger.debug("[queryFromBoc]orderStatus：{}", orderStatus);

				String amount 	  = MapUtils.getString(bMap, "payAmount");//response.get("price").toString();
				String payTime = MapUtils.getString(bMap, "payTime");//response.get("gmt_create").toString();
				String trade_no   = MapUtils.getString(bMap, "orderSeq");//response.get("trade_no").toString();
				String orderNo 	  = MapUtils.getString(bMap, "orderNo");

				resultMap.put("orderId", orderId);
				resultMap.put("status", orderStatus);
				resultMap.put("tranDate", payTime);
				resultMap.put("comment", BocReturnCode.getMessage(orderStatus));
				resultMap.put("notifyData", resultXml);
				resultMap.put("tranSerialNo", trade_no);

				return resultMap;
			} else {
				logger.debug("[queryFromBoc]操作失败");
				resultMap.put("orderId", orderId);
				resultMap.put("status", "-3");
				resultMap.put("comment", "操作失败");
				return resultMap;
			}
		} catch (Exception e) {
			resultMap.put("orderId", orderId);
			resultMap.put("status", "-2");
			resultMap.put("comment", "出现异常" + e.getMessage());
			return resultMap;
		}
	}

	@Override
	public Map<String, String> queryFromCcb(String sysId, String orderId, String orderDate) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			String QMERCHANTID = CCBPayConfig.QMERCHANTID;//MapUtils.getString(dbExtend, "QMERCHANTID");
			String QBRANCHID = CCBPayConfig.QBRANCHID;//MapUtils.getString(dbExtend, "QBRANCHID");
			String QPOSID = CCBPayConfig.QPOSID;//MapUtils.getString(dbExtend, "QPOSID");
			String QTXCODE = CCBPayConfig.QTXCODE;//MapUtils.getString(dbExtend, "QTXCODE");
			String QUPWD = CCBPayConfig.QUPWD;//MapUtils.getString(dbExtend, "QUPWD");
			String QSEL_TYPE = CCBPayConfig.QSEL_TYPE;//MapUtils.getString(dbExtend, "QSEL_TYPE");
			String bankURL = CCBPayConfig.bankURL;//MapUtils.getString(dbExtend, "bankURL");

//			String bankURL = CcbConfig.queryUrl;
			logger.debug("[queryFromCcb]bankURL:{}", bankURL);
			boolean isToday = isToday(orderDate);
			String KIND = isToday ? "0" : "1";
			logger.debug("[queryFromCcb]KIND:{}", KIND);

			Map<String, Object> map = new HashMap<String, Object>();
			StringBuffer p = new StringBuffer();
			p.append("MERCHANTID=").append(QMERCHANTID);
			p.append("&BRANCHID=").append(QBRANCHID);		//分行代码
			p.append("&POSID=").append(QPOSID);			//柜台号
			p.append("&ORDERDATE=").append("");
			p.append("&BEGORDERTIME=").append("");
			p.append("&ENDORDERTIME=").append("");
			p.append("&ORDERID=").append(orderId);
			p.append("&QUPWD=").append("");
			p.append("&TXCODE=").append(QTXCODE);			//交易码TXCODE=410408，这个参数的值是固定的，不可以修改
			p.append("&TYPE=").append("0");							//流水类型 0支付流水 1退款流水
			p.append("&KIND=").append(KIND);						//流水状态 0 未结算流水 1 已结算流水
			p.append("&STATUS=").append("1");						//交易状态 0失败 1成功 2不确定 3全部（已结算流水查询不支持全部）
			p.append("&SEL_TYPE=").append(QSEL_TYPE);		//查询方式  1页面形式 2文件返回形式 (提供TXT和XML格式文件的下载) 3XML页面形式
			p.append("&PAGE=").append("1");
			p.append("&OPERATOR=").append("");
			p.append("&CHANNEL=").append("");
			logger.debug("[queryFromCcb]p:{}", p.toString());
			String sign = CcbMD5.md5Str(p.toString());
			logger.debug("[queryFromCcb]sign:{}", sign);

			map.put("MERCHANTID", QMERCHANTID);
			map.put("BRANCHID", QBRANCHID);
			map.put("POSID", QPOSID);
			map.put("ORDERDATE", "");
			map.put("BEGORDERTIME", "");
			map.put("ENDORDERTIME", "");
			map.put("ORDERID", orderId);
			map.put("QUPWD", QUPWD);
			map.put("TXCODE", QTXCODE);
			map.put("TYPE", "0");
			map.put("KIND", KIND);
			map.put("STATUS", "1");
			map.put("SEL_TYPE", QSEL_TYPE);
			map.put("PAGE", "1");
			map.put("OPERATOR", "");
			map.put("CHANNEL", "");
			map.put("MAC", sign);
			logger.debug("[queryFromCcb]map:{}", map);

			String resultXml = CcbClientUtil.httpsPost(bankURL, map, "GB2312");
			logger.debug("[ccb_pay_query]建行返回数据如下：{}", resultXml);
			if (null == resultXml || "".equals(resultXml)) {
				logger.debug("[queryFromCcb]没有查询到数据");
				resultMap.put("orderId", orderId);
				resultMap.put("status", "-1");
				resultMap.put("comment", "没有查询到数据");
				return resultMap;
			}

			Map<String, Object> xmlMap = CcbXmlForDOM4J.getMapByXml(resultXml.trim());
			logger.debug("[queryFromCcb]建行返回数据后，xml 转 map：{}", xmlMap);
			logger.debug("[queryFromCcb]查询支付状态结果：{},xmlMap:{}", xmlMap);
			if (xmlMap == null || xmlMap.size() <= 0) {
				logger.debug("[queryFromCcb]没有查询到数据");
				resultMap.put("orderId", orderId);
				resultMap.put("status", "-1");
				resultMap.put("comment", "没有查询到数据");
				return resultMap;
			}
			// 验证返回签名
			Map<String, String> orderInfoMap = (Map<String, String>) xmlMap.get("QUERYORDER");
			logger.debug("[queryFromCcb]建行返回数据后，获取QUERYORDER={}", orderInfoMap);
			if (orderInfoMap == null || orderInfoMap.size() <= 0) {
				logger.debug("[queryFromCcb]没有查询到数据");
				resultMap.put("orderId", orderId);
				resultMap.put("status", "-1");
				resultMap.put("comment", "没有查询到数据");
				return resultMap;
			}
			String returnCode = xmlMap.get("RETURN_CODE").toString();
			if ("000000".equals(returnCode)) {
				String STATUSCODE = MapUtils.getString(orderInfoMap, "STATUSCODE");
				String ORDERDATE = MapUtils.getString(orderInfoMap, "ORDERDATE");//orderInfoMap.get("ORDERDATE").toString();
				String POSID = MapUtils.getString(orderInfoMap, "POSID");//orderInfoMap.get("POSID").toString();

				resultMap.put("orderId", orderId);
				resultMap.put("status", STATUSCODE);
				resultMap.put("tranDate", ORDERDATE);
				resultMap.put("comment", CcbReturnCode.getMessage(STATUSCODE));
				resultMap.put("notifyData", resultXml);
				resultMap.put("tranSerialNo", POSID);

				return resultMap;
			} else {
				resultMap.put("orderId", orderId);
				resultMap.put("status", "-3");
				resultMap.put("comment", "returnCode = " + returnCode);
				return resultMap;
			}
		} catch (Exception e) {
			resultMap.put("orderId", orderId);
			resultMap.put("status", "-2");
			resultMap.put("comment", "出现异常" + e.getMessage());
			return resultMap;
		}
	}

	@Override
	public Map<String, String> queryFromIcbc(String sysId, String orderId, String orderDate) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try{
			String jksFlePath = IcbcConfig.jksFlePath;//MapUtils.getString(dbExtend, "jksFlePathKey");

			IcbcHttpsClientSv0 icbcHttpsClientSv0 = new IcbcHttpsClientSv0();

			String protocolXML = icbcHttpsClientSv0.getHttpsClientPost(orderId, orderDate, jksFlePath);
			String errorDesc = IcbcConfig.getErrorDesc(protocolXML);
			if (errorDesc != null) {
				throw new Exception(errorDesc);
			}

			logger.debug("[queryFromIcbc]查询返回protocolXML={}", protocolXML);
			Map<String, Object> queryMap = IcbcXmlForDOM4J.parseB2C002(protocolXML);
			if (queryMap == null || queryMap.size() <= 0) {
				logger.debug("[queryFromIcbc]没有查询到数据");
				resultMap.put("orderId", orderId);
				resultMap.put("status", "-1");
				resultMap.put("comment", "没有查询到数据");
				return resultMap;
			}

			logger.debug("[queryFromIcbc]查询返回数据：{}", queryMap);
			Map<String, Object> in = (Map<String, Object>)queryMap.get("in");
			Map<String, Object> out = (Map<String, Object>)queryMap.get("out");

			String tranDate = MapUtils.getString(in, "tranDate");//getStrValue(in, "tranDate");
			String tranStat = MapUtils.getString(out, "tranStat");
			String tranTime = MapUtils.getString(out, "tranTime");//getStrValue(out, "tranTime");
			String tranSerialNo = MapUtils.getString(out, "tranSerialNum");//getStrValue(out, "tranSerialNum");

			resultMap.put("orderId", orderId);
			resultMap.put("status", tranStat);
			resultMap.put("tranDate", tranDate + tranTime);
			resultMap.put("comment", IcbcReturnCode.getMessage(tranStat));
			resultMap.put("notifyData", queryMap.toString());
			resultMap.put("tranSerialNo", tranSerialNo);

			return resultMap;
		} catch (Exception e) {
			resultMap.put("orderId", orderId);
			resultMap.put("status", "-2");
			resultMap.put("comment", "出现异常" + e.getMessage());
			return resultMap;
		}
	}

	@Override
	public Map<String, String> queryFromUnionpay(String sysId, String orderId, String orderDate) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			logger.debug("[queryFromUnionpay]查询支付状态开始，查询orderId:{},payCompany:{},tranDate:{}", orderId, orderDate);
			String merId = UnionpayConfig.merId;//MapUtils.getString(dbExtend, "merId");
			String reqUrl = UnionpayConfig.reqUrl;//MapUtils.getString(dbExtend, "reqUrl");
			if (StringUtils.isEmpty(merId)) {
				logger.debug("[queryFromUnionpay] 获取查询参数为空");
				throw new Exception("获取查询参数为空");
			}

			Map<String, String> map = new HashMap<>();
			map.put("version", SDKUtil.version);
			map.put("encoding", SDKUtil.encoding_UTF8);
			map.put("bizType", "000000");
			map.put("txnTime", orderDate);
			map.put("txnType", "00");
			map.put("txnSubType", "00");
			map.put("accessType", "0");
			map.put("signMethod", "01");
			//商户信息
			map.put("merId", merId);
			//订单信息
			map.put("orderId", orderId);

			String fileName = "acp_sdk_emt_windows.properties";
			String operatingSystem = Global.getConfig("epay.OS.switch");
			if("Linux".equals(operatingSystem)){
				fileName = "acp_sdk_emt_linux.properties";
			}

			SDKConfig.getConfig("pay/unopPay/" + fileName);
			Map<String, String> mapRet = SDKUtil.signData(map, SDKUtil.encoding_UTF8);
			map.put("certId", mapRet.get("certId"));
			map.put("signature", mapRet.get("signature"));

			String retString = HttpsUtil.doPostHttps(reqUrl, map, "UTF-8");
			logger.debug("[queryFromUnionpay]查询支付状态查询结果：{}", retString);
			if (null == retString || "".equals(retString)) {
				logger.debug("[queryFromUnionpay]没有查询到数据");
				resultMap.put("orderId", orderId);
				resultMap.put("status", "0");
				return resultMap;
			}

			Map<String, String> retMap = new HashMap<String, String>();
			String[] array = retString.split("\\&");
			if (null == array || array.length <= 0) {
				logger.debug("[queryFromUnionpay]没有查询到数据");
				resultMap.put("orderId", orderId);
				resultMap.put("status", "0");
				return resultMap;
			}
			for (int i = 0; i < array.length; i++) {
				String item = array[i];
				String[] tempArray = item.split("\\=");
				if (tempArray.length > 1) {
					retMap.put(tempArray[0], tempArray[1]);
				} else {
					retMap.put(tempArray[0], "");
				}
			}
			if (retMap == null || retMap.size() <= 0) {
				logger.debug("[queryFromUnionpay]没有查询到数据");
				resultMap.put("orderId", orderId);
				resultMap.put("status", "0");
				return resultMap;
			}

			//签名验证
			if (!SDKUtil.validate(retMap, SDKUtil.encoding_UTF8)) {
				logger.debug("[queryFromUnionpay]验证签名结果[失败].");
				//验签失败，需解决验签问题
				throw new Exception("验证签名结果[失败]");
			}

			String respCode = MapUtils.getString(retMap, "respCode");//retMap.get("respCode").toString();
			if (respCode.equals("00")) {//操作成功
				String origRespCode = MapUtils.getString(retMap, "origRespCode");//查询交易成功时返回
				String orderIdRet = MapUtils.getString(retMap, "orderId");
				String txnTime = MapUtils.getString(retMap,"txnTime");
				String origRespMsg = MapUtils.getString(retMap,"origRespMsg");
				String traceNo = MapUtils.getString(retMap,"traceNo");// retMap.get("traceNo").toString();
				if (origRespCode.equals("00") && orderId.equals(orderIdRet)) {
					String status = "00".equals(origRespCode) ? "1" : origRespCode;

					resultMap.put("orderId", orderId);
					resultMap.put("status", status);
					resultMap.put("tranDate", txnTime);
					resultMap.put("comment", origRespMsg);
					resultMap.put("notifyData", retMap.toString());
					resultMap.put("tranSerialNo", traceNo);

					return resultMap;
				} else {
					resultMap.put("orderId", orderId);
					resultMap.put("status", "-3");
					resultMap.put("comment", "origRespCode = " + origRespCode);
					return resultMap;
				}
			} else {
				resultMap.put("orderId", orderId);
				resultMap.put("status", "-3");
				resultMap.put("comment", "respCode = " + respCode);
				return resultMap;
			}
		} catch (Exception e) {
			resultMap.put("orderId", orderId);
			resultMap.put("status", "-2");
			resultMap.put("comment", "出现异常" + e.getMessage());
			return resultMap;
		}
	}

	@Override
	public Map<String, String> queryFromWeixinpay(String orderId, String sysId) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try{
			String mchId = WeixinConfig.mchId;//MapUtils.getString(dbExtend, "mchId");
			String appId = WeixinConfig.appId;//MapUtils.getString(dbExtend, "appId");
			String appSecret = WeixinConfig.appSecret;//MapUtils.getString(dbExtend, "appSecret");//AppSecret是APPID对应的接口密码，用于获取接口调用凭证access_token时使用。
			String key = WeixinConfig.key;//MapUtils.getString(dbExtend, "key");//交易过程生成签名的密钥，仅保留在商户系统和微信支付后台
			String reqUrl = WeixinConfig.queryReqUrl;//MapUtils.getString(dbExtend, "reqUrl");

			if (TextUtils.isEmpty(mchId)|| TextUtils.isEmpty(appId) || TextUtils.isEmpty(key) || TextUtils.isEmpty(reqUrl)) {
				logger.debug("[queryFromWeixinpay] 获取查询参数为空");
				throw new Exception("获取查询参数为空");
			}

			//生产请求签名
			Map<String, String> map = new HashMap<String, String>();
			Long randomLong = System.currentTimeMillis();
			map.put("appid", appId);
			map.put("mch_id", mchId);
			map.put("out_trade_no", orderId);
			map.put("nonce_str", randomLong.toString());
			List<String> list = new ArrayList<String>();
			list.add("sign");
			list.add("sign_type");
			map = ToolsUtil.paraFilter(map, list, true);
			String prestr = ToolsUtil.createLinkString(map);
			String mysign = wxSign(prestr, key);
			map.put("sign", mysign);

			//转换成xml格式
			String xml = ToolsUtil.mapToXml(map, null, null);

			//请求查询
			String resultXml = WeixinUtil.httpReq(reqUrl, xml);
			logger.debug("[queryFromWeixinpay]查询结果resultXml：{}", resultXml);
			if (TextUtils.isEmpty(resultXml)){
				throw new Exception("微信服务器响应参数为空");
			}
			return getMapByResultXml(resultXml, key);
		} catch (Exception e) {
			resultMap.put("orderId", orderId);
			resultMap.put("status", "-2");
			resultMap.put("comment", "出现异常" + e.getMessage());
			return resultMap;
		}
	}

	public Map<String, String> getMapByResultXml(String strxml, String key) throws Exception {
		if ("".equals(strxml) || strxml == null) {
			throw new Exception("微信支付接口返回数据为空！");
		}
		Map<String, String> map = WeixinUtil.parse_wap_b2c(strxml);
		String sign = MapUtils.getString(map, "sign");
		// 验证数据是否被篡改
		List<String> list = new ArrayList<String>();
		list.add("sign");
		list.add("sign_type");
		map = ToolsUtil.paraFilter(map, list, true);
		String prestr = ToolsUtil.createLinkString(map);
		String mySign = wxSign(prestr, key).toUpperCase();

		if (!mySign.equals(sign)) {
			// 数据被篡改
			logger.debug("[getMapByResultXml]微信支付接口返回数据被篡改！");
			throw new Exception("微信支付接口返回数据被篡改！");
		}
		return map;
	}

	/**
	 * 判断时间是否在当天
	 * @Title: isToday
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param orderDate
	 * @param @return  参数说明
	 * @return boolean    返回类型
	 * @throws
	 */
	private boolean isToday(String orderDate){
		if (null == orderDate || "".equals(orderDate)) {
			return false;//说明是以前没有记录的数据
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String today = dateFormat.format(new Date());
		String orderDateFmt = orderDate.substring(0, 8);
		if (today.equals(orderDateFmt)) {
			return true;
		}
		return false;
	}

	private String wxSign(String text, String key) throws Exception {
		text = text + "&key=" + key;
		return DigestUtils.md5Hex(text.getBytes("UTF-8"));
	}
}
