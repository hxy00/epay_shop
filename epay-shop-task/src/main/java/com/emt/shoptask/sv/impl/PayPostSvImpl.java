package com.emt.shoptask.sv.impl;

import com.emt.shoppay.sv.inter.IEpayOrderDetailSv;
import com.emt.shoppay.sv.inter.IValidataSv;
import com.emt.shoppay.util.Base64Util;
import com.emt.shoppay.util.HttpUtil;
import com.emt.shoppay.util.ToolsUtil;
import com.emt.shoppay.util.httpclient.HttpsClientUtil;
import com.emt.shoppay.util.json.JSONObject;
import com.emt.shoptask.sv.inter.IPayPostSv;
import org.apache.commons.collections.MapUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayPostSvImpl implements IPayPostSv {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IValidataSv iValidataSv;

	@Autowired
	private IEpayOrderDetailSv iEpayOrderDetailSv;


	@Override
	public void postPayResult(Map<String, Object> map) throws Exception {
		String orderId = map.get("orderId").toString();
		String discountAmount = map.get("discountAmount").toString();
		String resultUrl = map.get("resultUrl").toString();

		Map<String, Object> rd = new HashMap<String, Object>();
		rd.put("orderid", orderId);
		rd.put("tranStat", "1");
		List<Map<String, Object>> list = iEpayOrderDetailSv.Select(rd);
		if (list == null || list.size() == 0) {
			logger.debug("[postPayResult] 订单号：" + orderId + "，未查询到支付记录，未能更新IsPost、Times、IsSend字段！");
			throw new Exception("[postPayResult] 订单号：" + orderId + "，未查询到支付记录，未能更新IsPost、Times、IsSend字段！");
		}

		Map<String, Object> lstMap = list.get(0);
		String payCompany = MapUtils.getString(lstMap, "payCompany");// lstMap.get("payCompany").toString();
		String amount = MapUtils.getString(lstMap, "amount");// lstMap.get("amount").toString();
		String tranStat = MapUtils.getString(lstMap, "tranStat");// lstMap.get("tranStat").toString();

		Map<String, String> pMap = new HashMap<String, String>();
		pMap.put("orderId", orderId);
		pMap.put("payCompany", payCompany);
		pMap.put("amount", amount);
		pMap.put("tranStat", tranStat);
		pMap.put("discountAmount", discountAmount);
		pMap.put("interfaceName", payCompany);
		pMap.put("asyn", "1");

		String tranMapJson = ToolsUtil.mapToJson(pMap);
		String tranData = Base64Util.encodeBase64(tranMapJson, "UTF-8");
		String sign = iValidataSv.getValidataString("10001", tranData);

		logger.debug("[postPayResult] 请求远程服务器回调地址：" + resultUrl);
		logger.debug("[postPayResult] 参数 tranData：" + tranData);
		logger.debug("[postPayResult] 参数 sign：" + sign);
		String result = null;
		try {
			Boolean isHttps = Boolean.valueOf(resultUrl.toLowerCase().contains("https://"));
			if (isHttps) {
				StringBuffer url = new StringBuffer();
				url.append(resultUrl);
				url.append(resultUrl.contains("?") ? "&" : "?");
				url.append("tranData=").append(tranData);
				url.append("&sign=").append(sign);
				result = HttpsClientUtil.doGet(url.toString(), "utf-8");
			} else {
				Map<String, String> mmap = new HashMap<String, String>();
				mmap.put("tranData", tranData);
				mmap.put("sign", sign);
				result = HttpUtil.doGet(resultUrl, mmap);
			}
		} catch (Exception e) {
			logger.error("[postPayResult] 请求远程服务器失败！订单号：{}，异常：{}", orderId, e);
		}

		logger.debug("[postPayResult] 订单orderId={}，请求远程服务器回调返回json={}", orderId, result);
		try {
			if (!TextUtils.isEmpty(result)) {
				JSONObject jObject = new JSONObject(result);
				Boolean state = Boolean.valueOf(jObject.getBoolean("state"));
				Integer resultCode = Integer.valueOf(jObject.getInt("code"));
				logger.debug("[postPayResult] 解析返回json，state={}，resultCode={}", state, resultCode);
				rd.clear();
				if (state && resultCode == 0) {
					rd.put("IsPost", Integer.valueOf(1));
					rd.put("Times", Integer.valueOf(1));
					rd.put("IsSend", Integer.valueOf(0));
					rd.put("orderid", orderId);
					rd.put("payCompany", payCompany);
					iEpayOrderDetailSv.Update(rd);
					logger.debug("[postPayResult] iEpayOrderDetailSv.Update：success");
				} else {
					rd.put("IsPost", Integer.valueOf(0));
					rd.put("Times", Integer.valueOf(1));
					rd.put("IsSend", Integer.valueOf(0));
					rd.put("orderid", orderId);
					rd.put("payCompany", payCompany);
					iEpayOrderDetailSv.Update(rd);
					logger.debug("[postPayResult] iEpayOrderDetailSv.Update：success");
				}
			} else {
				rd.clear();
				rd.put("IsPost", Integer.valueOf(0));
				rd.put("Times", Integer.valueOf(1));
				rd.put("IsSend", Integer.valueOf(0));
				rd.put("orderid", orderId);
				rd.put("payCompany", payCompany);
				iEpayOrderDetailSv.Update(rd);
				logger.debug("[postPayResult] iEpayOrderDetailSv.Update：success");
			}
		} catch (Exception e) {
			logger.error("订单：" + orderId + "，请求远程回调时发生错误，错误原因：" + e.getMessage());
			rd.clear();
			rd.put("IsPost", Integer.valueOf(0));
			rd.put("Times", Integer.valueOf(1));
			rd.put("IsSend", Integer.valueOf(0));
			rd.put("orderid", orderId);
			rd.put("payCompany", payCompany);
			iEpayOrderDetailSv.Update(rd);
		}
	}

}
