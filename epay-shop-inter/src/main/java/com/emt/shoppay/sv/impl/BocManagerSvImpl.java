package com.emt.shoppay.sv.impl;

import com.emt.shoppay.dao.inter.IEpayParaConfigDao;
import com.emt.shoppay.pojo.BocPKCSTool;
import com.emt.shoppay.pojo.BocPayConfig;
import com.emt.shoppay.sv.inter.IBocManagerSv;
import com.emt.shoppay.sv.inter.IPayQueryApiSv;
import com.emt.shoppay.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 中行支付管理
* @ClassName: AbcManagerSvImpl 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author huangdafei
* @date 2017年5月5日 下午2:40:51 
*
 */
@Service
public class BocManagerSvImpl extends BaseSvImpl implements IBocManagerSv {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private IEpayParaConfigDao iEpayParaConfigDao;

	@Autowired
	private IPayQueryApiSv iPayQueryApiSv;

	@Override
	public Map<String, Object> bocPayPcB2C(Map<String, String> upTranData, Map<String, Object> upExtend) throws Exception {
		logger.debug("[bocPayPcB2C] 支付开始...");
		String orderId = getValue(upTranData, "orderId");
		String subject = getValue(upTranData, "subject");
		String fee = getValue(upTranData, "totalFee");
		String resultUrl = getValue(upTranData, "notifyUrl");//应用系统通知地址
		String mchtCustIP = getValue(upTranData, "ip");
		String tradeType = getValue(upTranData, "tradeType");
		String appType = getValue(upTranData, "appType");

		if ("".equals(orderId) || "".equals(fee) || "".equals(resultUrl) || "".equals(subject)) {
			logger.debug("[bocPayPcB2C] 缺少必要的参数！");
			throw new Exception("缺少必要的参数！");
		}

		String orderAmount = "0.00";
		try {
			//格式化金额
			DecimalFormat df = new DecimalFormat("#0.00");
			double dFee = Double.valueOf(fee) / 100;
			orderAmount = df.format(dFee);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new Exception("构建支付参数异常");
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
		logger.debug("[bocPayPcB2C] 查询配置数据，返回：" + lstData);
		if (null != lstData && lstData.size() > 0) {
			Map<String, Object> rMap = lstData.get(0);
			String paraExtend = rMap.get("paraExtend").toString();
			ObjectMapper mapper = new ObjectMapper();

			Map<String, String> dbExtend = mapper.readValue(paraExtend, Map.class);
			String bkInterfaceName = getValue(dbExtend, "interfaceName");
			String bkInterfaceVersion = getValue(dbExtend, "interfaceVersion");
			String merchantNo = getValue(dbExtend, "MerchantID");
			String curCode = getValue(dbExtend, "curCode");
			String payType = getValue(dbExtend, "payType");
			String payNotifyUrl = getValue(dbExtend, "notify_url");//银行回调地址
			payNotifyUrl = Global.getConfig("epay.notify.url") + payNotifyUrl;

			String pfxFilePathKey = getValue(dbExtend, "pfxFilePathKey");//属性文件key
			String signkeyPassword = getValue(dbExtend, "signkeyPassword");

			if ("".equals(curCode) || "".equals(merchantNo)) {
				logger.debug("[bocPayPcB2C] 获取支付参数为空");
				throw new Exception("获取支付参数为空");
			}


			String orderTime = DateUtils.DateTimeToYYYYMMDDhhmmss();
			// 加签
			// orderId|orderTime|curCode|orderAmount|merchantNo
			StringBuilder plainTextBuilder = new StringBuilder();
			plainTextBuilder.append(orderId).append("|")
					.append(orderTime).append("|")
					.append(curCode).append("|")
					.append(orderAmount).append("|")
					.append(merchantNo);

			// 如果使用B2C订单防篡改功能则获取客户IP地址，并作为加签内容，否则不上送该字段
			if (mchtCustIP != null && mchtCustIP.trim().length() != 0)
				plainTextBuilder.append("|").append(mchtCustIP);

			String plainText = plainTextBuilder.toString();
			logger.debug("[bocB2cPc plainText]=[" + plainText + "]");
			byte plainTextByte[] = plainText.getBytes("UTF-8");

			// 获取私钥证书
			BocPKCSTool tool = BocPKCSTool.getSigner(BocPayConfig.getKeystoreFileB2C(pfxFilePathKey),
					signkeyPassword, signkeyPassword,"PKCS7");
			// 签名
			String signData = tool.p7Sign(plainTextByte);
			String orderTimeoutDate = BocPayConfig.getOrderTimeoutDate(new Date());

			logger.info("[bocB2cPc merchantNo]=[" + merchantNo + "]");
			logger.info("[bocB2cPc payType]=[" + payType + "]");
			logger.info("[bocB2cPc orderId]=[" + orderId + "]");
			logger.info("[bocB2cPc curCode]=[" + curCode + "]");
			logger.info("[bocB2cPc orderAmount]=[" + orderAmount + "]");
			logger.info("[bocB2cPc orderTime]=[" + orderTime + "]");
			logger.info("[bocB2cPc orderNote]=[" + subject + "]");
			logger.info("[bocB2cPc orderUrl]=[" + payNotifyUrl + "]");
			logger.info("[bocB2cPc orderTimeoutDate]=[" + orderTimeoutDate + "]");
			logger.info("[bocB2cPc mchtCustIP]=[" + mchtCustIP + "]");
			logger.info("[bocB2cPc signData]=[" + signData + "]");

			String action = BocPayConfig.pgwPortalUrl + "/RecvOrder.do";
			logger.info("[bocB2cPc action]=[" + action + "]");

			// 将参数放置到request对象
			Map<String, Object> payMap = new HashMap<String, Object>();
			payMap.put("merchantNo", merchantNo);
			payMap.put("payType", payType);
			payMap.put("orderNo", orderId);
			payMap.put("curCode", curCode);
			payMap.put("orderAmount", orderAmount);
			payMap.put("orderTime", orderTime);
			payMap.put("orderNote", subject);
			payMap.put("orderUrl", payNotifyUrl);
			payMap.put("orderTimeoutDate", orderTimeoutDate);
			payMap.put("mchtCustIP", mchtCustIP);
			payMap.put("signData", signData);
			payMap.put("action", action);

			// 保存数据,写入数据库epay的epay_oder_detail表中
			String orderDate = DateUtils.DateTimeToYYYYMMDDhhmmss();
			Map<String, String> extend = new HashMap<String, String>();
			extend.put("merUrl", payNotifyUrl);
			extend.put("merVAR", "emaotai.cn.epay");
			extend.put("orderDate", orderDate);
			extend.put("buildData", ToolsUtil.mapObjToJson(payMap));
			extend.put("shopCode", merchantNo);
			logger.debug("[bocB2cPc]保存数据，调用insertPayOrderDetail()");
			Integer retInt = insertPayOrderDetail(upTranData, upExtend, dbExtend, extend);
			logger.debug("[bocB2cPc]保存detail表状态：{}", retInt);

			return payMap;
		} else {
			throw new Exception("获取支付参数为空！");
		}
	}

	@Override
	public Map<String, Object> bocPayWapB2C(Map<String, String> upTranData, Map<String, Object> upExtend) throws Exception {
		return null;
	}

	@Override
	public String notify(Map<String, String> mParam, String payCompany) throws Exception {
		try {
			String orderId = MapUtils.getString(mParam, "orderNo");//mParam.get("orderId").toString();
			String payAmount =  MapUtils.getString(mParam, "payAmount");//mParam.get("Amount").toString();
			String tranSerialNo =  MapUtils.getString(mParam, "orderSeq");//mParam.get("BatchNo").toString();
            String orderStatus = MapUtils.getString(mParam, "orderStatus");

			Map<String, Object> rd2 = new HashMap<String, Object>();
			rd2.put("orderid", orderId);
			rd2.put("payCompany", payCompany);
			List<Map<String, Object>> list = iEpayOrderDetailDao.Select(rd2);
            String resultUrl = "", interfaceName = "", amount = "", sysId = "";
			if (list != null && list.size() > 0) {
				Map<String, Object> map2 = list.get(0);
				resultUrl = MapUtils.getString(map2, "ResultUrl");//(String) map2.get("ResultUrl");
				interfaceName = MapUtils.getString(map2, "payCompany");//map2.get("payCompany").toString();
				amount = MapUtils.getString(map2, "amount");//map2.get("amount").toString();
				sysId =  MapUtils.getString(map2, "Emt_sys_id"); //map2.get("amount").toString();
			} else {
				logger.debug("订单号：" + orderId + "不存在！");
				throw new Exception("订单号：" + orderId + "不存在！");
			}

			//状态查询
			Map<String, String> queryMap = iPayQueryApiSv.queryFromBoc(sysId, orderId);
			logger.debug("[bocpay notify]查询结果：" + queryMap);
			if (null == queryMap || queryMap.size() == 0) {
				logger.debug("[bocpay notify]中行支付失败，中行没有查询到订单数据");
				throw new Exception("中行支付失败，中行没有查询到订单数据");
			}
			String qStatus = MapUtils.getString(queryMap, "status");
			//交易状态 0：初始 1：成功 2：失败 3：银行处理中 4：扣款成功
			if (orderStatus.equals("1") && "1".equals(qStatus)) {
				//更新Epay的数据
				String notify_time = DateUtils.DateTimeToYYYYMMDDhhmmss(new Date());
				Map<String, Object> rd = new HashMap<String, Object>();
				rd.put("TranSerialNo", tranSerialNo);
				rd.put("notifyDate", notify_time);
				rd.put("tranStat", qStatus);
				rd.put("notifyData", ToolsUtil.mapToJson(mParam));
				rd.put("comment", "支付成功");
				rd.put("orderid", orderId);
				rd.put("payCompany", payCompany);
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
				pMap.put("payCompany", payCompany);
				pMap.put("amount", amount);
				pMap.put("tranStat", qStatus);
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
				logger.debug("[bocpay notify]中国银行支付失败，订单未支付，状态：orderStatus=" + orderStatus + ",qStatus=" + qStatus);
				throw new Exception("中国银行支付失败，订单未支付，状态：orderStatus=" + orderStatus + ",qStatus=" + qStatus);
			}
		} catch (Exception e) {
			logger.debug("[bocpay notify]中行通知处理出错，Error：" + e.getMessage());
			throw new Exception("中行通知处理出错，Error：" + e.getMessage());
		}
	}

	/**
	 * 组装程序返回的地址和参数
	 * @param resultUrl
	 * @param amount
	 * @param orderId
	 * @param status
	 * @param interfaceName
	 * @return
	 */
	protected String getReturnUrl(String resultUrl, String amount, String orderId, String status, String interfaceName) {
		String json = "amount=" + amount + "&orderId=" + orderId + "&status=" + status;
		String sign = getSign("10001", json);
		return resultUrl + "?orderid=" + orderId + "&status=1&sign=" + sign + "&amount=" + amount + "&interfaceName=" + interfaceName;
	}
	
}
