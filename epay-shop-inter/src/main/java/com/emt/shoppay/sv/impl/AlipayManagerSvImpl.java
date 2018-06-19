package com.emt.shoppay.sv.impl;

import com.emt.shoppay.dao.inter.IEpayParaConfigDao;
import com.emt.shoppay.pojo.AlipayConfig;
import com.emt.shoppay.pojo.aliQuery.ReturnCode;
import com.emt.shoppay.sv.inter.IAlipayManagerSv;
import com.emt.shoppay.sv.inter.IPayQueryApiSv;
import com.emt.shoppay.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 支付宝支付管理
* @ClassName: AlipayManagerSvImpl 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author huangdafei
* @date 2017年5月5日 下午2:41:07 
*
 */
@Service
public class AlipayManagerSvImpl extends BaseSvImpl implements IAlipayManagerSv {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static String service_pc = "create_direct_pay_by_user";
	private static String service_wap = "alipay.wap.create.direct.pay.by.user";

	@Autowired
	private IPayQueryApiSv iPayQueryApiSv;

	@Override
	public Map<String, Object> alipayWap(Map<String, String> upTranData, Map<String, Object> upExtend) throws Exception {
		logger.debug("[alipayWap] 支付开始...");
		String orderId = getValue(upTranData, "orderId");
		String subject = getValue(upTranData, "subject");
		String fee = getValue(upTranData, "totalFee");
		String resultUrl = getValue(upTranData, "notifyUrl");
		String ip = getValue(upTranData, "ip");
		String tradeType = getValue(upTranData, "tradeType");
		String appType = getValue(upTranData, "appType");
		
		if ("".equals(orderId) || "".equals(fee) || "".equals(resultUrl) || "".equals(subject)) {
			logger.debug("[alipayWap] 缺少必要的参数！");
			throw new Exception("缺少必要的参数！");
		}
		
		String interfaceName = getValue(upExtend, "interfaceName", null);
		String interfaceVersion = getValue(upExtend, "interfaceVersion", null);
		String qid = getValue(upExtend, "qid", null);
		String clientType = getValue(upExtend, "clientType", null);
		String merReference = getValue(upExtend, "merReference", null);
		String busiid = getValue(upExtend, "busiid", null);
		String sysId = getValue(upExtend, "sysId", null);		


		String bkInterfaceVersion = AlipayConfig.bkInterfaceVersion;//getValue(dbExtend, "interfaceName");
		String reqUrl = AlipayConfig.reqUrl;//getValue(dbExtend, "req_url");
		String partner = AlipayConfig.partner;//getValue(dbExtend, "partner");
		String key = AlipayConfig.key;//getValue(dbExtend, "key");
		String input_charset = AlipayConfig.inputCharset;//getValue(dbExtend, "input_charset");
		String sign_type = AlipayConfig.signType;//getValue(dbExtend, "sign_type");
		String payment_type = AlipayConfig.paymentType;//getValue(dbExtend, "payment_type");
		String return_url = AlipayConfig.returnUrlWAP;//getValue(dbExtend, "return_url");
		String notify_url = AlipayConfig.notifyUrlWAP;//getValue(dbExtend, "notify_url");
		String error_url = AlipayConfig.errorUrl;//getValue(dbExtend, "error_url");
//		String timeOut = AlipayConfig.timeOut;//getValue(dbExtend, "timeOut");
		if ("".equals(reqUrl) || "".equals(partner) || "".equals(key) || "".equals(input_charset)
			|| "".equals(sign_type) || "".equals(payment_type) || "".equals(return_url) || "".equals(notify_url) || "".equals(error_url)) {
			logger.debug("[alipayWap] 获取支付参数为空");
			throw new Exception("获取支付参数为空");
		}

		String globalUrl = Global.getConfig("epay.notify.url");
		return_url = globalUrl + return_url;
		notify_url = globalUrl + notify_url;
		error_url = globalUrl + error_url;
		reqUrl += reqUrl.contains("?") ? "_input_charset=" + input_charset : "?_input_charset=" + input_charset;

		Double totalFee = Double.parseDouble(fee);
		totalFee = totalFee / 100f;
		// 构建sign
		Map<String, String> map = new HashMap<String, String>();
		DecimalFormat df = new DecimalFormat("######0.00");
		map.put("service", service_wap);
		map.put("partner", partner);
		map.put("_input_charset", input_charset);
		map.put("out_trade_no", orderId);
		map.put("subject", subject);
		map.put("total_fee", df.format(totalFee));
		map.put("seller_id", partner);
		map.put("payment_type", payment_type);
		map.put("return_url", return_url);
		map.put("notify_url", notify_url);
		map.put("error_notify_url", error_url);

		String showUrl = notify_url;
		String temp = "amount=" + totalFee + "&orderId=" + orderId + "&status=5";
		String sign = ValidataUtil.getValidataString("10001", temp);
		if(showUrl.contains("?")){
			showUrl = resultUrl + "&orderid=" + orderId + "&amount=" + totalFee + "&status=5&sign=" + sign;
		} else {
			showUrl = resultUrl + "?orderid=" + orderId + "&amount=" + totalFee + "&status=5&sign=" + sign;
		}
		map.put("show_url", showUrl);
		List<String> list = new ArrayList<>();
		list.add("sign");
		list.add("sign_type");
		map = ToolsUtil.paraFilter(map, list, true);
		String prestr = alipayLinkString(map);
		String mySign = alipaySign(prestr, key);
		map.put("sign_type", sign_type);
		map.put("sign", mySign);

		String orderDate = DateUtils.DateTimeToYYYYMMDDhhmmss();
		Map<String, String> extend = new HashMap<String, String>();
		extend.put("interfaceName", interfaceName);
		extend.put("interfaceVersion", bkInterfaceVersion);
		extend.put("merUrl", notify_url);
		extend.put("merVAR", "pay.cmaotai.com");
		extend.put("orderDate", orderDate);
		extend.put("buildData", ToolsUtil.mapToJson(map));
		extend.put("shopCode", partner);

		// 写入数据库epay的epay_oder_detail表中
		logger.debug("[alipayWap]保存数据，调用insertPayOrderDetail()");
		Integer retInt = insertPayOrderDetail(upTranData, upExtend, extend);
		logger.debug("[alipayWap]保存detail表状态：{}", retInt);

		logger.debug("[alipayWap]发起支付请求......");
		Map<String, Object> payMap = new HashMap<String, Object>();
		payMap.put("submitUrl", reqUrl);
		payMap.put("service", service_wap);
		payMap.put("partner", partner);
		payMap.put("_input_charset", input_charset);
		payMap.put("sign_type", sign_type);
		payMap.put("sign", mySign);
		payMap.put("out_trade_no", orderId);
		payMap.put("subject", subject);
		payMap.put("total_fee", totalFee);
		payMap.put("seller_id", partner);
		payMap.put("payment_type", payment_type);
		payMap.put("return_url", return_url);
		payMap.put("notify_url", notify_url);
		payMap.put("error_notify_url", error_url);
		payMap.put("rn_check", "T"); // 是否发起实名校验 T：发起实名校验； F：不发起实名校验。
		payMap.put("show_url", showUrl);
//		payMap.put("it_b_pay", timeOut + "m");
		return payMap;
	}

