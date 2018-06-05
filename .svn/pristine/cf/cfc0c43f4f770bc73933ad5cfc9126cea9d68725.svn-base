package com.emt.shoppay.sv.impl;

import com.emt.shoppay.dao.inter.IEpayParaConfigDao;
import com.emt.shoppay.sv.inter.ICcbManagerSv;
import com.emt.shoppay.sv.inter.IPayQueryApiSv;
import com.emt.shoppay.sv0.CCBRSASig;
import com.emt.shoppay.sv0.EscapeUnescape;
import com.emt.shoppay.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 建行支付
* @ClassName: CcbManagerSvImpl
* @Description: TODO(这里用一句话描述这个类的作用)
* @author huangdafei
* @date 2017年5月3日 下午5:31:55
*
 */
@Service
public class CcbManagerSvImpl extends BaseSvImpl implements ICcbManagerSv {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private IEpayParaConfigDao iEpayParaConfigDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> ccbPay(Map<String, String> upTranData,
			Map<String, Object> upExtend) throws Exception {
		logger.debug("[alipayPc] 支付开始...");
		String orderId = getValue(upTranData, "orderId");
		String subject = getValue(upTranData, "subject");
		String fee = getValue(upTranData, "totalFee");
		String resultUrl = getValue(upTranData, "notifyUrl");
		String ip = getValue(upTranData, "ip");
		String tradeType = getValue(upTranData, "tradeType");
		String appType = getValue(upTranData, "appType");
		
		if ("".equals(orderId) || "".equals(fee) || "".equals(resultUrl) || "".equals(subject)) {
			logger.debug("[alipayPc] 缺少必要的参数！");
			throw new Exception("缺少必要的参数！");
		}
		
		Double totalFee = Double.parseDouble(fee);
		totalFee = totalFee / 100f;
		
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
		logger.debug("[alipayPc] 查询配置数据，返回：" + lstData);
		if (null != lstData && lstData.size() > 0) {
			Map<String, Object> rMap = lstData.get(0);
			String paraExtend = rMap.get("paraExtend").toString();
			ObjectMapper mapper = new ObjectMapper();
			
			Map<String, String> dbExtend = mapper.readValue(paraExtend, Map.class);
			String bkInterfaceName = getValue(dbExtend, "interfaceName");
			String bkInterfaceVersion = getValue(dbExtend, "interfaceVersion");
			String PMERCHANTID = getValue(dbExtend, "PMERCHANTID");
			String POSID = getValue(dbExtend, "POSID");
			String BRANCHID = getValue(dbExtend, "BRANCHID");
			String CURCODE = getValue(dbExtend, "CURCODE");
			String TXCODE = getValue(dbExtend, "TXCODE");
			String REMARK1 = getValue(dbExtend, "REMARK1");
			String REMARK2 = getValue(dbExtend, "REMARK2");
			String TYPE = getValue(dbExtend, "TYPE");
			String PUB = getValue(dbExtend, "PUB");
			String payNotifyUrl = getValue(dbExtend, "notify_url");
			String timeOut = getValue(dbExtend, "timeOut");
			
			String globalUrl = Global.getConfig("epay.notify.url");
			payNotifyUrl = globalUrl + payNotifyUrl;
		    String pubId = PUB.substring(PUB.length() - 30, PUB.length());
			
			StringBuffer sb = new StringBuffer();
			sb.append("MERCHANTID=" + PMERCHANTID);
			sb.append("&POSID=" + POSID);
			sb.append("&BRANCHID=" + BRANCHID);
			sb.append("&ORDERID=" + orderId);
			sb.append("&PAYMENT=" + totalFee);
			sb.append("&CURCODE=" + CURCODE);
			sb.append("&TXCODE=" + TXCODE);
			sb.append("&REMARK1=" + REMARK1);
			sb.append("&REMARK2=" + REMARK2);
			sb.append("&TYPE=" + TYPE);
			sb.append("&PUB=" + pubId);
			sb.append("&GATEWAY=" + "");
			sb.append("&CLIENTIP=" + "");
			sb.append("&REGINFO=" + "");
			sb.append("&PROINFO=" + EscapeUnescape.escape(subject));
			sb.append("&REFERER=" + payNotifyUrl);

			String signText = sb.toString();
			logger.debug("[CCB_PAY] signText签名以前:{}", signText);
			signText = ccbBintoascii(ccbEncryptMD5(signText.trim().getBytes()));
			logger.debug("[CCB_PAY] signText签名以后:{}", signText);

			Map<String, String> map = new HashMap<>();
			map.put("MAC", signText);
			map.put("MERCHANTID", PMERCHANTID);
			map.put("POSID", POSID);
			map.put("BRANCHID", BRANCHID);
			map.put("ORDERID", orderId);
			map.put("PAYMENT", String.valueOf(totalFee));
			map.put("CURCODE",CURCODE);
			map.put("TXCODE", TXCODE);
			map.put("REMARK1", REMARK1);
			map.put("REMARK2", REMARK2);
			map.put("TYPE", TYPE);
			map.put("PUB", pubId);
			map.put("GATEWAY", "");
			map.put("CLIENTIP", "");
			map.put("REGINFO", "");
			map.put("PROINFO", EscapeUnescape.escape(subject));
			map.put("REFERER", payNotifyUrl);
			logger.debug("[CCB_PAY] map:{}", map);

			String orderDate = DateUtils.DateTimeToYYYYMMDDhhmmss();
			Map<String, String> extend = new HashMap<String, String>();
			extend.put("merUrl", payNotifyUrl);
			extend.put("merVAR", "emaotai.cn.epay");
			extend.put("orderDate", orderDate);
			extend.put("buildData", ToolsUtil.mapToJson(map));
			extend.put("shopCode", PMERCHANTID);
			
			// 写入数据库epay的epay_oder_detail表中
			logger.debug("[alipayPc]保存数据，调用insertPayOrderDetail()");
			Integer retInt = insertPayOrderDetail(upTranData, upExtend, dbExtend, extend);
			logger.debug("[alipayPc]保存detail表状态：{}" , retInt);

			Map<String, Object> payMap = new HashMap<String, Object>();
			payMap.put("MERCHANTID", PMERCHANTID);
			payMap.put("POSID", POSID);
			payMap.put("BRANCHID", BRANCHID);
			payMap.put("ORDERID", orderId);
			payMap.put("PAYMENT", totalFee);
			payMap.put("CURCODE", CURCODE);
			payMap.put("TXCODE", TXCODE);
			payMap.put("TYPE", TYPE);
			payMap.put("MAC", signText);
			payMap.put("PROINFO", EscapeUnescape.escape(subject));
			payMap.put("GATEWAY", "");
			payMap.put("CLIENTIP", "");
			payMap.put("REGINFO", "");
			payMap.put("REMARK1", REMARK1);
			payMap.put("REMARK2", REMARK2);
			payMap.put("REFERER", payNotifyUrl);
			payMap.put("THIRDAPPINFO", "");
			
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
			//region 取值
			String POSID = getValue(params, "POSID");
			String BRANCHID = getValue(params, "BRANCHID");
			String ORDERID = getValue(params, "ORDERID");
			String PAYMENT = getValue(params, "PAYMENT");
			String CURCODE = getValue(params, "CURCODE");
			String REMARK1 = getValue(params, "REMARK1");
			String REMARK2 = getValue(params, "REMARK2");
			String ACC_TYPE = getValue(params, "ACC_TYPE");
			String SUCCESS = getValue(params, "SUCCESS");
			String TYPE = getValue(params, "TYPE");
			String REFERER = getValue(params, "REFERER");
			String CLIENTIP = getValue(params, "CLIENTIP");
			String ACCDATE = getValue(params, "ACCDATE");
			String ERRMSG = getValue(params, "ERRMSG");
			String SIGN = getValue(params, "SIGN");
			String USRMSG = getValue(params, "USRMSG");
			String INSTALLNUM = getValue(params, "INSTALLNUM");
			//endregion

			Map<String, Object> rd2 = new HashMap<String, Object>();
			rd2.put("orderid", ORDERID);
			rd2.put("payCompany", "ccb_pay");
			List<Map<String, Object>> list = iEpayOrderDetailDao.Select(rd2);
			String resultUrl = "",interfaceName = "",amount = "",sysId = "",pubId = "",orderDate = "";
			if (list != null && list.size() > 0) {
				Map<String, Object> map2 = list.get(0);
				resultUrl 		= MapUtils.getString(map2, "ResultUrl");//map2.get("ResultUrl").toString();
				amount 			= MapUtils.getString(map2, "amount");//map2.get("amount").toString();
				interfaceName 	= MapUtils.getString(map2, "payCompany");//map2.get("payCompany").toString();
				sysId 			= MapUtils.getString(map2, "Emt_sys_id");//map2.get("Emt_sys_id").toString();
				orderDate		= MapUtils.getString(map2, "orderDate");
				
				Map<String, Object> rd = new HashMap<String, Object>();
				rd.put("payCompany", interfaceName);
				rd.put("sysId", sysId);
				rd.put("type", "pay");
				List<Map<String, Object>> lstData = iEpayParaConfigDao.Select(rd);
				if (null == lstData || lstData.size() == 0) {
					logger.debug("订单号：" + ORDERID + "没有查询到支付参数。");
					throw new Exception("订单号：" + ORDERID + "没有查询到支付参数。");
				}
				Map<String, Object> dbExtend = lstData.get(0);
				pubId = getValue(dbExtend, "PUB");
			}else{
				logger.debug("订单号：" + ORDERID + "不存在！");
				throw new Exception("订单号：" + ORDERID + "不存在！");
			}
			
			//region 生成验签字符串
			StringBuffer sb = new StringBuffer();
			sb.append("POSID=" + POSID);
			sb.append("&BRANCHID=" + BRANCHID);
			sb.append("&ORDERID=" + ORDERID);
			sb.append("&PAYMENT=" + PAYMENT);
			sb.append("&CURCODE=" + CURCODE);
			
			if (null != REMARK1) {
				sb.append("&REMARK1=" + REMARK1);
			}
			
			if (null != REMARK2){
				sb.append("&REMARK2=" + REMARK2);
			}
			
			if(null != ACC_TYPE){
				sb.append("&ACC_TYPE=" + ACC_TYPE);
			}
			
			sb.append("&SUCCESS=" + SUCCESS);
			
			if(null != TYPE){
				sb.append("&TYPE=" + TYPE);
			}
			
			if(REFERER != null){
				sb.append("&REFERER=" + REFERER);
			}
			
			if(null != CLIENTIP){
				sb.append("&CLIENTIP=" + CLIENTIP);
			}
			
			if(null != ACCDATE){
				sb.append("&ACCDATE=" + ACCDATE);
			}
			
			if(null != USRMSG){
				sb.append("&USRMSG=" + USRMSG);
			}
			
			if(null != INSTALLNUM){
				sb.append("&INSTALLNUM=" + INSTALLNUM);
			}
			
			if(null != ERRMSG){
				sb.append("&ERRMSG=" + ERRMSG);
			}

			String signText = sb.toString();
			CCBRSASig rsa = new CCBRSASig();
			rsa.setPublicKey(pubId);
			
			//region 异常处理
			if (!rsa.verifySigature(SIGN, signText)) {
				logger.debug("建行返回结果验签失败，返回的签名：" + SIGN + "，验签：" + signText);
				throw new Exception("建行返回结果验签失败");
			}

			//状态查询
			Map<String, String> queryMap = iPayQueryApiSv.queryFromCcb(sysId, ORDERID, orderDate);
			logger.debug("[ccbpay notify]查询结果：" + queryMap);
			if (null == queryMap || queryMap.size() == 0) {
				logger.debug("[ccbpay notify]建行支付失败，建行没有查询到订单数据");
				throw new Exception("建行支付失败，建行没有查询到订单数据");
			}
			String status = MapUtils.getString(queryMap, "status");
			//交易状态 0：失败 1：成功 2：待银行确认 3：已部分退款 4：已全部退款 5：带银行确认
			if ("Y".equals(SUCCESS) && "1".equals(status)) {
				//region 更新Epay的数据
				String notify_time = DateUtils.DateTimeToYYYYMMDDhhmmss(new Date());
				Map<String, Object> rd = new HashMap<String, Object>();
				rd.put("TranSerialNo", POSID);
				rd.put("notifyDate", notify_time);
				rd.put("tranStat", status);
				rd.put("notifyData", ToolsUtil.mapToJson(params));
				rd.put("comment", "支付成功");
				rd.put("orderid", ORDERID);
				rd.put("payCompany", "ccb_pay");
				iEpayOrderDetailDao.Update(rd);

				Map<String, Object> pstMap = new HashMap<String, Object>();
				pstMap.put("orderId", ORDERID);
				pstMap.put("discountAmount", Integer.valueOf(0));
				pstMap.put("resultUrl", resultUrl);
				postPayResult(pstMap);

				Map<String, String> pMap = new HashMap<String, String>();
				pMap.put("orderId", ORDERID);
				pMap.put("payCompany", interfaceName);
				pMap.put("amount", amount);
				pMap.put("tranStat", status);
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
				logger.debug("[ccbpay notify]建设银行支付失败，订单未支付，状态：SUCCESS=" + SUCCESS + "，status=" + status);
				throw new Exception("建设银行支付失败，订单未支付，状态：SUCCESS=" + SUCCESS + "，status=" + status);
			}
		} catch (Exception e) {
			throw new Exception("建设银行支回调出错：" + e.getMessage());
		}
	}

