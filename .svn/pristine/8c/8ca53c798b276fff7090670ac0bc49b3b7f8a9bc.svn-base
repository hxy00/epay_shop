package com.emt.shoptask.sv.impl;

import com.emt.shoppay.dao.inter.IEpayOrderDetailDao;
import com.emt.shoppay.dao.inter.IEpayOrderOperRecordDao;
import com.emt.shoppay.pojo.aliQuery.ReturnCode;
import com.emt.shoppay.sv.inter.IPayQueryApiSv;
import com.emt.shoppay.util.DateUtils;
import com.emt.shoppay.util.ToolsUtil;
import com.emt.shoppay.util.json.JSONArray;
import com.emt.shoptask.sv.inter.IPayPostSv;
import com.emt.shoptask.sv.inter.IPayQuerySv;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayQuerySvImpl implements IPayQuerySv {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired	
	private IEpayOrderOperRecordDao iEpayOrderOperRecordDao;

	@Autowired
	private IEpayOrderDetailDao iEpayOrderDetailDao;

	@Autowired
	protected IPayQueryApiSv iPayQueryApiSv;
	
	@Autowired
	private IPayPostSv iPayPostSv;

	@Override
	public void unPayQuery(String orderId){
		Map<String, Object> rd = new HashMap<String, Object>();
		rd.put("orderid", orderId);
		List<Map<String, Object>> list = null;
		try {
			list = iEpayOrderDetailDao.Select(rd);
		} catch (Exception e1) {
			logger.error(e1.getMessage());
		}
		try {
			if (null == list || list.size() == 0) return;
			for (Map<String, Object> map : list) {
				logger.debug("[postPayResult] 订单号：{} ，支付方式--->{}！", orderId, map.get("payCompany"));
				String _pay =  map.get("payCompany") + "_query";
				Class clazz = this.getClass();
				Method m1 = clazz.getDeclaredMethod(_pay, Map.class);
				Map<String, Object> _r = (Map<String, Object>) m1.invoke(this, map);
				String status = MapUtils.getString(_r, "status");
				if (status.equals("1"))
					break;
				logger.debug("[updateBankState] 订单号：{} ，查询返回：{}！", orderId, _r);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 查询银行支付状态(直接查询)
	 */
	@Override
	public String queryOrderState(String orderId) {
		unPayQuery(orderId);
 
		List<Map<String, Object>> _list = null;
		try {
			Map<String, Object> rd = new HashMap<String, Object>();
			rd.put("orderid", orderId);
			_list = iEpayOrderDetailDao.Select(rd);
			if (null != _list && _list.size() > 0){
				JSONArray json = new JSONArray(_list);
				return json.toString();
			}
		} catch (Exception e1) {
			logger.error(e1.getMessage());
		}
		return null;
	}

	public Map<String, Object> alipay_query(Map<String, Object> pMap) {
		return alipay_pc_query(pMap);
	}

	/**
	 * 支付宝
	 * @param pMap
	 * @return
	 */
	public Map<String, Object> alipay_pc_query(Map<String, Object> pMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String orderId = MapUtils.getString(pMap, "orderid");//pMap.get("orderid") + "";
		String payCompany = MapUtils.getString(pMap, "payCompany");//pMap.get("payCompany") + "";
		String sysId = MapUtils.getString(pMap, "Emt_sys_id");//pMap.get("Emt_sys_id") + "";
		String resultUrl = MapUtils.getString(pMap, "ResultUrl");//pMap.get("ResultUrl") + "";
		String amount = MapUtils.getString(pMap, "amount");
		String orderDate = MapUtils.getString(pMap, "orderDate");

		try{
			Map<String, String> qMap = iPayQueryApiSv.queryFromAlipay(sysId, orderId);
			if (null != qMap && qMap.size() > 0) {
				String status = MapUtils.getString(qMap, "status");
				String tranDate = MapUtils.getString(qMap, "tranDate");
				String comment = MapUtils.getString(qMap, "comment");
				String notifyData = MapUtils.getString(qMap, "notifyData");
				String tranSerialNo = MapUtils.getString(qMap, "tranSerialNo");
				//TRADE_SUCCESS：支付成功，其他：未支付或失败
				if ("TRADE_SUCCESS".equals(status)) {
					logger.debug("[alipay_query]修改订单状态");
					// 更新Epay的数据
					String notify_time = DateUtils.DateTimeToYYYYMMDDhhmmss(new Date());
					Map<String, Object> rd = new HashMap<String, Object>();
					rd.put("TranSerialNo", tranSerialNo);
					rd.put("notifyDate", notify_time);
					rd.put("tranStat", ReturnCode.getCode(status));
					rd.put("comment", comment);
					rd.put("notifyData", notifyData);
					rd.put("orderid", orderId);
					rd.put("payCompany", payCompany);
					//更新详单中订单的状态
					iEpayOrderDetailDao.Update(rd);

					//更新云商订单的状态
					Map<String, Object> pstMap = new HashMap<String, Object>();
					pstMap.put("orderId", orderId);
					pstMap.put("discountAmount", Integer.valueOf(0));
					pstMap.put("resultUrl", resultUrl);
					iPayPostSv.postPayResult(pstMap);

					//保存操作记录 未完待续...
					resultMap.put("orderId", orderId);
					resultMap.put("payCompany", payCompany);
					resultMap.put("amount", amount);
					resultMap.put("status", ReturnCode.getCode(status));
					resultMap.put("orderDate", orderDate);
					resultMap.put("tranDate", tranDate);
					resultMap.put("comment", comment);
					resultMap.put("tranData", pMap.toString());
					resultMap.put("notifyData", notifyData);
					iEpayOrderOperRecordDao.Insert(resultMap);
				} else {
					resultMap.put("status", status);
					resultMap.put("comment", comment);
				}
			} else {
				resultMap.put("comment", "没有查询到数据");
			}
		} catch (Exception e) {
			resultMap.put("comment", "没有查询到数据");
		}
		return resultMap;
	}

	public Map<String, Object> alipay_wap_query(Map<String, Object> pMap) {
		return alipay_pc_query(pMap);
	}

	/**
	 * 农行
	 * @param pMap
	 * @return
	 */
	public Map<String, Object> abc_pay_pc_query(Map<String, Object> pMap){
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String orderId = MapUtils.getString(pMap, "orderid");//pMap.get("orderid") + "";
		String payCompany = MapUtils.getString(pMap, "payCompany");//pMap.get("payCompany") + "";
		String sysId = MapUtils.getString(pMap, "Emt_sys_id");//pMap.get("Emt_sys_id") + "";
		String resultUrl = MapUtils.getString(pMap, "ResultUrl");//pMap.get("ResultUrl") + "";
		String amount = MapUtils.getString(pMap, "amount");
		String orderDate = MapUtils.getString(pMap, "orderDate");

		try{
			Map<String, String> qMap = iPayQueryApiSv.queryFromABC(sysId, orderId);
			if (null != qMap && qMap.size() > 0) {
				String status = MapUtils.getString(qMap, "status");
				String tranDate = MapUtils.getString(qMap, "tranDate");
				String comment = MapUtils.getString(qMap, "comment");
				String notifyData = MapUtils.getString(qMap, "notifyData");
				String tranSerialNo = MapUtils.getString(qMap, "tranSerialNo");
				//"01":"未支付";"02":"无回应";"03":"已请款";"04":"成功";"05":"已退款";"07":"授权确认成功";"00":"授权已取消";"99":"失败";
				if ("04".equals(status)) {
					String tranState = "04".equals(status) ? "1" : status;
					logger.debug("[abc_pay_pc_query]修改订单状态");
					// 更新Epay的数据
					String notify_time = DateUtils.DateTimeToYYYYMMDDhhmmss(new Date());
					Map<String, Object> rd = new HashMap<String, Object>();
					rd.put("TranSerialNo", tranSerialNo);
					rd.put("notifyDate", notify_time);
					rd.put("tranStat", tranState);
					rd.put("comment", comment);
					rd.put("notifyData", notifyData);
					rd.put("orderid", orderId);
					rd.put("payCompany", payCompany);
					//更新详单中订单的状态
					iEpayOrderDetailDao.Update(rd);

					//更新云商订单的状态
					Map<String, Object> pstMap = new HashMap<String, Object>();
					pstMap.put("orderId", orderId);
					pstMap.put("discountAmount", Integer.valueOf(0));
					pstMap.put("resultUrl", resultUrl);
					iPayPostSv.postPayResult(pstMap);

					//保存操作记录 未完待续...
					resultMap.put("orderId", orderId);
					resultMap.put("payCompany", payCompany);
					resultMap.put("amount", amount);
					resultMap.put("status", tranState);
					resultMap.put("orderDate", orderDate);
					resultMap.put("tranDate", tranDate);
					resultMap.put("comment", comment);
					resultMap.put("tranData", pMap.toString());
					resultMap.put("notifyData", notifyData);
					iEpayOrderOperRecordDao.Insert(resultMap);
				} else {
					resultMap.put("status", status);
					resultMap.put("comment", comment);
				}
			} else {
				resultMap.put("comment", "没有查询到数据");
			}
		} catch (Exception e) {
			resultMap.put("comment", "没有查询到数据");
		}
		return resultMap;
	}
	
	public Map<String, Object> abc_pay_wap_query(Map<String, Object> pMap) {
		return abc_pay_pc_query(pMap);
	}

	/**
	 * 建行
	 * @param pMap
	 * @return
	 */
	public Map<String, Object> ccb_pay_query(Map<String, Object> pMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String orderId = MapUtils.getString(pMap, "orderid");//pMap.get("orderid") + "";
		String payCompany = MapUtils.getString(pMap, "payCompany");//pMap.get("payCompany") + "";
		String sysId = MapUtils.getString(pMap, "Emt_sys_id");//pMap.get("Emt_sys_id") + "";
		String resultUrl = MapUtils.getString(pMap, "ResultUrl");//pMap.get("ResultUrl") + "";
		String amount = MapUtils.getString(pMap, "amount");
		String orderDate = MapUtils.getString(pMap, "orderDate");

		try{
			Map<String, String> qMap = iPayQueryApiSv.queryFromCcb(sysId, orderId, orderDate);
			if (null != qMap && qMap.size() > 0) {
				String status = MapUtils.getString(qMap, "status");
				String tranDate = MapUtils.getString(qMap, "tranDate");
				String comment = MapUtils.getString(qMap, "comment");
				String notifyData = MapUtils.getString(qMap, "notifyData");
				String tranSerialNo = MapUtils.getString(qMap, "tranSerialNo");
				//交易状态 0：失败 1：成功 2：待银行确认 3：已部分退款 4：已全部退款 5：带银行确认
				if ("1".equals(status)) {
					logger.debug("[ccb_pay_query]修改订单状态");
					// 更新Epay的数据
					String notify_time = DateUtils.DateTimeToYYYYMMDDhhmmss(new Date());
					Map<String, Object> rd = new HashMap<String, Object>();
					rd.put("TranSerialNo", tranSerialNo);
					rd.put("notifyDate", notify_time);
					rd.put("tranStat", status);
					rd.put("comment", comment);
					rd.put("notifyData", notifyData);
					rd.put("orderid", orderId);
					rd.put("payCompany", payCompany);
					//更新详单中订单的状态
					iEpayOrderDetailDao.Update(rd);

					//更新云商订单的状态
					Map<String, Object> pstMap = new HashMap<String, Object>();
					pstMap.put("orderId", orderId);
					pstMap.put("discountAmount", Integer.valueOf(0));
					pstMap.put("resultUrl", resultUrl);
					iPayPostSv.postPayResult(pstMap);

					//保存操作记录 未完待续...
					resultMap.put("orderId", orderId);
					resultMap.put("payCompany", payCompany);
					resultMap.put("amount", amount);
					resultMap.put("status", status);
					resultMap.put("orderDate", orderDate);
					resultMap.put("tranDate", tranDate);
					resultMap.put("comment", comment);
					resultMap.put("tranData", pMap.toString());
					resultMap.put("notifyData", notifyData);
					iEpayOrderOperRecordDao.Insert(resultMap);
				} else {
					resultMap.put("status", status);
					resultMap.put("comment", comment);
				}
			} else {
				resultMap.put("comment", "没有查询到数据");
			}
		} catch (Exception e) {
			resultMap.put("comment", "没有查询到数据");
		}
		return resultMap;
	}

	public Map<String, Object> ccb_wap_query(Map<String, Object> pMap) {
		return ccb_pay_query(pMap);
	}

	/**
	 * 工行支付状态查询
	* @Title: icbc_pc_query 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param pMap
	* @param @return
	* @param @throws Exception  参数说明 
	* @return Map<String,Object>    返回类型 
	* @throws
	 */
	public Map<String, Object> icbc_pc_query(Map<String, Object> pMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String orderId = MapUtils.getString(pMap, "orderid");//pMap.get("orderid") + "";
		String payCompany = MapUtils.getString(pMap, "payCompany");//pMap.get("payCompany") + "";
		String sysId = MapUtils.getString(pMap, "Emt_sys_id");//pMap.get("Emt_sys_id") + "";
		String resultUrl = MapUtils.getString(pMap, "ResultUrl");//pMap.get("ResultUrl") + "";
		String amount = MapUtils.getString(pMap, "amount");
		String orderDate = MapUtils.getString(pMap, "orderDate");

		try{
			Map<String, String> qMap = iPayQueryApiSv.queryFromIcbc(sysId, orderId, orderDate);
			if (null != qMap && qMap.size() > 0) {
				String status = MapUtils.getString(qMap, "status");
				String tranDate = MapUtils.getString(qMap, "tranDate");
				String comment = MapUtils.getString(qMap, "comment");
				String notifyData = MapUtils.getString(qMap, "notifyData");
				String tranSerialNo = MapUtils.getString(qMap, "tranSerialNo");
				//tranStat=“1”：成功，“2”：失败，“3”：交易可疑，其他：未知交易状态
				if ("1".equals(status)) {
					logger.debug("[icbc_pc_query]修改订单状态");
					// 更新Epay的数据
					String notify_time = DateUtils.DateTimeToYYYYMMDDhhmmss(new Date());
					Map<String, Object> rd = new HashMap<String, Object>();
					rd.put("TranSerialNo", tranSerialNo);
					rd.put("notifyDate", notify_time);
					rd.put("tranStat", status);
					rd.put("comment", comment);
					rd.put("notifyData", notifyData);
					rd.put("orderid", orderId);
					rd.put("payCompany", payCompany);
					//更新详单中订单的状态
					iEpayOrderDetailDao.Update(rd);

					//更新云商订单的状态
					Map<String, Object> pstMap = new HashMap<String, Object>();
					pstMap.put("orderId", orderId);
					pstMap.put("discountAmount", Integer.valueOf(0));
					pstMap.put("resultUrl", resultUrl);
					iPayPostSv.postPayResult(pstMap);

					//保存操作记录 未完待续...
					resultMap.put("orderId", orderId);
					resultMap.put("payCompany", payCompany);
					resultMap.put("amount", amount);
					resultMap.put("status", status);
					resultMap.put("orderDate", orderDate);
					resultMap.put("tranDate", tranDate);
					resultMap.put("comment", comment);
					resultMap.put("tranData", pMap.toString());
					resultMap.put("notifyData", notifyData);
					iEpayOrderOperRecordDao.Insert(resultMap);
				} else {
					resultMap.put("status", status);
					resultMap.put("comment", comment);
				}
			} else {
				resultMap.put("comment", "没有查询到数据");
			}
		} catch (Exception e) {
			resultMap.put("comment", "没有查询到数据");
		}
		return resultMap;
	}

	public Map<String, Object> icbc_wap_query(Map<String, Object> pMap) {
		return icbc_pc_query(pMap);
	}

	/**
	 * 银联“商城支付”-查询订单状态
	 */
	public Map<String, Object> unionpay_emt_query(Map<String, Object> pMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String orderId = MapUtils.getString(pMap, "orderid");//pMap.get("orderid") + "";
		String payCompany = MapUtils.getString(pMap, "payCompany");//pMap.get("payCompany") + "";
		String sysId = MapUtils.getString(pMap, "Emt_sys_id");//pMap.get("Emt_sys_id") + "";
		String resultUrl = MapUtils.getString(pMap, "ResultUrl");//pMap.get("ResultUrl") + "";
		String amount = MapUtils.getString(pMap, "amount");
		String orderDate = MapUtils.getString(pMap, "orderDate");

		try{
			Map<String, String> qMap = iPayQueryApiSv.queryFromUnionpay(sysId, orderId, orderDate);
			if (null != qMap && qMap.size() > 0) {
				String status = MapUtils.getString(qMap, "status");
				String tranDate = MapUtils.getString(qMap, "tranDate");
				String comment = MapUtils.getString(qMap, "comment");
				String notifyData = MapUtils.getString(qMap, "notifyData");
				String tranSerialNo = MapUtils.getString(qMap, "tranSerialNo");
				//"origRespCode":查询交易成功时返回00，查询时已将“00”转换成“1”
				if ("1".equals(status)) {
					logger.debug("[unionpay_emt_query]修改订单状态");
					// 更新Epay的数据
					String notify_time = DateUtils.DateTimeToYYYYMMDDhhmmss(new Date());
					Map<String, Object> rd = new HashMap<String, Object>();
					rd.put("TranSerialNo", tranSerialNo);
					rd.put("notifyDate", notify_time);
					rd.put("tranStat", status);
					rd.put("comment", comment);
					rd.put("notifyData", notifyData);
					rd.put("orderid", orderId);
					rd.put("payCompany", payCompany);
					//更新详单中订单的状态
					iEpayOrderDetailDao.Update(rd);

					//更新云商订单的状态
					Map<String, Object> pstMap = new HashMap<String, Object>();
					pstMap.put("orderId", orderId);
					pstMap.put("discountAmount", Integer.valueOf(0));
					pstMap.put("resultUrl", resultUrl);
					iPayPostSv.postPayResult(pstMap);

					//保存操作记录 未完待续...
					resultMap.put("orderId", orderId);
					resultMap.put("payCompany", payCompany);
					resultMap.put("amount", amount);
					resultMap.put("status", status);
					resultMap.put("orderDate", orderDate);
					resultMap.put("tranDate", tranDate);
					resultMap.put("comment", comment);
					resultMap.put("tranData", pMap.toString());
					resultMap.put("notifyData", notifyData);
					iEpayOrderOperRecordDao.Insert(resultMap);
				} else {
					resultMap.put("status", status);
					resultMap.put("comment", comment);
				}
			} else {
				resultMap.put("comment", "没有查询到数据");
			}
		} catch (Exception e) {
			resultMap.put("comment", "没有查询到数据");
		}
		return resultMap;
	}

	/**
	 * 微信
	 * @param pMap
	 * @return
	 */
	public Map<String, Object> weixin_query(Map<String, Object> pMap){
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String orderId = MapUtils.getString(pMap, "orderid");//pMap.get("orderid") + "";
		String payCompany = MapUtils.getString(pMap, "payCompany");//pMap.get("payCompany") + "";
		String sysId = MapUtils.getString(pMap, "Emt_sys_id");//pMap.get("Emt_sys_id") + "";
		String resultUrl = MapUtils.getString(pMap, "ResultUrl");//pMap.get("ResultUrl") + "";
		String amount = MapUtils.getString(pMap, "amount");
		String orderDate = MapUtils.getString(pMap, "orderDate");

		try{
			Map<String, String> qMap = iPayQueryApiSv.queryFromWeixinpay(orderId, sysId);
			if (null != qMap && qMap.size() > 0) {
				String returnCode = MapUtils.getString(qMap, "return_code");
				String resultCode = MapUtils.getString(qMap, "result_code");
				String trade_state = MapUtils.getString(qMap, "trade_state");
				String total_fee = MapUtils.getString(qMap, "total_fee");
				String tranSerialNo = MapUtils.getString(qMap, "transaction_id");
				String time_end = MapUtils.getString(qMap, "time_end");
//				String orderId = MapUtils.getString(qMap, "out_trade_no");
				String err_code_des = MapUtils.getString(qMap, "err_code_des");
				String err_code = MapUtils.getString(qMap, "err_code");

				//判断查询返回的结果
				Map<String, Object> returnMap = new HashMap<String, Object>();
				if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode) && "SUCCESS".equals(trade_state)) {
					String status = "SUCCESS".equals(trade_state) ? "1" : "0";
					logger.debug("[weixin_query]修改订单状态");
					// 更新Epay的数据
					String notify_time = DateUtils.DateTimeToYYYYMMDDhhmmss(new Date());
					Map<String, Object> rd = new HashMap<String, Object>();
					rd.put("TranSerialNo", tranSerialNo);
					rd.put("notifyDate", notify_time);
					rd.put("tranStat", status);
					rd.put("comment", "支付成功");
					rd.put("notifyData", ToolsUtil.mapToJson(qMap));
					rd.put("orderid", orderId);
					rd.put("payCompany", payCompany);
					//更新详单中订单的状态
					iEpayOrderDetailDao.Update(rd);

					//更新云商订单的状态
					Map<String, Object> pstMap = new HashMap<String, Object>();
					pstMap.put("orderId", orderId);
					pstMap.put("discountAmount", Integer.valueOf(0));
					pstMap.put("resultUrl", resultUrl);
					iPayPostSv.postPayResult(pstMap);

					//保存操作记录 未完待续...
					resultMap.put("orderId", orderId);
					resultMap.put("payCompany", payCompany);
					resultMap.put("amount", amount);
					resultMap.put("status", status);
					resultMap.put("orderDate", orderDate);
					resultMap.put("tranDate", time_end);
					resultMap.put("comment", "支付成功");
					resultMap.put("tranData", pMap.toString());
					resultMap.put("notifyData", ToolsUtil.mapToJson(qMap));
					iEpayOrderOperRecordDao.Insert(resultMap);
				} else {
					resultMap.put("status", trade_state);
					resultMap.put("comment", err_code + "：" + err_code_des);
				}
			} else {
				resultMap.put("comment", "没有查询到数据");
			}
		} catch (Exception e) {
			resultMap.put("comment", "没有查询到数据");
		}
		return resultMap;
	}

	/**
	 * 中行pc b2c
	 * @param pMap
	 * @return
	 */
	public Map<String, Object> boc_b2c_pc_query(Map<String, Object> pMap){
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String orderId = MapUtils.getString(pMap, "orderid");//pMap.get("orderid") + "";
		String payCompany = MapUtils.getString(pMap, "payCompany");//pMap.get("payCompany") + "";
		String sysId = MapUtils.getString(pMap, "Emt_sys_id");//pMap.get("Emt_sys_id") + "";
		String resultUrl = MapUtils.getString(pMap, "ResultUrl");//pMap.get("ResultUrl") + "";
		String amount = MapUtils.getString(pMap, "amount");
		String orderDate = MapUtils.getString(pMap, "orderDate");

		try{
			Map<String, String> qMap = iPayQueryApiSv.queryFromBoc(sysId, orderId);
			if (null != qMap && qMap.size() > 0) {
				String status = MapUtils.getString(qMap, "status");
				String tranDate = MapUtils.getString(qMap, "tranDate");
				String comment = MapUtils.getString(qMap, "comment");
				String notifyData = MapUtils.getString(qMap, "notifyData");
				String tranSerialNo = MapUtils.getString(qMap, "tranSerialNo");
				//交易状态 0：初始 1：成功 2：失败 3：银行处理中 4：扣款成功
				if ("1".equals(status)) {
					logger.debug("[boc_b2c_pc_query]修改订单状态");
					// 更新Epay的数据
					String notify_time = DateUtils.DateTimeToYYYYMMDDhhmmss(new Date());
					Map<String, Object> rd = new HashMap<String, Object>();
					rd.put("TranSerialNo", tranSerialNo);
					rd.put("notifyDate", notify_time);
					rd.put("tranStat", status);
					rd.put("comment", comment);
					rd.put("notifyData", notifyData);
					rd.put("orderid", orderId);
					rd.put("payCompany", payCompany);
					//更新详单中订单的状态
					iEpayOrderDetailDao.Update(rd);

					//更新云商订单的状态
					Map<String, Object> pstMap = new HashMap<String, Object>();
					pstMap.put("orderId", orderId);
					pstMap.put("discountAmount", Integer.valueOf(0));
					pstMap.put("resultUrl", resultUrl);
					iPayPostSv.postPayResult(pstMap);

					//保存操作记录 未完待续...
					resultMap.put("orderId", orderId);
					resultMap.put("payCompany", payCompany);
					resultMap.put("amount", amount);
					resultMap.put("status", status);
					resultMap.put("orderDate", orderDate);
					resultMap.put("tranDate", tranDate);
					resultMap.put("comment", comment);
					resultMap.put("tranData", pMap.toString());
					resultMap.put("notifyData", notifyData);
					iEpayOrderOperRecordDao.Insert(resultMap);
				} else {
					resultMap.put("status", status);
					resultMap.put("comment", comment);
				}
			} else {
				resultMap.put("comment", "没有查询到数据");
			}
		} catch (Exception e) {
			resultMap.put("comment", "没有查询到数据");
		}
		return resultMap;
	}
}