	@Override
	public Map<String, Object> alipayPc(Map<String, String> upTranData, Map<String, Object> upExtend) throws Exception {
		logger.debug("[alipayPc] 支付开始...");
		String orderId = getValue(upTranData, "orderId");
		String subject = getValue(upTranData, "subject");
		String fee = getValue(upTranData, "totalFee");
		String resultUrl = getValue(upTranData, "notifyUrl");
		String ip = getValue(upTranData, "ip");
		String tradeType = getValue(upTranData, "tradeType");
		String appType = getValue(upTranData, "appType");
		String interfaceName = getValue(upExtend, "interfaceName", null);
		
		if ("".equals(orderId) || "".equals(fee) || "".equals(resultUrl) || "".equals(subject)) {
			logger.debug("[alipayPc] 缺少必要的参数！");
			throw new Exception("缺少必要的参数！");
		}

		String bkInterfaceVersion = AlipayConfig.bkInterfaceVersion;//getValue(dbExtend, "interfaceName");
		String reqUrl = AlipayConfig.reqUrl;//getValue(dbExtend, "req_url");
		String partner = AlipayConfig.partner;//getValue(dbExtend, "partner");
		String seller_email = AlipayConfig.sellerEmail;//getValue(dbExtend, "seller_email");
		String key = AlipayConfig.key;//getValue(dbExtend, "key");
		String input_charset = AlipayConfig.inputCharset;//getValue(dbExtend, "input_charset");
		String sign_type = AlipayConfig.signType;//getValue(dbExtend, "sign_type");
		String payment_type = AlipayConfig.paymentType;//getValue(dbExtend, "payment_type");
		String return_url = AlipayConfig.returnUrlPC;//getValue(dbExtend, "return_url");
		String notify_url = AlipayConfig.notifyUrlPC;//getValue(dbExtend, "notify_url");
		String error_url = AlipayConfig.errorUrl;//getValue(dbExtend, "error_url");
		String timeOut = AlipayConfig.timeOut;//getValue(dbExtend, "timeOut");
		if ("".equals(reqUrl) || "".equals(partner) || "".equals(key) || "".equals(input_charset)
			|| "".equals(sign_type) || "".equals(payment_type) || "".equals(return_url) || "".equals(notify_url) || "".equals(error_url)) {
			logger.debug("[alipayPc] 获取支付参数为空");
			throw new Exception("获取支付参数为空");
		}

		String globalUrl = Global.getConfig("epay.notify.url");
		return_url = globalUrl + return_url;
		notify_url = globalUrl + notify_url;
		error_url = globalUrl + error_url;
		reqUrl += reqUrl.contains("?") ? "_input_charset=" + input_charset : "?_input_charset=" + input_charset;

		Double totalFee = Double.parseDouble(fee);
		totalFee = totalFee / 100f;
		// 构建sign
		Map<String, String> map = new HashMap<String, String>();
		DecimalFormat df = new DecimalFormat("######0.00");
		map.put("service", service_pc);
		map.put("partner", partner);
		map.put("seller_email", seller_email);
		map.put("_input_charset", input_charset);
		map.put("payment_type", payment_type);
		map.put("out_trade_no", orderId);
		map.put("subject", subject);
		map.put("total_fee", df.format(totalFee));
		map.put("return_url", return_url);
		map.put("error_notify_url", error_url);
		map.put("notify_url", notify_url);
//		map.put("seller_id", partner);

		map = alipayParaFilter(map);
		String prestr = alipayLinkString(map);
		String mySign = alipaySign(prestr, key);

		String orderDate = DateUtils.DateTimeToYYYYMMDDhhmmss();
		Map<String, String> extend = new HashMap<String, String>();
		extend.put("interfaceName", interfaceName);
		extend.put("interfaceVersion", bkInterfaceVersion);
		extend.put("merUrl", notify_url);
		extend.put("merVAR", "pay.cmaotai.com");
		extend.put("orderDate", orderDate);
		extend.put("buildData", ToolsUtil.mapToJson(map));
		extend.put("shopCode", partner);

		// 写入数据库epay的epay_oder_detail表中
		logger.debug("[alipayPc]保存数据，调用insertPayOrderDetail()");
		Integer retInt = insertPayOrderDetail(upTranData, upExtend, extend);
		logger.debug("[alipayPc]保存detail表状态：" + retInt);

		logger.debug("[alipayPc]发起支付请求......");
		Map<String, Object> payMap = new HashMap<String, Object>();
		payMap.put("submitUrl", reqUrl);
		payMap.put("service", service_pc);
		payMap.put("_input_charset", input_charset);
		payMap.put("out_trade_no", orderId);
		payMap.put("partner", partner);
		payMap.put("payment_type", payment_type);
		payMap.put("seller_email", seller_email);
		payMap.put("sign_type", sign_type);
		payMap.put("sign", mySign);
		payMap.put("subject", subject);
		payMap.put("total_fee", totalFee);
		payMap.put("return_url", return_url);
		payMap.put("error_notify_url", error_url);
		payMap.put("notify_url", notify_url);
		payMap.put("it_b_pay", timeOut + "m");
		return payMap;
	}