	/**
	 * 建行支付字节编码
	 * @Title: bintoascii
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param bySourceByte
	 * @param @return  参数说明
	 * @return String    返回类型
	 * @throws
	 */
	public String ccbBintoascii(byte[] bySourceByte) {
		int len, i;
		byte tb;
		char high, tmp, low;
		String result = new String();
		len = bySourceByte.length;
		for (i = 0; i < len; i++) {
			tb = bySourceByte[i];
			tmp = (char) ((tb >>> 4) & 0x000f);
			if (tmp >= 10)
				high = (char) ('a' + tmp - 10);
			else
				high = (char) ('0' + tmp);
			result += high;
			tmp = (char) (tb & 0x000f);
			if (tmp >= 10)
				low = (char) ('a' + tmp - 10);
			else
				low = (char) ('0' + tmp);

			result += low;
		}
		return result;
	}

	/**
	 * 建行支付MD5加密
	 * @Title: ccbEncryptMD5
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param data
	 * @param @return
	 * @param @throws Exception  参数说明
	 * @return byte[]    返回类型
	 * @throws
	 */
	public byte[] ccbEncryptMD5(byte[] data) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(data);
		return md5.digest();
	}

	/**
	 * 当支付时的参数和当前参数不一致时，组装程序返回的地址和参数
	 * @Title: getReturnUrl
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param resultUrl
	 * @param @param amount
	 * @param @param orderId
	 * @param @param status
	 * @param @param interfaceName
	 * @param @return  参数说明
	 * @return String    返回类型
	 * @throws
	 */
	private String getReturnUrl(String resultUrl, String amount, String orderId, String status, String interfaceName) {
		String json = "amount=" + amount + "&orderId=" + orderId + "&status=" + status;
		String sign = getSign("10001", json);
		return resultUrl + "?orderid=" + orderId + "&status=1&sign=" + sign + "&amount=" + amount + "&interfaceName=" + interfaceName;
	}
}
