package com.emt.shoppay.sv.impl;

import com.abc.pay.client.JSON;
import com.abc.pay.client.ebus.QueryOrderRequest;
import com.emt.shoppay.acp.sdk.SDKConfig;
import com.emt.shoppay.acp.sdk.SDKUtil;
import com.emt.shoppay.dao.inter.IEpayOrderDetailDao;
import com.emt.shoppay.dao.inter.IEpayOrderOperRecordDao;
import com.emt.shoppay.dao.inter.IEpayParaConfigDao;
import com.emt.shoppay.pojo.BocPKCSTool;
import com.emt.shoppay.pojo.BocPayConfig;
import com.emt.shoppay.pojo.HttpsUtil;
import com.emt.shoppay.pojo.IcbcConfig;
import com.emt.shoppay.pojo.aliQuery.AlipaySubmit;
import com.emt.shoppay.pojo.aliQuery.AlipayXmlForDOM4J;
import com.emt.shoppay.pojo.aliQuery.ReturnCode;
import com.emt.shoppay.pojo.query.AbcReturnCode;
import com.emt.shoppay.pojo.query.BocReturnCode;
import com.emt.shoppay.pojo.query.CcbReturnCode;
import com.emt.shoppay.pojo.query.IcbcReturnCode;
import com.emt.shoppay.sv.inter.IPayQueryApiSv;
import com.emt.shoppay.sv0.*;
import com.emt.shoppay.util.Global;
import com.emt.shoppay.util.StringUtils;
import com.emt.shoppay.util.ToolsUtil;
import com.emt.shoppay.util.json.JSONObject;
import com.emt.shoppay.util.security.BaseCoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 支付订单查询接口
 */
@Service
public class PayQueryApiApiSvImpl implements IPayQueryApiSv {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource	
	private IEpayOrderOperRecordDao iEpayOrderOperRecordDao;

	@Resource
	private IEpayOrderDetailDao iEpayOrderDetailDao;

	@Resource
	private IEpayParaConfigDao iEpayParaConfigDao;

	@Override
	public Map<String, String> queryFromABC(String sysId, String orderId) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			Map<String, Object> rd = new HashMap<String, Object>();
			rd.put("payCompany", "abc_query");
			rd.put("sysId", sysId);
			rd.put("type", "query");
			List<Map<String, Object>> lstData = iEpayParaConfigDao.Select(rd);