	@Override
	public String notifyFront(Map<String, String> map, String payCompany) throws Exception {
		try {
			String sign = getValue(map, "sign");
			String status = getValue(map, "trade_status");
			String notify_time = getValue(map, "notify_time");
			String TranSerialNo = getValue(map, "trade_no");
			String isSucess = getValue(map, "is_success");

			//1.查询数据库订单信息
			String orderId = getValue(map, "out_trade_no");
			String amount = getValue(map, "total_fee");
			Map<String, Object> rd2 = new HashMap<String, Object>();
			rd2.put("orderid", orderId);
			rd2.put("payCompany", payCompany);
			List<Map<String, Object>> list = iEpayOrderDetailDao.Select(rd2);
			String resultUrl = "", interfaceName = "", sysId = "";
			if (list != null && list.size() > 0) {
				Map<String, Object> map2 = list.get(0);
				resultUrl = MapUtils.getString(map2, "ResultUrl");//(String) map2.get("ResultUrl");
				amount = MapUtils.getString(map2, "amount");//map2.get("amount").toString();
				interfaceName = MapUtils.getString(map2, "payCompany");//map2.get("payCompany").toString();
				sysId = MapUtils.getString(map2, "Emt_sys_id");
			} else {
				logger.debug("[notifyFront] 订单号：" + orderId + "不存在！");
				throw new Exception("订单号：" + orderId + "不存在！");
			}


			String key = AlipayConfig.key;

			//2.验签
			Map<String, String> signMap = alipayParaFilter(map);
			String prestr = alipayLinkString(signMap);
			String mySign = alipaySign(prestr, key);
			// 校验数据是否被篡改
			if (!mySign.equals(sign)) {
				logger.debug("[notifyFront]支付宝支付回调验签失败");
				throw new Exception("[notifyFront]支付宝支付回调验签失败");
			}

			//3.查询订单支付状态
			Map<String, String> queryMap = iPayQueryApiSv.queryFromAlipay(sysId, orderId);
			logger.debug("[notifyFront]查询结果：" + queryMap);
			if (null == queryMap || queryMap.size() == 0) {
				logger.debug("[notifyFront]支付宝支付失败，支付宝没有查询到订单数据");
				throw new Exception("支付宝支付失败，支付宝没有查询到订单数据");
			}
			String qStatus = MapUtils.getString(queryMap, "status");
			//trade_status=TRADE_SUCCESS：成功
			if ("TRADE_SUCCESS".equals(status) && "TRADE_SUCCESS".equals(qStatus)) {
				// 4.更新Epay的数据
				notify_time = DateUtils.DateTimeToYYYYMMDDhhmmss(DateUtils.StringToDateTime(notify_time));
				Map<String, Object> rd = new HashMap<String, Object>();
				rd.put("TranSerialNo", TranSerialNo);
				rd.put("notifyDate", notify_time);
				rd.put("tranStat", ReturnCode.getCode(status));
				rd.put("notifyData", ToolsUtil.mapToJson(map));
				rd.put("comment", "支付成功");
				rd.put("orderid", orderId);
				rd.put("payCompany", payCompany);
				iEpayOrderDetailDao.Update(rd);

				//5.推送到应用系统
				Map<String, Object> pstMap = new HashMap<String, Object>();
				pstMap.put("orderId", orderId);
				pstMap.put("discountAmount", Integer.valueOf(0));
				pstMap.put("resultUrl", resultUrl);
				postPayResult(pstMap);

				//6.组装返回参数
				Map<String, String> pMap = new HashMap<String, String>();
				pMap.put("orderId", orderId);
				pMap.put("payCompany", payCompany);
				pMap.put("amount", amount);
				pMap.put("tranStat", ReturnCode.getCode(status));
				pMap.put("discountAmount", "0.00");
				pMap.put("interfaceName", interfaceName);

				String tranMapJson = ToolsUtil.mapToJson(pMap);
				String tranData = Base64Util.encodeBase64(tranMapJson, "UTF-8");
				sign = ValidataUtil.getValidataString("10001", tranData);

				Map<String, Object> param = new HashMap<String, Object>();
				param.put("tranData", tranData);
				param.put("sign", sign);
				return HttpUtil.getResultUrl(resultUrl, param);
			} else {
				logger.debug("[notifyFront]异步回调，订单状态未支付，status={}，qStatus={}", status, qStatus);
				throw new Exception("异步回调，订单状态未支付成功，status=" + status + "，qStatus=" + qStatus);
			}
		} catch (Exception e) {
			logger.debug("[notifyFront]支付宝同步通知处理出错，Error：" + e.getMessage());
			throw new Exception("支付宝同步通知处理出错，Error：" + e.getMessage());
		}
	}

