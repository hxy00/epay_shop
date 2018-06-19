package com.emt.shoptask.sv.impl;

import com.emt.shoppay.util.ValidataUtil;
import com.emt.shoppay.sv.inter.IEpayOrderDetailSv;
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