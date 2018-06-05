package com.emt.shoppay.sv.impl;

import cn.com.infosec.icbc.ReturnValue;
import com.emt.shoppay.dao.inter.IEpayParaConfigDao;
import com.emt.shoppay.pojo.IcbcOrderInfoPcVo;
import com.emt.shoppay.pojo.IcbcOrderInfoWapVo;
import com.emt.shoppay.sv.inter.IIcbcManagerSv;
import com.emt.shoppay.sv.inter.IPayQueryApiSv;
import com.emt.shoppay.sv0.IcbcXmlForDOM4J;
import com.emt.shoppay.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工行支付管理
* @ClassName: IcbcManagerSvImpl 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author huangdafei
* @date 2017年5月5日 下午2:40:35 
*
 */
@Repository("icbcManagerSvImpl")
public class IcbcManagerSvImpl extends BaseSvImpl implements IIcbcManagerSv {
	//根据操作系统设置读取证书路径
	private static String operatingSystem = Global.getConfig("epay.OS.switch");
	private static String flag = operatingSystem.equals("Linux") ? "/" : "";

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private IEpayParaConfigDao iEpayParaConfigDao;

	@Autowired
	private IPayQueryApiSv iPayQueryApiSv;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> icbcPayWapB2C(Map<String, String> upTranData, Map<String, Object> upExtend) throws Exception {
		String orderId = getValue(upTranData, "orderId");
		String fee = getValue(upTranData, "totalFee");
		String resultUrl = getValue(upTranData, "notifyUrl");
		String subject = getValue(upTranData, "subject");
		String ip = getValue(upTranData, "ip");
		String tradeType = getValue(upTranData, "tradeType");
		String appType = getValue(upTranData, "appType");
		
		if ("".equals(orderId) || "".equals(fee) || "".equals(resultUrl) || "".equals(subject)) {
			logger.debug("[abcPay] 缺少必要的参数！");
			throw new Exception("缺少必要的参数！");
		}
		
		String interfaceName = getValue(upExtend, "interfaceName");
		String interfaceVersion = getValue(upExtend, "interfaceVersion");
		String qid = getValue(upExtend, "qid");
		String clientType = getValue(upExtend, "clientType");
		String merReference = getValue(upExtend, "merReference");
		String busiid = getValue(upExtend, "busiid");
		String sysId = getValue(upExtend, "sysId");		

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
			String crtFile = getValue(dbExtend, "crtFile");
			String keyFile = getValue(dbExtend, "keyFile");
			String password = getValue(dbExtend, "password");
			String bkInterfaceName = getValue(dbExtend, "interfaceName");
			String bkInterfaceVersion = getValue(dbExtend, "interfaceVersion");
			String merID = getValue(dbExtend, "merID");
			String merAcct = getValue(dbExtend, "merAcct");
			String req_url = getValue(dbExtend, "req_url");
			String payNotifyUrl = getValue(dbExtend, "notify_url");
			String timeOut = getValue(dbExtend, "timeOut");
			String crtFilePathKey = getValue(dbExtend, "crtFilePathKey");
			String keyFilePathKey = getValue(dbExtend, "keyFilePathKey");

			if ("".equals(crtFile) || "".equals(keyFile) || "".equals(password) || "".equals(bkInterfaceName)|| 
					"".equals(bkInterfaceVersion) || "".equals(merID) || "".equals(merAcct) || "".equals(req_url) ||
					"".equals(payNotifyUrl) || "".equals(timeOut) || TextUtils.isEmpty(crtFilePathKey) || TextUtils.isEmpty(keyFilePathKey)) {
				logger.debug("[abcPay] 获取支付参数为空");
				throw new Exception("获取支付参数为空");
			}

			if("Linux".equals(operatingSystem)){
				crtFile = Config.getConfig("pay/icbc/icbc_conf_linux.properties", crtFilePathKey);
				keyFile = Config.getConfig("pay/icbc/icbc_conf_linux.properties", keyFilePathKey);
			} else {
				crtFile = Config.getConfig("pay/icbc/icbc_conf_windows.properties", crtFilePathKey);
				keyFile = Config.getConfig("pay/icbc/icbc_conf_windows.properties", keyFilePathKey);
			}
			
			byte[] bcert = this.getFileByte(crtFile);
			byte[] bkey = this.getFileByte(keyFile);
			char[] keyPass = password.toCharArray();

			Integer totalFee = Integer.valueOf(fee);
			payNotifyUrl = Global.getConfig("epay.notify.url") + payNotifyUrl;
			String orderDate = DateUtils.DateTimeToYYYYMMDDhhmmss();
			IcbcOrderInfoWapVo orderInfo = new IcbcOrderInfoWapVo();
			orderInfo.setMerID(merID);
			orderInfo.setMerAcct(merAcct);
			orderInfo.setAmount(totalFee);
			orderInfo.setOrderid(orderId);
			orderInfo.setGoodsName(subject);
			orderInfo.setOrderDate(orderDate);
			orderInfo.setMerURL(payNotifyUrl);
			orderInfo.setQid(Long.valueOf(qid));
			String tranDataxml = orderInfo.toTranData();
			// tranDataxml签名后的签名数据
			byte[] sign = ReturnValue.sign(tranDataxml.getBytes(), tranDataxml.getBytes().length, bkey, keyPass);
			if (sign == null) {
				throw new Exception("签名失败");
			} else {
				logger.debug("ICBC 签名成功");
			}

			// base64编码
			byte[] EncSign = ReturnValue.base64enc(sign);
			String SignMsgBase64 = (new String(EncSign)).toString();// 签名信息BASE64编码
			logger.debug("ICBC 签名信息BASE64编码 : {}", SignMsgBase64);

			byte[] EncCert = ReturnValue.base64enc(bcert);
			String CertBase64 = new String(EncCert).toString();// 证书公钥BASE64编码
			logger.debug("ICBC 证书公钥BASE64编码 : {}", CertBase64);

			byte[] base64 = ReturnValue.base64enc(tranDataxml.getBytes());
			String tranDataBase64 = new String(base64, "GBK");

			byte[] bxml = ReturnValue.base64dec(tranDataBase64.getBytes());
			logger.debug("tranData base64dec: {}", new String(bxml));

			Map<String, String> extend = new HashMap<>();
			extend.put("merUrl", payNotifyUrl);
			extend.put("merVAR", "emaotai.cn.epay");
			extend.put("orderDate", orderDate);
			extend.put("buildData", orderInfo.toTranData());
			extend.put("shopCode", merID);

			// 写入数据库epay的epay_oder_detail表中
			Integer retInt = insertPayOrderDetail(upTranData, upExtend, dbExtend, extend);
			logger.debug("保存detail表状态：{}", retInt);
			
			Map<String, Object> payMap = new HashMap<String, Object>();
			payMap.put("interfaceName", bkInterfaceName);
			payMap.put("interfaceVersion", bkInterfaceVersion);
			payMap.put("tranData", tranDataBase64);
			payMap.put("merSignMsg", SignMsgBase64);
			payMap.put("merCert", CertBase64);
			payMap.put("clientType", 0);
			payMap.put("req_url", req_url);
			return payMap;
		} else {
			throw new Exception("获取支付参数为空！");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> icbcPayPcB2C(Map<String, String> upTranData, Map<String, Object> upExtend) throws Exception {
		String orderId = getValue(upTranData, "orderId");
		String fee = getValue(upTranData, "totalFee");
		String resultUrl = getValue(upTranData, "notifyUrl");
		String subject = getValue(upTranData, "subject");
		String ip = getValue(upTranData, "ip");
		String tradeType = getValue(upTranData, "tradeType");
		String appType = getValue(upTranData, "appType");
		
		if ("".equals(orderId) || "".equals(fee) || "".equals(resultUrl) || "".equals(subject)) {
			logger.debug("[abcPay] 缺少必要的参数！");
			throw new Exception("缺少必要的参数！");
		}
		
		String interfaceName = getValue(upExtend, "interfaceName");
		String interfaceVersion = getValue(upExtend, "interfaceVersion");
		String qid = getValue(upExtend, "qid");
		String clientType = getValue(upExtend, "clientType");
		String merReference = getValue(upExtend, "merReference");
		String busiid = getValue(upExtend, "busiid");
		String sysId = getValue(upExtend, "sysId");
		
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
			String crtFile = getValue(dbExtend, "crtFile");
			String keyFile = getValue(dbExtend, "keyFile");
			String password = getValue(dbExtend, "password");
			String bkInterfaceName = getValue(dbExtend, "interfaceName");
			String bkInterfaceVersion = getValue(dbExtend, "interfaceVersion");
			String merID = getValue(dbExtend, "merID");
			String merAcct = getValue(dbExtend, "merAcct");
			String req_url = getValue(dbExtend, "req_url");
			String payNotifyUrl = getValue(dbExtend, "notify_url");
			String timeOut = getValue(dbExtend, "timeOut");
			String crtFilePathKey = getValue(dbExtend, "crtFilePathKey");
			String keyFilePathKey = getValue(dbExtend, "keyFilePathKey");

			if ("".equals(password) || "".equals(bkInterfaceName)||
					"".equals(bkInterfaceVersion) || "".equals(merID) || "".equals(merAcct) || "".equals(req_url) ||
					"".equals(payNotifyUrl) || "".equals(timeOut) || TextUtils.isEmpty(crtFilePathKey) || TextUtils.isEmpty(keyFilePathKey)) {
				logger.debug("[icbcPay] 获取支付参数为空");
				throw new Exception("获取支付参数为空");
			}

			if("Linux".equals(operatingSystem)){
				crtFile = Config.getConfig("pay/icbc/icbc_conf_linux.properties", crtFilePathKey);
				keyFile = Config.getConfig("pay/icbc/icbc_conf_linux.properties", keyFilePathKey);
			} else {
				crtFile = Config.getConfig("pay/icbc/icbc_conf_windows.properties", crtFilePathKey);
				keyFile = Config.getConfig("pay/icbc/icbc_conf_windows.properties", keyFilePathKey);
			}
			
//			crtFile = SystemUtil.getClassPath() + crtFile;
//			keyFile = SystemUtil.getClassPath() + keyFile;
			
			byte[] bcert = this.getFileByte(crtFile);
			byte[] bkey = this.getFileByte(keyFile);
			char[] keyPass = password.toCharArray();

			IcbcOrderInfoPcVo orderInfo = new IcbcOrderInfoPcVo();
			payNotifyUrl = Global.getConfig("epay.notify.url") + payNotifyUrl;
			String orderDate = DateUtils.DateTimeToYYYYMMDDhhmmss();
			Integer totalFee = Integer.valueOf(fee);
			
			orderInfo.setMerID(merID);
			orderInfo.setMerAcct(merAcct);
			orderInfo.setAmount(totalFee);
			orderInfo.setOrderid(orderId);
			orderInfo.setGoodsName(subject);
			orderInfo.setOrderDate(orderDate);
			orderInfo.setMerURL(payNotifyUrl);
			orderInfo.setMerReference(merReference);
			orderInfo.setQid(Long.valueOf(qid));
			String tranDataxml = orderInfo.toTranData();
			// tranDataxml签名后的签名数据
			byte[] sign = ReturnValue.sign(tranDataxml.getBytes(), tranDataxml.getBytes().length, bkey, keyPass);
			if (sign == null) {
				throw new Exception("签名失败");
			} else {
				logger.debug("ICBC 签名成功");
			}

			// base64编码
			byte[] EncSign = ReturnValue.base64enc(sign);
			String SignMsgBase64 = (new String(EncSign)).toString();// 签名信息BASE64编码
			logger.debug("ICBC 签名信息BASE64编码 : {}", SignMsgBase64);

			byte[] EncCert = ReturnValue.base64enc(bcert);
			String CertBase64 = new String(EncCert).toString();// 证书公钥BASE64编码
			logger.debug("ICBC 证书公钥BASE64编码 : {}", CertBase64);

			byte[] base64 = ReturnValue.base64enc(tranDataxml.getBytes());
			String tranDataBase64 = new String(base64, "GBK");

			byte[] bxml = ReturnValue.base64dec(tranDataBase64.getBytes());
			logger.debug("tranData base64dec: {}", new String(bxml));

			Map<String, String> extend = new HashMap<>();
			extend.put("merUrl", payNotifyUrl);
			extend.put("merVAR", "emaotai.cn.epay");
			extend.put("orderDate", orderDate);
			extend.put("buildData", orderInfo.toTranData());
			// 写入数据库epay的epay_oder_detail表中
			Integer retInt = insertPayOrderDetail(upTranData, upExtend, dbExtend, extend);
			logger.debug("保存detail表状态：{}", retInt);
			
			Map<String, Object> payMap = new HashMap<String, Object>();
			payMap.put("interfaceName", bkInterfaceName);
			payMap.put("interfaceVersion", bkInterfaceVersion);
			payMap.put("tranData", tranDataBase64);
			payMap.put("merSignMsg", SignMsgBase64);
			payMap.put("merCert", CertBase64);
			payMap.put("clientType", 0);
			payMap.put("req_url", req_url);
			return payMap;
		} else {
			throw new Exception("获取支付参数为空！");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String notifyWapB2C(Map<String, String> mParam) throws Exception {
		if (null == mParam) {
			throw new Exception("回调参数为空");
		}
		String notifyDataXml = null;
		try {
			String notifyDataBase64 = mParam.get("notifyDataBase64").toString();
			String resultUrl = "";
			String amount = "";
			String strList[] = notifyDataBase64.split(" ");
			notifyDataBase64 = strList[0];
			for (int i = 1; i < strList.length; i++) {
				notifyDataBase64 = notifyDataBase64 + "+" + strList[i];
			}
			byte[] notifyDataB = ReturnValue.base64dec(notifyDataBase64.getBytes());
			notifyDataXml = new String(notifyDataB);
			logger.debug("notifyDataXml:\n{}\n", notifyDataXml);
			if(StringUtils.isEmpty(notifyDataXml))
				throw new Exception("工行回调处理失败");
		}catch (Exception e){
			logger.debug("工行回调处理失败");
			throw new Exception("工行回调处理失败");
		}

		try {
			Map<String, Object> map = IcbcXmlForDOM4J.parse_wap_b2c(notifyDataXml);
			Map<String, Object> mapOrder = (Map<String, Object>) map.get("orderInfo");// 订单信息
			Map<String, Object> mapBank = (Map<String, Object>) map.get("bank");// 银行通知结果

			String orderId = orderId = MapUtils.getString(mapOrder, "orderid");//(String) mapOrder.get("orderid");
			String TranSerialNo = MapUtils.getString(mapBank, "TranSerialNo");//(String) mapBank.get("TranBatchNo");
			String notifyDate = MapUtils.getString(mapBank, "notifyDate");//(String) mapBank.get("notifyDate");
			String tranStat = MapUtils.getString(mapBank, "tranStat");//(String) mapBank.get("tranStat");
			String comment = MapUtils.getString(mapBank, "comment");//(String) mapBank.get("comment");

			Map<String, Object> rd2 = new HashMap<String, Object>();
			rd2.put("orderid", orderId);
			rd2.put("payCompany", "icbc_wap");
			List<Map<String, Object>> list = iEpayOrderDetailDao.Select(rd2);
			String resultUrl = "", amount = "", sysId = "", orderDate = "", interfaceName = "";
			if (list != null && list.size() > 0) {
				Map<String, Object> map2 = list.get(0);
				resultUrl = MapUtils.getString(map2, "ResultUrl");//(String) map2.get("ResultUrl");
				amount = MapUtils.getString(map2, "amount");//map2.get("amount").toString();
				sysId = MapUtils.getString(map2, "Emt_sys_id");
				orderDate = MapUtils.getString(map2, "orderDate");
				interfaceName = getValue(map2, "payCompany");
			} else {
				logger.debug("订单号：" + orderId + "不存在！");
				throw new Exception("订单号：" + orderId + "不存在！");
			}

			//判断回调的状态，查询订单支付状态
			Map<String, String> queryMap = iPayQueryApiSv.queryFromIcbc(sysId, orderId, orderDate);
			logger.debug("[icbc notify]查询结果：" + queryMap);
			if (null == queryMap || queryMap.size() == 0) {
				logger.debug("[icbc notify]工行回调失败，工行没有查询到订单数据");
				throw new Exception("工行回调失败，工行没有查询到订单数据");
			}
			String qStatus = MapUtils.getString(queryMap, "status");
			//tranStat=“1”：成功，“2”：失败，“3”：交易可疑，其他：未知交易状态
			if ("1".equals(tranStat) && "1".equals(qStatus)) {
				// 更新epay中的数据
				Map<String, Object> rd = new HashMap<String, Object>();
				rd.put("TranSerialNo", TranSerialNo);
				rd.put("notifyDate", notifyDate);
				rd.put("tranStat", tranStat);
				rd.put("notifyData", notifyDataXml);
				rd.put("comment", comment);
				rd.put("orderid", orderId);
				rd.put("payCompany", "icbc_wap");
				iEpayOrderDetailDao.Update(rd);

				// 通知应用系统
				Map<String, Object> pstMap = new HashMap<String, Object>();
				pstMap.put("orderId", orderId);
				pstMap.put("discountAmount", Integer.valueOf(0));
				pstMap.put("resultUrl", resultUrl);
				postPayResult(pstMap);

				//组装返回参数
				Map<String, String> pMap = new HashMap<String, String>();
				pMap.put("orderId", orderId);
				pMap.put("payCompany", interfaceName);
				pMap.put("amount", amount);
				pMap.put("tranStat", tranStat);
				pMap.put("discountAmount", "0.00");
				pMap.put("interfaceName", interfaceName);

				String tranMapJson = ToolsUtil.mapToJson(pMap);
				String tranData = Base64Util.encodeBase64(tranMapJson, "UTF-8");
				String sign = getSign("10001", tranData);

				Map<String, Object> param = new HashMap<String, Object>();
				param.put("tranData", tranData);
				param.put("sign", sign);
				return HttpUtil.getResultUrl(resultUrl, param);
			} else {
				logger.debug("[icbc notify]工行回调失败，查询订单状态：tranStat={}，qstatus={}", tranStat, qStatus);
				throw new Exception("工行回调失败，查询订单状态：tranStat = " + tranStat + ", qstatus=" + qStatus);
			}
		} catch (Exception e) {
			logger.debug("[icbc notify]工行通知处理出错，Error：" + e.getMessage());
			throw new Exception("工行通知处理出错，Error：" + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String notifyPcB2C(Map<String, String> mParam) throws Exception {
		if (null == mParam) {
			throw new Exception("工行回调参数为空");
		}
		String notifyDataXml = null;
		try{
			String notifyDataBase64 = mParam.get("notifyDataBase64").toString();
			String strList[] = notifyDataBase64.split(" ");
			notifyDataBase64 = strList[0];
			for (int i = 1; i < strList.length; i++) {
				notifyDataBase64 = notifyDataBase64 + "+" + strList[i];
			}
			byte[] notifyDataB = ReturnValue.base64dec(notifyDataBase64.getBytes());
			notifyDataXml = new String(notifyDataB);
			logger.debug("notifyDataXml:\n{}\n", notifyDataXml);
			if(StringUtils.isEmpty(notifyDataXml))
				throw new Exception("工行回调处理失败");
		}catch (Exception e){
			logger.debug("工行回调处理失败");
			throw new Exception("工行回调处理失败");
		}

		try {
			Map<String, Object> map = IcbcXmlForDOM4J.parse_pc_b2c(notifyDataXml);
			Map<String, Object> mapOrder = (Map<String, Object>) map.get("orderInfo");// 订单信息
			Map<String, Object> mapBank = (Map<String, Object>) map.get("bank");// 银行通知结果
			Map<String, List<Map<String, String>>> subOrderInfoList = (Map<String, List<Map<String, String>>>)mapOrder.get("subOrderInfoList");
			List<Map<String, String>> subOrderInfos = (List<Map<String, String>>)subOrderInfoList.get("subOrderInfos");
			Map<String, String> subOrderInfo = subOrderInfos.get(0);

			String orderId = orderId = MapUtils.getString(mapOrder, "orderid");//(String) mapOrder.get("orderid");
			String TranSerialNo = MapUtils.getString(mapBank, "TranBatchNo");//(String) mapBank.get("TranBatchNo");
			String notifyDate = MapUtils.getString(mapBank, "notifyDate");//(String) mapBank.get("notifyDate");
			String tranStat = MapUtils.getString(mapBank, "tranStat");//(String) mapBank.get("tranStat");
			String comment = MapUtils.getString(mapBank, "comment");//(String) mapBank.get("comment");

			Map<String, Object> rd2 = new HashMap<String, Object>();
			rd2.put("orderid", orderId);
			rd2.put("payCompany", "icbc_pc");
			List<Map<String, Object>> list = iEpayOrderDetailDao.Select(rd2);
			String resultUrl = "",  amount = "", sysId = "", orderDate = "", interfaceName = "";
			if (list != null && list.size() > 0) {
				Map<String, Object> map2 = list.get(0);
				resultUrl = MapUtils.getString(map2, "ResultUrl");//(String) map2.get("ResultUrl");
				amount = MapUtils.getString(map2, "amount");//map2.get("amount").toString();
				sysId = MapUtils.getString(map2, "Emt_sys_id");
				orderDate = MapUtils.getString(map2, "orderDate");
				interfaceName = getValue(map2, "payCompany");
			} else {
				logger.debug("订单号：" + orderId + "不存在！");
				throw new Exception("订单号：" + orderId + "不存在！");
			}

			//判断回调的状态，查询订单支付状态
			Map<String, String> queryMap = iPayQueryApiSv.queryFromIcbc(sysId, orderId, orderDate);
			logger.debug("[icbc notify]查询结果：" + queryMap);
			if (null == queryMap || queryMap.size() == 0) {
				logger.debug("[icbc notify]工行回调失败，工行没有查询到订单数据");
				throw new Exception("工行回调失败，工行没有查询到订单数据");
			}
			String qStatus = MapUtils.getString(queryMap, "status");
			//tranStat=“1”：成功，“2”：失败，“3”：交易可疑，其他：未知交易状态
			if ("1".equals(tranStat) && "1".equals(qStatus)) {
				// 更新epay中的数据
				Map<String, Object> rd = new HashMap<String, Object>();
				rd.put("TranSerialNo", TranSerialNo);
				rd.put("notifyDate", notifyDate);
				rd.put("tranStat", tranStat);
				rd.put("comment", comment);
				rd.put("notifyData", notifyDataXml);
				rd.put("orderid", orderId);
				rd.put("payCompany", "icbc_pc");
				iEpayOrderDetailDao.Update(rd);

				//通知应用系统
				Map<String, Object> pstMap = new HashMap<String, Object>();
				pstMap.put("orderId", orderId);
				pstMap.put("discountAmount", Integer.valueOf(0));
				pstMap.put("resultUrl", resultUrl);
				postPayResult(pstMap);

				//组装返回参数
				Map<String, String> pMap = new HashMap<String, String>();
				pMap.put("orderId", orderId);
				pMap.put("payCompany", interfaceName);
				pMap.put("amount", amount);
				pMap.put("tranStat", tranStat);
				pMap.put("discountAmount", "0.00");//优惠金额
				pMap.put("interfaceName", interfaceName);

				String tranMapJson = ToolsUtil.mapToJson(pMap);
				String tranData = Base64Util.encodeBase64(tranMapJson, "UTF-8");
				String sign = getSign("10001", tranData);

				Map<String, Object> param = new HashMap<String, Object>();
				param.put("tranData", tranData);
				param.put("sign", sign);
				return HttpUtil.getResultUrl(resultUrl, param);
			} else {
				logger.debug("[icbc notify]工行回调失败，查询订单状态：tranStat={}，qstatus={}", tranStat, qStatus);
				throw new Exception("工行回调失败，查询订单状态：tranStat = " + tranStat + ", qstatus=" + qStatus);
			}
		} catch (Exception e) {
			logger.debug("[icbc notify]工行通知处理出错，Error：" + e.getMessage());
			throw new Exception("工行通知处理出错，Error：" + e.getMessage());
		}
	}

	/**
	 * 获取文件的字节数
	 * @Title: getFileByte
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param fileName
	 * @param @return
	 * @param @throws IOException  参数说明
	 * @return byte[]    返回类型
	 * @throws
	 */
	public byte[] getFileByte(String fileName) throws IOException {

		FileInputStream in1 = new FileInputStream(fileName);
		byte[] bcert = null;
		try {
			bcert = new byte[in1.available()];
			in1.read(bcert);
		} finally {
			in1.close();
		}
		return bcert;
	}
}