	@Override
	public String notifyBack(Map<String, String> map, String payCompany) throws Exception {
		try {
			String sign = getValue(map, "sign");
			String status = getValue(map, "trade_status");
			String notify_time = getValue(map, "notify_time");
			String TranSerialNo = getValue(map, "trade_no");
			String isSucess = getValue(map, "is_success");

			//1.查询数据库订单信息
			String orderId = getValue(map, "out_trade_no");
			String amount = getValue(map, "total_fee");
			Map<String, Object> rd2 = new HashMap<String, Object>();
			rd2.put("orderid", orderId);
			rd2.put("payCompany", payCompany);
			List<Map<String, Object>> list = iEpayOrderDetailDao.Select(rd2);
			String resultUrl = "", interfaceName= "" , sysId = "";
			if (list != null && list.size() > 0) {
				Map<String, Object> map2 = list.get(0);
				resultUrl = MapUtils.getString(map2, "ResultUrl");//(String) map2.get("ResultUrl");
				interfaceName = MapUtils.getString(map2, "payCompany");
				sysId = MapUtils.getString(map2, "Emt_sys_id");
			} else {
				logger.debug("订单号：" + orderId + "不存在！");
				throw new Exception("订单号：" + orderId + "不存在！");
			}


			String key = AlipayConfig.key;
			//2.验签
			Map<String, String> signMap = alipayParaFilter(map);
			String prestr = alipayLinkString(signMap);
			String mySign = alipaySign(prestr, key);
			// 校验数据是否被篡改
			if (!mySign.equals(sign)) {
				logger.debug("[notifyBack]支付宝支付回调验签失败");
				throw new Exception("[notifyBack]支付宝支付回调验签失败");
			}

			//3.查询订单支付状态
			Map<String, String> queryMap = iPayQueryApiSv.queryFromAlipay(sysId, orderId);
			logger.debug("[notifyBack]查询结果：" + queryMap);
			if (null == queryMap || queryMap.size() == 0) {
				logger.debug("[notifyBack]支付宝回调失败，支付宝没有查询到订单数据");
				throw new Exception("支付宝回调失败，支付宝没有查询到订单数据");
			}
			String qStatus = MapUtils.getString(queryMap, "status");
			//trade_status=TRADE_SUCCESS：成功
			if ("TRADE_SUCCESS".equals(status) && "TRADE_SUCCESS".equals(qStatus)) {
				// 4.更新Epay的数据
				notify_time = DateUtils.DateTimeToYYYYMMDDhhmmss(DateUtils.StringToDateTime(notify_time));
				Map<String, Object> rd = new HashMap<String, Object>();
				rd.put("TranSerialNo", TranSerialNo);
				rd.put("notifyDate", notify_time);
				rd.put("tranStat", ReturnCode.getCode(status));
				rd.put("notifyData", ToolsUtil.mapToJson(map));
				rd.put("comment", "支付成功");
				rd.put("orderid", orderId);
				rd.put("payCompany", payCompany);
				iEpayOrderDetailDao.Update(rd);

				//5.通知应用系统
				Map<String, Object> pstMap = new HashMap<String, Object>();
				pstMap.put("orderId", orderId);
				pstMap.put("discountAmount", Integer.valueOf(0));
				pstMap.put("resultUrl", resultUrl);
				postPayResult(pstMap);

				//6.组装返回参数
				Map<String, String> pMap = new HashMap<String, String>();
				pMap.put("orderId", orderId);
				pMap.put("payCompany", payCompany);
				pMap.put("amount", amount);
				pMap.put("tranStat", ReturnCode.getCode(status));
				pMap.put("discountAmount", "0.00");
				pMap.put("interfaceName", payCompany);
				pMap.put("asyn", "1");//支付宝回调不需要跳转，故使用异步请求

				String tranMapJson = ToolsUtil.mapToJson(pMap);
				String tranData = Base64Util.encodeBase64(tranMapJson, "UTF-8");
				String msign = ValidataUtil.getValidataString("10001", tranData);

				Map<String, Object> params = new HashMap<String, Object>();
				params.put("tranData", tranData);
				params.put("sign", msign);
				return HttpUtil.getResultUrl(resultUrl, params);
			} else {
				logger.debug("[notifyBack]异步回调，订单状态未支付，status={}，qStatus={}", status, qStatus);
				throw new Exception("异步回调，订单状态未支付成功，status=" + status + "，qStatus=" + qStatus);
			}
		} catch (Exception e) {
			logger.debug("[notifyBack]付宝异步通知处理出错，Error：" + e.getMessage());
			throw new Exception("支付宝异步通知处理出错，Error：" + e.getMessage());
		}
	}

	/**
	 * 支付宝支付构建支付数据
	 * @param params
	 * @return
	 */
	public String alipayLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i).trim();
			String value = params.get(key).trim();

			if (i == keys.size() - 1) {
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr;
	}

	/**
	 * 支付宝支付签名数据构建
	 * @param sArray
	 * @return
	 */
	public Map<String, String> alipayParaFilter(Map<String, String> sArray) {
		Map<String, String> result = new HashMap<String, String>();
		if (sArray == null || sArray.size() <= 0) {
			return result;
		}
		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value == null || value.equals("")
					|| key.equalsIgnoreCase("sign")
					|| key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			result.put(key, value);
		}
		return result;
	}

	/**
	 * 支付宝支付生成签名
	 * @param text
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String alipaySign(String text, String key) throws Exception {
		text = text + key;
		return DigestUtils.md5Hex(text.getBytes("UTF-8"));
	}
}
