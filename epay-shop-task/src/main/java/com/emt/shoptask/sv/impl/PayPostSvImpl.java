package com.emt.shoptask.sv.impl;

import com.emt.shoppay.util.*;
import com.emt.shoppay.sv.inter.IEpayOrderDetailSv;
import com.emt.shoppay.util.httpclient.HttpsClientUtil;
import com.emt.shoppay.util.json.JSONObject;
import com.emt.shoppay.util.sms.SendSMS;
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
	private IEpayOrderDetailSv iEpayOrderDetailSv;

	/**
	 * 支付成功以后，调用该方法，post支付内容到应用服务器
	 *
	 * @param  map
	 */
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
		String sign = ValidataUtil.getValidataString("10001", tranData);

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
			logger.error("[postPayResult]订单：" + orderId + "，请求远程回调时发生错误，错误原因：" + e.getMessage());
			rd.clear();
			rd.put("IsPost", Integer.valueOf(0));
			rd.put("Times", Integer.valueOf(1));
			rd.put("IsSend", Integer.valueOf(0));
			rd.put("orderid", orderId);
			rd.put("payCompany", payCompany);
			iEpayOrderDetailSv.Update(rd);
		}
	}

	/**
	 * 查询已支付且未推送的数据进行推送
	 */
	@Override
	public void alreadyPayPost(){
		logger.debug("开始执行“已支付且未推送的数据进行推送到应用系统”任务.");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<Map<String, Object>> payRecord = null;
		//查询出所有已经支付，但是未post回去的支付记录
		try {
			paramMap.put("tranStat", 1);
			paramMap.put("Emt_sys_id", "400001");
			paramMap.put("IsPost", 0);
			paramMap.put("IsSend", 0);
			payRecord = iEpayOrderDetailSv.Select(paramMap);
		} catch (Exception e) {
			logger.debug("查询已经支付但是未post回去的支付记录出错，错误原因：" + e.getMessage());
			return ;
		}

		if(payRecord == null || payRecord.size() == 0){
			logger.debug("没有查询到已支付但未POST的记录。");
			return ;
		}

		int timesInt = 0;
		for (Map<String, Object> payDetail : payRecord) {
			String orderId = payDetail.get("orderid").toString();
			String discountAmount = "0.00";
			String payCompany = payDetail.get("payCompany").toString();
			String amount = payDetail.get("amount").toString();
			String tranStat = payDetail.get("tranStat").toString();
			String times = payDetail.get("Times").toString();
			String resultUrl = payDetail.get("ResultUrl") + "";
			try {
				String hostAddr = resultUrl;
				timesInt = TextUtils.isEmpty(times) ? 0 : Integer.parseInt(times);
				if (timesInt == 20) {
					logger.debug("[alreadyPayPost] 已经推送20次，将发送短信告知管理员");
					//发送短信
					String smsContent = "订单号：" + orderId + "已完成支付，将该订单推送到应用系统时失败，次数已达20次！！！";
					SendSMS.sendSmsCode("18786669608",smsContent,"127.0.0.1");

					//修改为已推送
					paramMap.clear();
					paramMap.put("IsSend", Integer.valueOf(1));
					paramMap.put("orderid", orderId);
					paramMap.put("payCompany", payCompany);
					iEpayOrderDetailSv.Update(paramMap);
				} else {
					Map<String, String> tranMap = new HashMap<String, String>();
					tranMap.put("orderId", orderId);
					tranMap.put("payCompany", payCompany);
					tranMap.put("amount", amount);
					tranMap.put("tranStat", tranStat);
					tranMap.put("discountAmount", discountAmount);
					tranMap.put("interfaceName", payCompany);
					tranMap.put("asyn", "1");

					String tranMapJson = ToolsUtil.mapToJson(tranMap);
					String tranData = Base64Util.encodeBase64(tranMapJson, "UTF-8");
					String sign = ValidataUtil.getValidataString("10001", tranData);

					logger.debug("[alreadyPayPost] 请求远程服务器回调地址：" + hostAddr);
					logger.debug("[alreadyPayPost] 参数 tranData：" + tranData);
					logger.debug("[alreadyPayPost] 参数 sign：" + sign);
					String result = null;
					boolean isHttps = Boolean.valueOf(hostAddr.toLowerCase().contains("https://"));
					if (isHttps){
						StringBuffer url = new StringBuffer();
						url.append(hostAddr).append(hostAddr.contains("?") ? "&" : "?").append("tranData=").append(tranData).append("&sign=").append(sign);
						result = HttpsClientUtil.doGet(url.toString(), "utf-8");
					} else {
						Map<String, String> mmap = new HashMap<String, String>();
						mmap.put("tranData", tranData);
						mmap.put("sign", sign);
						result = HttpUtil.doGet(hostAddr, mmap);
					}
					logger.debug("[alreadyPayPost] 请求远程服务器回调返回result={}", result);
					if (!TextUtils.isEmpty(result)) {
						JSONObject jObject = new JSONObject(result);
						Boolean state = Boolean.valueOf(jObject.getBoolean("state"));
						Integer resultCode = Integer.valueOf(jObject.getInt("code"));
						logger.debug("[alreadyPayPost] 解析返回json，state={}，resultCode={}", state, resultCode);
						if (state && resultCode == 0) {
							paramMap.clear();
							paramMap.put("IsPost", Integer.valueOf(1));
							paramMap.put("Times", Integer.valueOf(timesInt + 1));
							paramMap.put("IsSend", Integer.valueOf(0));
							paramMap.put("orderid", orderId);
							paramMap.put("payCompany", payCompany);
							int ret = iEpayOrderDetailSv.Update(paramMap);
							logger.debug("[alreadyPayPost] iEpayOrderDetailSv.Update：" + ret);
						} else {
							paramMap.clear();
							paramMap.put("IsPost", Integer.valueOf(0));
							paramMap.put("Times", Integer.valueOf(timesInt + 1));
							paramMap.put("IsSend", Integer.valueOf(0));
							paramMap.put("orderid", orderId);
							paramMap.put("payCompany", payCompany);
							int ret = iEpayOrderDetailSv.Update(paramMap);
							logger.debug("[alreadyPayPost] iEpayOrderDetailSv.Update：" + ret);
						}
					} else {
						paramMap.clear();
						paramMap.put("IsPost", Integer.valueOf(0));
						paramMap.put("Times", Integer.valueOf(timesInt + 1));
						paramMap.put("IsSend", Integer.valueOf(0));
						paramMap.put("orderid", orderId);
						paramMap.put("payCompany", payCompany);
						int ret = iEpayOrderDetailSv.Update(paramMap);
						logger.debug("[alreadyPayPost] iEpayOrderDetailSv.Update：" + ret);
					}
				}
			} catch (Exception e) {
				logger.debug("[alreadyPayPost] 订单：" + orderId + "，请求远程回调时发生错误，错误原因：" + e.getMessage());
				paramMap.clear();
				paramMap.put("IsPost", Integer.valueOf(0));
				paramMap.put("Times", Integer.valueOf(timesInt + 1));
				paramMap.put("IsSend", Integer.valueOf(0));
				paramMap.put("orderid", orderId);
				paramMap.put("payCompany", payCompany);
				iEpayOrderDetailSv.Update(paramMap);
			}
		}
	}
}
