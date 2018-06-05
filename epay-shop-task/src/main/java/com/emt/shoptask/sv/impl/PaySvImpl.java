package com.emt.shoptask.sv.impl;

import com.emt.shoppay.sv.inter.IEpayOrderDetailSv;
import com.emt.shoppay.sv.inter.IValidataSv;
import com.emt.shoppay.util.Base64Util;
import com.emt.shoppay.util.Global;
import com.emt.shoppay.util.HttpUtil;
import com.emt.shoppay.util.ToolsUtil;
import com.emt.shoppay.util.httpclient.HttpsClientUtil;
import com.emt.shoppay.util.json.JSONObject;
import com.emt.shoppay.util.sms.SendSMS;
import com.emt.shoptask.sv.inter.IPaySv;
import com.emt.shoptask.sv.inter.IPayQuerySv;
import org.apache.commons.collections.MapUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业,处理:
 * 1.已支付未推送
 * 2.未支付订单
 *  * @author Mr.Huang
 */
@Service
public class PaySvImpl implements IPaySv {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IPayQuerySv iPayQuerySv;

	@Autowired
	private IEpayOrderDetailSv iEpayOrderDetailSv;

	@Autowired
	private IValidataSv validataSv;

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
				String hostAddr = Global.getConfig("epay.pay.success.url");
				if (resultUrl.contains("/Order/pay/payOrderSuccess")) {//大額支付的
					hostAddr = Global.getConfig("epay.pay.success.url");
				} else {
					hostAddr = resultUrl;
				}

				timesInt = TextUtils.isEmpty(times) ? 0 : Integer.parseInt(times);
				if (timesInt == 20) {
					logger.debug("已经推送20次，将发送短信告知管理员");
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
					String sign = validataSv.getValidataString("10001", tranData);

					logger.debug("[postPayResult] 请求远程服务器回调地址：" + hostAddr);
					logger.debug("[postPayResult] 参数 tranData：" + tranData);
					logger.debug("[postPayResult] 参数 sign：" + sign);
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
					logger.debug("请求远程服务器回调返回result={}", result);
					if (!TextUtils.isEmpty(result)) {
						JSONObject jObject = new JSONObject(result);
						Boolean state = Boolean.valueOf(jObject.getBoolean("state"));
						Integer resultCode = Integer.valueOf(jObject.getInt("code"));
						logger.debug("解析返回json，state={}，resultCode={}", state, resultCode);
						if (state && resultCode == 0) {
							paramMap.clear();
							paramMap.put("IsPost", Integer.valueOf(1));
							paramMap.put("Times", Integer.valueOf(timesInt + 1));
							paramMap.put("IsSend", Integer.valueOf(0));
							paramMap.put("orderid", orderId);
							paramMap.put("payCompany", payCompany);
							int ret = iEpayOrderDetailSv.Update(paramMap);
							logger.debug("[postPayResult] iEpayOrderDetailSv.Update：" + ret);
						} else {
							paramMap.clear();
							paramMap.put("IsPost", Integer.valueOf(0));
							paramMap.put("Times", Integer.valueOf(timesInt + 1));
							paramMap.put("IsSend", Integer.valueOf(0));
							paramMap.put("orderid", orderId);
							paramMap.put("payCompany", payCompany);
							int ret = iEpayOrderDetailSv.Update(paramMap);
							logger.debug("[postPayResult] iEpayOrderDetailSv.Update：" + ret);
						}
					} else {
						paramMap.clear();
						paramMap.put("IsPost", Integer.valueOf(0));
						paramMap.put("Times", Integer.valueOf(timesInt + 1));
						paramMap.put("IsSend", Integer.valueOf(0));
						paramMap.put("orderid", orderId);
						paramMap.put("payCompany", payCompany);
						int ret = iEpayOrderDetailSv.Update(paramMap);
						logger.debug("[postPayResult] iEpayOrderDetailSv.Update：" + ret);
					}
				}
			} catch (Exception e) {
				logger.error("订单：" + orderId + "，请求远程回调时发生错误，错误原因：" + e.getMessage());
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

	/**
	 * 查询3小时以内未支付的订单数据到银行或第三方平台主动查询，并完成推送到应用系统
	 */
	@Override
	public void unPayQuery(int orderCloseTime, String sysId){
		logger.debug("开始执行“查询{}小时以内未支付的订单数据到银行或第三方平台主动查询，并完成推送到应用系统”任务。", orderCloseTime);
		logger.debug("获取订单关闭时间{}小时之内的数据", orderCloseTime);

		List<Map<String, Object>> list = iEpayOrderDetailSv.unPayList(orderCloseTime, sysId);
		if (null == list || list.size() == 0) return;

		int i = 0;
		for (Map<String, Object> map : list) {
			String orderId = MapUtils.getString(map, "orderid");
			logger.debug("{}:开始获取{}订单的支付状态", (i + 1), orderId);
			iPayQuerySv.unPayQuery(orderId);
			i++;
		}
		logger.debug("总{}条订单,全部执行完成", i);
	}
}