			logger.debug("[abcPay] 查询配置数据，返回：" + lstData);
			if (null != lstData && lstData.size() > 0) {
				Map<String, Object> rMap = lstData.get(0);
				String paraExtend = MapUtils.getString(rMap, "paraExtend");
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> dbExtend = mapper.readValue(paraExtend, Map.class);
				String PayTypeID = MapUtils.getString(dbExtend, "PayTypeID");
				String QueryDetail = MapUtils.getString(dbExtend, "QueryDetail");
				String configIndex = MapUtils.getString(dbExtend, "configIndex");

				if ("".equals(PayTypeID) || "".equals(QueryDetail) || "".equals(configIndex)) {
					logger.debug("[abcPay] 获取查询参数为空");
					throw new Exception("获取查询参数为空");
				}

				QueryOrderRequest queryOrderRequest = new QueryOrderRequest();
				queryOrderRequest.queryRequest.put("PayTypeID", PayTypeID);
				queryOrderRequest.queryRequest.put("OrderNo", orderId);
				queryOrderRequest.queryRequest.put("QueryDetail", QueryDetail);//0：状态查询；1：详细查询

				int index = Integer.valueOf(configIndex);
				JSON json = queryOrderRequest.extendPostRequest(index);
				if (null == json) {
					logger.debug("[abc_pay_query]没有查询到数据");
					resultMap.put("orderId", orderId);
					resultMap.put("status", "-1");
					resultMap.put("comment", "没有查询到数据");
					return resultMap;
				}
				String ReturnCode = json.GetKeyValue("ReturnCode");
				String ErrorMessage = json.GetKeyValue("ErrorMessage");
				logger.debug("[abc_pay_query] ReturnCode   = [" + ReturnCode + "]");
				logger.debug("[abc_pay_query] ErrorMessage = [" + ErrorMessage + "]");

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
			} else {
				throw new Exception("没有查询到配置参数");
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

			Map<String, Object> rd = new HashMap<String, Object>();
			rd.put("payCompany", "alipay_query");
			rd.put("sysId", sysId);
			rd.put("type", "query");
			List<Map<String, Object>> lstData = iEpayParaConfigDao.Select(rd);
			logger.debug("[alipay_query] 查询配置数据，返回：" + lstData);

			if (null != lstData && lstData.size() > 0) {
				Map<String, Object> rMap = lstData.get(0);
				String paraExtend = MapUtils.getString(rMap, "paraExtend");
				ObjectMapper mapper = new ObjectMapper();
				Map<String, String> dbExtend = mapper.readValue(paraExtend, Map.class);
				String service = MapUtils.getString(dbExtend, "service");
				String partner = MapUtils.getString(dbExtend, "partner");
				String charset = MapUtils.getString(dbExtend, "_input_charset");
				String key = MapUtils.getString(dbExtend, "key");

				if ("".equals(service) || "".equals(partner) || "".equals(charset) || "".equals(key)) {
					logger.debug("[abcPay] 获取查询参数为空");
					throw new Exception("获取查询参数为空");
				}

				sParaTemp.put("service", service);
				sParaTemp.put("partner", partner);
				sParaTemp.put("_input_charset", charset);
				sParaTemp.put("trade_no", trade_no);
				sParaTemp.put("out_trade_no", orderId);

				//建立请求
				String resultXml = AlipaySubmit.buildRequest("", "", sParaTemp, key);
				logger.debug("[alipay_query]支付宝返回数据如下：{}", resultXml);
				if (null == resultXml || "".equals(resultXml)) {
					logger.debug("[alipay_query]没有查询到数据");
					resultMap.put("orderId", orderId);
					resultMap.put("status", "-1");
					resultMap.put("comment", "没有查询到数据");
					return resultMap;
				}
				Map<String, Object> xmlMap = AlipayXmlForDOM4J.getMapByXml(resultXml.trim());
				logger.debug("[alipay_query]查询支付状态结果：{},xmlMap:{}", xmlMap);
				if (xmlMap == null || xmlMap.size() <= 0) {
					logger.debug("[alipay_pc_query]没有查询到数据");
					resultMap.put("orderId", orderId);
					resultMap.put("status", "-1");
					resultMap.put("comment", "没有查询到数据");
					return resultMap;
				}
				String is_success = xmlMap.get("is_success").toString();
				logger.debug("[alipay_query]is_success：{}", is_success);
				if ("T".equals(is_success)) {
					Map<String, String> request = (Map<String, String>) xmlMap.get("request");
					Map<String, String> response = (Map<String, String>) xmlMap.get("response");

					String amount 			= response.get("price").toString();
					String gmt_create 		= response.get("gmt_create").toString();
					String trade_status 	= response.get("trade_status").toString();
					trade_no 				= response.get("trade_no").toString();

					String status = ReturnCode.getCode(trade_status);
					String comment = ReturnCode.getMessage(trade_status);

					resultMap.put("orderId", orderId);
					resultMap.put("status", trade_status);
					resultMap.put("tranDate", gmt_create);
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
			} else {
				throw new Exception("没有查询到配置参数");
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
			Map<String, Object> rd = new HashMap<String, Object>();
			rd.put("payCompany", "boc_query");
			rd.put("sysId", sysId);
			rd.put("type", "query");
			List<Map<String, Object>> lstData = iEpayParaConfigDao.Select(rd);

			logger.debug("[boc_query] 查询配置数据，返回：" + lstData);
			if (null != lstData && lstData.size() > 0) {
				Map<String, Object> rMap = lstData.get(0);
				String paraExtend = MapUtils.getString(rMap, "paraExtend");
				ObjectMapper mapper = new ObjectMapper();

				Map<String, String> dbExtend = mapper.readValue(paraExtend, Map.class);
				if (null == dbExtend || dbExtend.size() == 0) {
					logger.debug("[boc_query] 获取查询参数为空");
					throw new Exception("获取查询参数为空");
				}
				String merchantNo = MapUtils.getString(dbExtend, "merchantNo");
				String keyStore = MapUtils.getString(dbExtend, "cerFilePathKey");
				String keyPassword = MapUtils.getString(dbExtend, "keyPassword");

				String pfxFilePathKey = MapUtils.getString(dbExtend, "pfxFilePathKey");//属性文件key
				String signkeyPassword = MapUtils.getString(dbExtend, "signkeyPassword");

				//加签
				StringBuilder plainTextBuilder =  new StringBuilder();
				plainTextBuilder.append(merchantNo).append(":").append(orderId);
				String plainText = plainTextBuilder.toString();
				logger.debug("[plainText]=["+plainText+"]");
				byte  plainTextByte[] = plainText.getBytes("UTF-8");
				//获取私钥证书
				BocPKCSTool tool = BocPKCSTool.getSigner(BocPayConfig.getKeystoreFileB2C(pfxFilePathKey), signkeyPassword, signkeyPassword, "PKCS7");
				//签名
				String signData = tool.p7Sign(plainTextByte);

				logger.info("---------- CommonQueryOrder send message ----------");
				logger.info("[merchantNo]=[" + merchantNo + "]");
				logger.info("[orderNos]=[" + orderId + "]");
				logger.info("[signData]=[" + signData + "]");

				String action = BocPayConfig.pgwPortalUrl + "/CommonQueryOrder.do";
				logger.info("[action]=[" + action + "]");
				// 发送查询请求并获取反馈结果
				DefaultHttpClient httpClient = new DefaultHttpClient();
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				formParams.add(new BasicNameValuePair("merchantNo", merchantNo));
				formParams.add(new BasicNameValuePair("orderNos", orderId));
				formParams.add(new BasicNameValuePair("signData", signData));
				HttpEntity entity = new UrlEncodedFormEntity(formParams, "UTF-8");
				HttpPost post = new HttpPost(action);
				post.setEntity(entity);
				HttpResponse postRes = httpClient.execute(post);
				HttpEntity entityResult = postRes.getEntity();
				String resultXml =  EntityUtils.toString(entityResult);

				logger.debug("[boc_b2c_pc_query]中行查询返回数据如下：{}", resultXml);
				if (TextUtils.isEmpty(resultXml)) {
					logger.debug("[alipay_query]没有查询到数据");
					resultMap.put("orderId", orderId);
					resultMap.put("status", "-1");
					resultMap.put("comment", "没有查询到数据");
					return resultMap;
				}
				Map<String, Object> xmlMap = BocPayConfig.getMapByXml(resultXml.trim());
				logger.debug("[boc_b2c_pc_query]查询支付状态结果：{},xmlMap:{}", xmlMap);
				if (xmlMap == null || xmlMap.size() <= 0) {
					logger.debug("[boc_b2c_pc_query]没有查询到数据");
					resultMap.put("orderId", orderId);
					resultMap.put("status", "-1");
					resultMap.put("comment", "没有查询到数据");
					return resultMap;
				}

				Map<String, String> hMap = (Map<String, String>) MapUtils.getObject(xmlMap, "header");// xmlMap.get("header");
				Map<String, String> bMap = (Map<String, String>) MapUtils.getObject(xmlMap, "body");//(Map<String, String>) xmlMap.get("body");

				String hdlSts = MapUtils.getString(hMap, "hdlSts");
				logger.debug("[boc_b2c_pc_query]hdlSts：{}", hdlSts);
				if ("A".equals(hdlSts)) {
					String orderStatus = MapUtils.getString(bMap, "orderStatus");
					logger.debug("[boc_b2c_pc_query]orderStatus：{}", orderStatus);

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
					logger.debug("[boc_b2c_pc_query]操作失败");
					resultMap.put("orderId", orderId);
					resultMap.put("status", "-3");
					resultMap.put("comment", "操作失败");
					return resultMap;
				}
			} else {
				throw new Exception("没有查询到配置参数");
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
			Map<String, Object> rd = new HashMap<String, Object>();
			rd.put("payCompany", "ccb_query");
			rd.put("sysId", sysId);
			rd.put("type", "query");
			List<Map<String, Object>> lstData = iEpayParaConfigDao.Select(rd);
			logger.debug("[alipayPc] 查询配置数据，返回：" + lstData);
			if (null != lstData && lstData.size() > 0) {
				Map<String, Object> rMap = lstData.get(0);
				String paraExtend = rMap.get("paraExtend").toString();
				ObjectMapper mapper = new ObjectMapper();
				Map<String, String> dbExtend = mapper.readValue(paraExtend, Map.class);

				String QMERCHANTID = MapUtils.getString(dbExtend, "QMERCHANTID");
				String QBRANCHID = MapUtils.getString(dbExtend, "QBRANCHID");
				String QPOSID = MapUtils.getString(dbExtend, "QPOSID");
				String QTXCODE = MapUtils.getString(dbExtend, "QTXCODE");
				String QUPWD = MapUtils.getString(dbExtend, "QUPWD");
				String QSEL_TYPE = MapUtils.getString(dbExtend, "QSEL_TYPE");
				String bankURL = MapUtils.getString(dbExtend, "bankURL");

//				String bankURL = CcbConfig.queryUrl;
				logger.debug("[ccb_pay_query]bankURL:{}", bankURL);
				boolean isToday = isToday(orderDate);
				String KIND = isToday ? "0" : "1";
				logger.debug("[ccb_pay_query]KIND:{}", KIND);

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
				logger.debug("[ccb_pay_query]p:{}", p.toString());
				String sign = CcbMD5.md5Str(p.toString());
				logger.debug("[ccb_pay_query]sign:{}", sign);

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
				logger.debug("[ccb_pay_query]map:{}", map);

				String resultXml = CcbClientUtil.httpsPost(bankURL, map, "GB2312");
				logger.debug("[ccb_pay_query]建行返回数据如下：{}", resultXml);
				if (null == resultXml || "".equals(resultXml)) {
					logger.debug("[ccb_pay_query]没有查询到数据");
					resultMap.put("orderId", orderId);
					resultMap.put("status", "-1");
					resultMap.put("comment", "没有查询到数据");
					return resultMap;
				}

				Map<String, Object> xmlMap = CcbXmlForDOM4J.getMapByXml(resultXml.trim());
				logger.debug("[ccb_pay_query]建行返回数据后，xml 转 map：{}", xmlMap);
				logger.debug("[ccb_pay_query]查询支付状态结果：{},xmlMap:{}", xmlMap);
				if (xmlMap == null || xmlMap.size() <= 0) {
					logger.debug("[ccb_pay_query]没有查询到数据");
					resultMap.put("orderId", orderId);
					resultMap.put("status", "-1");
					resultMap.put("comment", "没有查询到数据");
					return resultMap;
				}
				// 验证返回签名
				Map<String, String> orderInfoMap = (Map<String, String>) xmlMap.get("QUERYORDER");
				logger.debug("[ccb_pay_query]建行返回数据后，获取QUERYORDER：{}的值", orderInfoMap);
				if (orderInfoMap == null || orderInfoMap.size() <= 0) {
					logger.debug("[ccb_pay_query]没有查询到数据");
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
					resultMap.put("comment", CcbReturnCode.getMessage(ORDERDATE));
					resultMap.put("notifyData", resultXml);
					resultMap.put("tranSerialNo", POSID);

					return resultMap;
				} else {
					resultMap.put("orderId", orderId);
					resultMap.put("status", "-3");
					resultMap.put("comment", "returnCode = " + returnCode);
					return resultMap;
				}
			} else {
				throw new Exception("没有查询到配置参数");
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
			Map<String, Object> rd = new HashMap<String, Object>();
			rd.put("payCompany", "icbc_query");
			rd.put("sysId", sysId);
			rd.put("type", "query");
			List<Map<String, Object>> lstData = iEpayParaConfigDao.Select(rd);

			logger.debug("[icbc_pc_query] 查询配置数据，返回：" + lstData);
			if (null != lstData && lstData.size() > 0) {

				Map<String, Object> rMap = lstData.get(0);
				String paraExtend = rMap.get("paraExtend").toString();
				ObjectMapper mapper = new ObjectMapper();
				Map<String, String> dbExtend = mapper.readValue(paraExtend, Map.class);
				if (null == dbExtend || dbExtend.size() == 0) {
					logger.debug("[icbc_pc_query] 获取查询参数为空");
					throw new Exception("获取查询参数为空");
				}
				String jksFlePathKey = MapUtils.getString(dbExtend, "jksFlePathKey");

				IcbcHttpsClientSv0 icbcHttpsClientSv0 = new IcbcHttpsClientSv0();

				String protocolXML = icbcHttpsClientSv0.getHttpsClientPost(orderId, orderDate, jksFlePathKey);
				String errorDesc = IcbcConfig.getErrorDesc(protocolXML);
				if (errorDesc != null) {
					throw new Exception(errorDesc);
				}
				Map<String, Object> queryMap = IcbcXmlForDOM4J.parseB2C002(protocolXML);
				if (queryMap == null || queryMap.size() <= 0) {
					logger.debug("[icbc_query]没有查询到数据");
					resultMap.put("orderId", orderId);
					resultMap.put("status", "-1");
					resultMap.put("comment", "没有查询到数据");
					return resultMap;
				}

				logger.debug("[icbc_query]查询返回数据：{}", queryMap);
				Map<String, Object> in = (Map<String, Object>)queryMap.get("in");
				Map<String, Object> out = (Map<String, Object>)queryMap.get("out");

				String tranStat = MapUtils.getString(out, "tranStat");
				String tranDate = MapUtils.getString(out, "tranDate");//getStrValue(in, "tranDate");
				String tranTime = MapUtils.getString(out, "tranTime");//getStrValue(out, "tranTime");
				String tranSerialNo = MapUtils.getString(out, "tranSerialNum");//getStrValue(out, "tranSerialNum");

				resultMap.put("orderId", orderId);
				resultMap.put("status", tranStat);
				resultMap.put("tranDate", tranDate + tranTime);
				resultMap.put("comment", IcbcReturnCode.getMessage(tranStat));
				resultMap.put("notifyData", queryMap.toString());
				resultMap.put("tranSerialNo", tranSerialNo);

				return resultMap;
			} else {
				throw new Exception("没有查询到配置参数");
			}
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
			logger.debug("[unionpay_query]查询支付状态开始，查询orderId:{},payCompany:{},tranDate:{}", orderId, orderDate);

			Map<String, Object> rd = new HashMap<String, Object>();
			rd.put("payCompany", "unionpay_query");
			rd.put("sysId", sysId);
			rd.put("type", "query");
			List<Map<String, Object>> lstData = iEpayParaConfigDao.Select(rd);
			logger.debug("[unionpay_query] 查询配置数据，返回：" + lstData);

			if (null != lstData && lstData.size() > 0) {
				Map<String, Object> rMap = lstData.get(0);
				String paraExtend = rMap.get("paraExtend").toString();
				ObjectMapper mapper = new ObjectMapper();

				Map<String, String> dbExtend = mapper.readValue(paraExtend, Map.class);
				String merId = MapUtils.getString(dbExtend, "merId");
//				String fileName = MapUtils.getString(dbExtend, "config");
				String reqUrl = MapUtils.getString(dbExtend, "req_url");
				if (StringUtils.isEmpty(merId)) {
					logger.debug("[unionpay_query] 获取查询参数为空");
					throw new Exception("获取查询参数为空");
				}

				Map<String, String> map = new HashMap<>();
				map.put("version", SDKUtil.version);
				map.put("encoding", SDKUtil.encoding_UTF8);
				map.put("signMethod", "01");
				map.put("txnType", "00");
				map.put("txnSubType", "00");
				map.put("bizType", "000000");
				//商户信息
				map.put("accessType", "0");
				map.put("merId", merId);
				//订单信息
				map.put("orderId", orderId);
				map.put("txnTime", orderDate);

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
				logger.debug("[unionpay_b2c_wap_query]查询支付状态查询结果：{}", retString);
				if (null == retString || "".equals(retString)) {
					logger.debug("[unionpay_query]没有查询到数据");
					resultMap.put("orderId", orderId);
					resultMap.put("status", "0");
					return resultMap;
				}

				Map<String, String> retMap = new HashMap<String, String>();
				String[] array = retString.split("\\&");
				if (null == array || array.length <= 0) {
					logger.debug("[unionpay_query]没有查询到数据");
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
					logger.debug("[unionpay_query]没有查询到数据");
					resultMap.put("orderId", orderId);
					resultMap.put("status", "0");
					return resultMap;
				}

				//签名验证
				if (!SDKUtil.validate(retMap, SDKUtil.encoding_UTF8)) {
					logger.debug("验证签名结果[失败].");
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
			} else {
				throw new Exception("没有查询到配置参数");
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
			Map<String, Object> rd = new HashMap<String, Object>();
			rd.put("payCompany", "weixin_query");
			rd.put("sysId", sysId);
			rd.put("type", "query");
			List<Map<String, Object>> lstData = iEpayParaConfigDao.Select(rd);
			logger.debug("[weixinpay_query] 查询配置数据，返回：" + lstData);

			if (null != lstData && lstData.size() > 0) {
				Map<String, Object> rMap = lstData.get(0);
				String paraExtend = rMap.get("paraExtend").toString();
				ObjectMapper mapper = new ObjectMapper();

				Map<String, String> dbExtend = mapper.readValue(paraExtend, Map.class);
				if (null == dbExtend || dbExtend.size() == 0) {
					logger.debug("[weixinpay_query] 获取查询参数为空");
					throw new Exception("获取查询参数为空");
				}

				String mchId = MapUtils.getString(dbExtend, "mchId");
				String appId = MapUtils.getString(dbExtend, "appId");
				String appSecret = MapUtils.getString(dbExtend, "appSecret");
				String reqUrl = MapUtils.getString(dbExtend, "reqUrl");

				if (TextUtils.isEmpty(mchId)|| TextUtils.isEmpty(appId) || TextUtils.isEmpty(appSecret) || TextUtils.isEmpty(reqUrl)) {
					logger.debug("[weixinpay_query] 获取查询参数为空");
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
				String mysign = wxSign(prestr, appSecret);
				map.put("sign", mysign);

				//转换成xml格式
				String xml = ToolsUtil.mapToXml(map, null, null);

				//请求查询
				URL url = new URL(reqUrl);
				logger.debug("[weixinpay_query]请求地址：" + reqUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				logger.debug("[weixinpay_query]----------------------------------one");
				conn.setConnectTimeout(30000); // 设置连接主机超时（单位：毫秒)
				conn.setReadTimeout(30000); // 设置从主机读取数据超时（单位：毫秒)
				logger.debug("[weixinpay_query]----------------------------------two");
				conn.setDoOutput(true); // post请求参数要放在http正文内，顾设置成true，默认是false
				conn.setDoInput(true); // 设置是否从httpUrlConnection读入，默认情况下是true
				conn.setUseCaches(false); // Post 请求不能使用缓存
				logger.debug("[weixinpay_query]----------------------------------three");
				// 设定传送的内容类型是可序列化的java对象(如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestMethod("POST");// 设定请求的方法为"POST"，默认是GET
				logger.debug("[weixinpay_query]----------------------------------four");
				conn.setRequestProperty("Content-Length", xml.length() + "");
				logger.debug("[weixinpay_query]----------------------------------five");
				OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
				logger.debug("[weixinpay_query]xml：" + xml);
				out.write(xml);
				out.flush();
				out.close();
				logger.debug("[weixinpay_query]请求返回：" + HttpURLConnection.HTTP_OK);
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
				logger.debug("[weixinpay_query]查询结果resultXml：{}", resultXml);

				return getMapByResultXml(resultXml, appSecret);
			} else {
				throw new Exception("没有查询到配置参数");
			}
		} catch (Exception e) {
			resultMap.put("orderId", orderId);
			resultMap.put("status", "-2");
			resultMap.put("comment", "出现异常" + e.getMessage());
			return resultMap;
		}
	}

	public Map<String, String> getMapByResultXml(String strxml, String appSecret) throws Exception {
		if ("".equals(strxml) || strxml == null) {
			throw new Exception("微信支付接口返回数据为空！");
		}
		Map<String, String> map = WeixinXmlForDOM4J.parse_wap_b2c(strxml);
		String sign = MapUtils.getString(map, "sign");
		// 验证数据是否被篡改
		List<String> list = new ArrayList<String>();
		list.add("sign");
		list.add("sign_type");
		map = ToolsUtil.paraFilter(map, list, true);
		String prestr = ToolsUtil.createLinkString(map);
		String mySign = wxSign(prestr, appSecret).toUpperCase();

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
