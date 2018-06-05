package com.emt.shoppay.sv.impl;


import com.emt.shoppay.acp.sdk.*;
import com.emt.shoppay.dao.inter.IEpayParaConfigDao;
import com.emt.shoppay.pojo.ReturnObject;
import com.emt.shoppay.sv.inter.IPayQueryApiSv;
import com.emt.shoppay.sv.inter.IUnionpayManagerSv;
import com.emt.shoppay.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 银联支付管理
 * 
 * @ClassName: UnionpayManagerSvImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author huangdafei
 * @date 2017年5月4日 上午8:57:04
 * 
 */
@Service
public class UnionpayManagerSvImpl extends BaseSvImpl implements
		IUnionpayManagerSv {
	//根据操作系统设置读取证书路径
	private static String operatingSystem = Global.getConfig("epay.OS.switch");

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private IEpayParaConfigDao iEpayParaConfigDao;

//	@Resource
//	private IActivitySv iActivitySv;

	/*
	 * 银联unionpay_b2b,unionpay_b2c,unionpay_b2c_wap,unionpay_emt支付通用方法 Title:
	 * unionpayDescription:
	 * 
	 * @param upTranData
	 * 
	 * @param upExtend
	 * 
	 * @return
	 * 
	 * @throws Exception
	 * 
	 * @see
	 * com.emt.modules.epay.sv.inter.IUnionpayManagerSv#unionpay(java.util.Map,
	 * java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> unionpay(Map<String, String> upTranData, Map<String, Object> upExtend) throws Exception {
		String orderId = getValue(upTranData, "orderId");
		String fee = getValue(upTranData, "totalFee");
		String resultUrl = getValue(upTranData, "notifyUrl");
		String subject = getValue(upTranData, "subject");
		String ip = getValue(upTranData, "ip");
		String tradeType = getValue(upTranData, "tradeType");
		String appType = getValue(upTranData, "appType");

		if ("".equals(orderId) || "".equals(fee) || "".equals(resultUrl) || "".equals(subject)) {
			logger.debug("[unionpay] 缺少必要的参数！");
			throw new Exception("缺少必要的参数！");
		}

		String interfaceName = getValue(upExtend, "interfaceName", null);
		String interfaceVersion = getValue(upExtend, "interfaceVersion", null);
		String qid = getValue(upExtend, "qid", null);
		String clientType = getValue(upExtend, "clientType", null);
		String merReference = getValue(upExtend, "merReference", null);
		String busiid = getValue(upExtend, "busiid", null);
		String sysId = getValue(upExtend, "sysId", null);

		Map<String, Object> rd = new HashMap<String, Object>();
		rd.put("payCompany", interfaceName);
		rd.put("sysId", sysId);
		rd.put("type", "pay");
		List<Map<String, Object>> lstData = iEpayParaConfigDao.Select(rd);
		if (null != lstData && lstData.size() > 0) {
			Map<String, Object> rMap = lstData.get(0);
			String paraExtend = rMap.get("paraExtend").toString();
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> dbExtend = mapper.readValue(paraExtend, Map.class);
			String bkInterfaceName = getValue(dbExtend, "interfaceName");
			String bkInterfaceVersion = getValue(dbExtend, "interfaceVersion");
			String merId = getValue(dbExtend, "merId");
			String encoding = getValue(dbExtend, "encoding");
			String bizType = getValue(dbExtend, "bizType");
			String payNotifyUrl = getValue(dbExtend, "notify_url");
			String backURL = getValue(dbExtend, "backUrl");
			String timeOut = getValue(dbExtend, "timeOut");
			timeOut = !TextUtils.isEmpty(timeOut) ? timeOut : "30";

			String fileName = "acp_sdk_emt_windows.properties";
			String operatingSystem = Global.getConfig("epay.OS.switch");
			if("Linux".equals(operatingSystem)){
				fileName = "acp_sdk_emt_linux.properties";
			}

			if ("".equals(merId) || "".equals(encoding) || "".equals(fileName)
					|| "".equals(payNotifyUrl) || "".equals(bkInterfaceVersion)
					|| "".equals(backURL) || "".equals(timeOut)) {
				logger.debug("[" + bkInterfaceName + "] 获取支付参数为空");
				throw new Exception("获取支付参数为空");
			}
			//获取订单失效时间
			Date date = new Date();
			String orderTimeOutDate = getOrderTimeoutDate(date, Integer.valueOf(timeOut));
			
			Integer totalFee = Integer.valueOf(fee);
			// totalFee = totalFee / 100f;
			String globalUrl = Global.getConfig("epay.notify.url");
			payNotifyUrl = globalUrl + payNotifyUrl;
			String backUrl = globalUrl + backURL;
			String orderDate = DateUtils.DateTimeToYYYYMMDDhhmmss();

			Map<String, String> data = new HashMap<String, String>();
			data.put("version", bkInterfaceVersion);
			data.put("encoding", encoding);
			data.put("signMethod", "01");
			data.put("txnType", "01");
			data.put("txnSubType", "01");
			data.put("bizType", bizType);// 000201：B2C 网关支付 000301：认证支付 2.0
											// 000302：评级支付 000401：代付 000501：代收
											// 000601：账单支付 000801：跨行收单
											// 000901：绑定支付 001001：订购 000202：B2B
			data.put("channelType", "07");
			data.put("merId", merId);
			data.put("accessType", "0");
			data.put("orderId", orderId);
			data.put("txnTime", orderDate);
			data.put("currencyCode", "156");
			data.put("txnAmt", String.valueOf(totalFee));
			data.put("backUrl", backUrl);
			data.put("frontUrl", payNotifyUrl);
			data.put("accType", "01");
//			data.put("payTimeout", orderTimeOutDate);
//			data.put("reqReserved", Base64Util.encodeBase64("unionpay_b2c", "UTF-8"));

			SDKConfig.getConfig("pay/unopPay/"+ fileName);
			data = SDKUtil.signData(data, encoding);

			Map<String, String> extend = new HashMap<>();
			extend.put("merUrl", payNotifyUrl);
			extend.put("merVAR", "emaotai.cn.epay");
			extend.put("orderDate", orderDate);
			extend.put("buildData", ToolsUtil.mapToJson(data));
			extend.put("shopCode", merId);

			// 写入数据库epay的epay_oder_detail表中
			Integer retInt = insertPayOrderDetail(upTranData, upExtend, dbExtend, extend);
			logger.debug("[" + bkInterfaceName + "] 保存detail表状态：", retInt);

			Map<String, Object> payMap = new HashMap<String, Object>();
			payMap.put("version", data.get("version"));
			payMap.put("encoding", data.get("encoding"));
			payMap.put("signMethod", data.get("signMethod"));
			payMap.put("txnType", data.get("txnType"));
			payMap.put("txnSubType", data.get("txnSubType"));
			payMap.put("bizType", data.get("bizType"));
			payMap.put("channelType", data.get("channelType"));
			payMap.put("merId", data.get("merId"));
			payMap.put("accessType", data.get("accessType"));
			payMap.put("orderId", data.get("orderId"));
			payMap.put("txnTime", data.get("txnTime"));
			payMap.put("currencyCode", data.get("currencyCode"));
			payMap.put("txnAmt", data.get("txnAmt"));
			payMap.put("frontUrl", data.get("frontUrl"));
			payMap.put("backUrl", data.get("backUrl"));
			payMap.put("accType", data.get("accType"));
			payMap.put("certId", data.get("certId"));
			payMap.put("signature", data.get("signature"));
			payMap.put("payTimeout", orderTimeOutDate);
			return payMap;
		} else {
			throw new Exception("获取支付参数为空！");
		}
	}

	/*
	 * 银联闪付 Title: unionpayQuickPassDescription:
	 * 
	 * @param upTranData
	 * 
	 * @param upExtend
	 * 
	 * @return
	 * 
	 * @throws Exception
	 * 
	 * @see
	 * com.emt.modules.epay.sv.inter.IUnionpayManagerSv#unionpayQuickPass(java
	 * .util.Map, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> unionpayQuickPass(Map<String, String> upTranData, Map<String, Object> upExtend) throws Exception {
		String orderId = getValue(upTranData, "orderId");
		String fee = getValue(upTranData, "totalFee");
		String resultUrl = getValue(upTranData, "notifyUrl");
		String subject = getValue(upTranData, "subject");
		String activity = getValue(upTranData, "activity");
		String ip = getValue(upTranData, "ip");
		String tradeType = getValue(upTranData, "tradeType");
		String appType = getValue(upTranData, "appType");

		if ("".equals(orderId) || "".equals(fee) || "".equals(resultUrl)
				|| "".equals(subject)) {
			logger.debug("[unionpayQuickPass] 缺少必要的参数！");
			throw new Exception("缺少必要的参数！");
		}

		String interfaceName = getValue(upExtend, "interfaceName", null);
		String interfaceVersion = getValue(upExtend, "interfaceVersion", null);
		String qid = getValue(upExtend, "qid", null);
		String clientType = getValue(upExtend, "clientType", null);
		String merReference = getValue(upExtend, "merReference", null);
		String busiid = getValue(upExtend, "busiid", null);
		String sysId = getValue(upExtend, "sysId", null);

		Map<String, Object> rd = new HashMap<String, Object>();
		rd.put("payCompany", interfaceName);
		rd.put("sysId", sysId);
		rd.put("type", "pay");
		List<Map<String, Object>> lstData = iEpayParaConfigDao.Select(rd);
		if (null != lstData && lstData.size() > 0) {
			Map<String, Object> rMap = lstData.get(0);
			String paraExtend = rMap.get("paraExtend").toString();
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> dbExtend = mapper.readValue(paraExtend, Map.class);
			String bkInterfaceName = getValue(dbExtend, "interfaceName");
			String bkInterfaceVersion = getValue(dbExtend, "interfaceVersion");
			String merId = getValue(dbExtend, "merId");
			String encoding = getValue(dbExtend, "encoding");
			String bizType = getValue(dbExtend, "bizType");
			String payNotifyUrl = getValue(dbExtend, "notify_url");
			String backURL = getValue(dbExtend, "backUrl");
			String timeOut = getValue(dbExtend, "timeOut");
			timeOut = !TextUtils.isEmpty(timeOut) ? timeOut : "30";

			String fileName = "acp_sdk_emt_windows.properties";
			String operatingSystem = Global.getConfig("epay.OS.switch");
			if("Linux".equals(operatingSystem)){
				fileName = "acp_sdk_emt_linux.properties";
			}

			if ("".equals(merId) || "".equals(encoding) || "".equals(fileName)
					|| "".equals(payNotifyUrl) || "".equals(bkInterfaceVersion)
					|| "".equals(backURL) || "".equals(timeOut)) {
				logger.debug("[" + bkInterfaceName + "] 获取支付参数为空");
				throw new Exception("获取支付参数为空");
			}

			//获取订单失效时间
			Date date = new Date();
			String orderTimeOutDate = getOrderTimeoutDate(date, Integer.valueOf(timeOut));

			Integer totalFee = Integer.valueOf(fee);
			// totalFee = totalFee / 100f;
			String globalUrl = Global.getConfig("epay.notify.url");
			payNotifyUrl = globalUrl + payNotifyUrl;
			String backUrl = globalUrl + backURL;
			String orderDate = DateUtils.DateTimeToYYYYMMDDhhmmss();

			Map<String, String> data = new HashMap<String, String>();
			data.put("version", bkInterfaceVersion);
			data.put("encoding", encoding);
			data.put("signMethod", "01");
			data.put("txnType", "01");
			data.put("txnSubType", "01");
			data.put("bizType", bizType);// 000201：B2C 网关支付 000301：认证支付 2.0
											// 000302：评级支付 000401：代付 000501：代收
											// 000601：账单支付 000801：跨行收单
											// 000901：绑定支付 001001：订购 000202：B2B
			data.put("channelType", "08");
			data.put("merId", merId);
			data.put("accessType", "0");
			data.put("orderId", orderId);
			data.put("txnTime", orderDate);
			data.put("currencyCode", "156");
			data.put("txnAmt", String.valueOf(totalFee));
			data.put("backUrl", backUrl);
			data.put("frontUrl", payNotifyUrl);
			data.put("accType", "01");
			data.put("payTimeout", orderTimeOutDate);
			data.put("reqReserved", interfaceName);//支付接口标识
			if ("1".equals(activity)) { // 是否参加活动，0：不参加活动，1：参加活动
				data.put("reserved", "{discountCode=activity}"); // 如果参与活动，该字段的值为“activity”，反之则不上送该字段
			}

			SDKConfig.getConfig("pay/unopPay/"+fileName);
			data = SDKUtil.signData(data, encoding);

			Map<String, String> extend = new HashMap<>();
			extend.put("merUrl", payNotifyUrl);
			extend.put("merVAR", "emaotai.cn.epay");
			extend.put("orderDate", orderDate);
			extend.put("buildData", ToolsUtil.mapToJson(data));

			logger.debug("[" + bkInterfaceName + "] 签名以前：" + data);
			data = SDKUtil.signData(data, SDKUtil.encoding_UTF8);
			logger.debug("[" + bkInterfaceName + "] 签名以后：" + data);

			Map<String, String> retData = new HashMap<String, String>();
			String requestAppUrl = SDKConfig.getConfig().getAppRequestUrl();
			ReturnObject returnObject = new ReturnObject();
			try {
				logger.debug("[" + bkInterfaceName + "]请求地址：" + requestAppUrl);
				retData = post(data, requestAppUrl, "UTF-8");
				// 应答码规范参考open.unionpay.com帮助中心 下载 产品接口规范 《平台接入接口规范-第5部分-附录》
				if (null != retData && retData.size() > 0) {
					logger.debug("[" + bkInterfaceName + "]返回值：" + retData);
					if (validate(retData, "UTF-8")) {
						logger.debug("[ApplePay]验证签名成功");
						String respCode = retData.get("respCode");
						/**
						 * 00 成功，01-09 因银联全渠道系统原因导致的错误 ，10-29 有关商户端上送报文格式检查导致的错误
						 * 30-59 有关商户/收单机构相关业务检查导致的错误 ，60-89
						 * 有关持卡人或者发卡行（渠道）相关的问题导致的错误，90-99 预留
						 */
						if (("00").equals(respCode)) {// 成功,获取tn号
							// 返回数据为成功时
							String tn = retData.get("tn");
							String tempString = "orderId=" + orderId + "&tn=" + tn;
							String sign = getSign("10001", tempString);

							Map<String, String> jsonData = new HashMap<String, String>();
							jsonData.put("orderId", orderId);
							jsonData.put("tn", tn);
							jsonData.put("sign", sign);

							returnObject.setRetcode(0);
							returnObject.setRetmsg("成功");
							returnObject.setData(jsonData);
							String json = returnObject.toJson();
							logger.debug("[" + bkInterfaceName + "]返回给云商城参数:{}", json);

							// 写入数据库epay的epay_oder_detail表中
							Integer retInt = insertPayOrderDetail(upTranData, upExtend, dbExtend, extend);
							logger.debug("[" + bkInterfaceName + "]保存detail表状态：{}", retInt);
						} else {
							// 其他应答码为失败请排查原因或做失败处理
							logger.debug("[" + bkInterfaceName + "]获取TN失败，返回代码：" + retData.get("respCode") + "/t/t返回消息：" + retData.get("respMsg"));
							returnObject.setRetcode(-1);
							returnObject.setRetmsg(retData.get("respMsg"));
						}
					} else {
						logger.debug("[" + bkInterfaceName + "]验证签名失败");
						returnObject.setRetcode(-2);
						returnObject.setRetmsg("验证签名失败");
					}
				} else {
					// 未返回正确的http状态
					logger.debug("[" + bkInterfaceName + "]未获取到返回报文或返回http状态码非200");
					returnObject.setRetcode(-3);
					returnObject.setRetmsg("未获取到返回报文或返回http状态码非200");
				}
			} catch (Exception e) {
				logger.debug("[" + bkInterfaceName + "]错误信息：" + e);
				returnObject.setRetcode(-4);
				returnObject.setRetmsg("获取TN失败");
			}
			Map<String, Object> payMap = new HashMap<String, Object>();
			payMap.put("returnObject", returnObject);
			return payMap;
		} else {
			throw new Exception("获取支付参数为空！");
		}
	}

	@Autowired
	private IPayQueryApiSv iPayQueryApiSv;

	@Override
	public String notify(Map<String, String> params) throws Exception {
		try {
			//签名验证
			if (!SDKUtil.validate(params, SDKUtil.encoding_UTF8)) {
				logger.debug("验证签名结果[失败].");
				//验签失败，需解决验签问题
				throw new Exception("验证签名结果[失败]");
			}

			String respCode = getValue(params, "respCode");
			if (respCode.equals("00")) {
				String payCompany = "unionpay_emt";
				String orderId = getValue(params, "orderId");//(String) params.get("orderId");
				String amount = getValue(params, "settleAmt");//(String) params.get("settleAmt");
				String queryId = getValue(params, "queryId");//(String) params.get("queryId");
				String merId = getValue(params, "merId");//(String) params.get("merId");
				String reqReserved = getValue(params, "reqReserved");//(String) params.get("reqReserved");
				String reserved = getValue(params, "reserved");//(String) params.get("reserved");
				logger.debug("[unionpay notify]: reqReserved = " + reqReserved);
				logger.debug("[unionpay notify]: reserved = " + reserved);

				Map<String, Object> rd = new HashMap<String, Object>();
				rd.put("orderid", orderId);
				rd.put("payCompany", payCompany);
				List<Map<String, Object>> list = iEpayOrderDetailDao.Select(rd);
				logger.debug("[unionpay notify]: list = " + list);
				String resultUrl, interfaceName = null, sysId, orderDate;
				if ((list != null) && (list.size() > 0)) {
					Map<String, Object> orderInfo = list.get(0);
					resultUrl = getValue(orderInfo, "ResultUrl");//orderInfo.get("ResultUrl").toString();
					interfaceName = getValue(orderInfo, "payCompany");//orderInfo.get("payCompany").toString();
					sysId = getValue(orderInfo, "sysId");
					orderDate = getValue(orderInfo, "orderDate");
					logger.debug("[unionpay notify]: resultUrl={}, payCompany={}, sysId={}, orderDate={}", resultUrl, payCompany, sysId, orderDate);
				} else {
					logger.debug("订单号：" + orderId + "不存在！");
					throw new Exception("订单号：" + orderId + "不存在！");
				}

				//状态查询
				Map<String, String> queryMap = iPayQueryApiSv.queryFromUnionpay(sysId, orderId, orderDate);
				logger.debug("[unionpay notify]查询结果：" + queryMap);
				if (null == queryMap || queryMap.size() == 0) {
					logger.debug("[unionpay notify]银联支付失败，银联没有查询到订单数据");
					throw new Exception("银联支付失败，银联没有查询到订单数据");
				}
				String status = getValue(queryMap, "status");
				//"origRespCode":查询交易成功时返回00，已将“00”转换成“1”
				if ("1".equals(status)) {
					//修改数据库
					rd.put("TranSerialNo", queryId);
					rd.put("notifyDate", DateUtils.DateTimeToYYYYMMDDhhmmss(new Date()));
					rd.put("tranStat", status);
					rd.put("notifyData", ToolsUtil.mapToJson(params));
					rd.put("comment", "支付成功");
					rd.put("orderid", orderId);
					rd.put("payCompany", payCompany);
					iEpayOrderDetailDao.Update(rd);

					//推送到应用系统
					String discountAmount = "0.00";//优惠金额
					Map<String, Object> pstMap = new HashMap<String, Object>();
					pstMap.put("orderId", orderId);
					pstMap.put("discountAmount", discountAmount);
					pstMap.put("resultUrl", resultUrl);
					postPayResult(pstMap);

					//组装返回参数
					Map<String, String> pMap = new HashMap<String, String>();
					pMap.put("orderId", orderId);
					pMap.put("payCompany", payCompany);
					pMap.put("amount", amount);
					pMap.put("tranStat", status);
					pMap.put("discountAmount", discountAmount);
					pMap.put("interfaceName", interfaceName);
					String tranMapJson = ToolsUtil.mapToJson(pMap);
					String tranData = Base64Util.encodeBase64(tranMapJson, "UTF-8");
					String sign = getSign("10001", tranData);

					Map<String, Object> rParams = new HashMap<String, Object>();
					rParams.put("tranData", tranData);
					rParams.put("sign", sign);
					return HttpUtil.getResultUrl(resultUrl, rParams);
				} else {
					logger.debug("[unionpay notify]银联支付失败，订单未支付，状态：" + status);
					throw new Exception("银联支付失败，订单未支付，状态：" + status);
				}
			} else {
				throw new Exception("支付失败，respCode=" + respCode);
			}
		}catch (Exception e){
			logger.debug("[unionpay notify]银联回调通知处理出错，Error：" + e.getMessage());
			throw new Exception("银联回调通知处理出错，Error：" + e.getMessage());
		}
	}


	/**
	 * 获取截止时间（当前时间往前flag分钟）
	 * @Title: getOrderTimeoutDate
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param date
	 * @param @return  参数说明
	 * @return String    返回类型
	 * @throws
	 */
	public String getOrderTimeoutDate(Date date, Integer flag){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		date.setTime(date.getTime() + (1000 * 60) * flag);//当前时间往后flag分钟
		return dateFormat.format(date);
	}

	/**
	 * 功能：后台交易提交请求报文并接收同步应答报文<br>
	 * @param reqData 请求报文<br>
	 * @param reqUrl  请求地址<br>
	 * @param encoding<br>
	 * @return 应答http 200返回true ,其他false<br>
	 */
	public Map<String,String> post(Map<String, String> reqData, String reqUrl, String encoding) {
		Map<String, String> rspData = new HashMap<String,String>();
		LogUtil.writeLog("请求银联地址:" + reqUrl);
		//发送后台请求数据
		HttpClient hc = new HttpClient(reqUrl, 30000, 30000);
		try {
			int status = hc.send(reqData, encoding);
			if (200 == status) {
				String resultString = hc.getResult();
				if (null != resultString && !"".equals(resultString)) {
					// 将返回结果转换为map
					Map<String,String> tmpRspData  = SDKUtil.convertResultStringToMap(resultString);
					rspData.putAll(tmpRspData);
				}
			}else{
				LogUtil.writeLog("返回http状态码["+status+"]，请检查请求报文或者请求地址是否正确");
			}
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return rspData;
	}

	/**
	 * 验证签名(SHA-1摘要算法)<br>
	 * @param encoding 上送请求报文域encoding字段的值<br>
	 * @return true 通过 false 未通过<br>
	 */
	public boolean validate(Map<String, String> rspData, String encoding) {
		LogUtil.writeLog("验签处理开始");
		if (SDKUtil.isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		String stringSign = rspData.get(SDKConstants.param_signature);
		LogUtil.writeLog("stringSign：["+stringSign+"]");
		// 从返回报文中获取certId ，然后去证书静态Map中查询对应验签证书对象
		String certId = rspData.get(SDKConstants.param_certId);

		LogUtil.writeLog("对返回报文串验签使用的验签公钥序列号：["+certId+"]");

		// 将Map信息转换成key1=value1&key2=value2的形式
		String stringData = SDKUtil.coverMap2String(rspData);

		LogUtil.writeLog("待验签返回报文串：["+stringData+"]");
		try {
			// 验证签名需要用银联发给商户的公钥证书.
			PublicKey publicKey = CertUtil.getValidateKey(certId);
			LogUtil.writeLog("publicKey：["+publicKey+"]");
			byte[] signData = SecureUtil.base64Decode(stringSign.getBytes(encoding));
			byte[] srcData = SecureUtil.sha1X16(stringData, encoding);
			return SecureUtil.validateSignBySoft(publicKey, signData, srcData);
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return false;
	}
}
