package com.emt.shoppay.sv.impl;

import com.abc.pay.client.Constants;
import com.abc.pay.client.JSON;
import com.abc.pay.client.ebus.PaymentRequest;
import com.emt.shoppay.dao.inter.IEpayParaConfigDao;
import com.emt.shoppay.sv.inter.IAbcManagerSv;
import com.emt.shoppay.sv.inter.IPayQueryApiSv;
import com.emt.shoppay.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 农行支付管理
* @ClassName: AbcManagerSvImpl 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author huangdafei
* @date 2017年5月5日 下午2:40:51 
*
 */
@Service
public class AbcManagerSvImpl extends BaseSvImpl implements IAbcManagerSv {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private IEpayParaConfigDao iEpayParaConfigDao;

	@Autowired
	private IPayQueryApiSv iPayQueryApiSv;

	@Override
	public Map<String, Object> abcPay(Map<String, String> upTranData, Map<String, Object> upExtend) throws Exception {
		logger.debug("[abcPay] 支付开始...");
		String orderId = getValue(upTranData, "orderId");
		String subject = getValue(upTranData, "subject");
		String fee = getValue(upTranData, "totalFee");
		String resultUrl = getValue(upTranData, "notifyUrl");
		String ip = getValue(upTranData, "ip");
		String tradeType = getValue(upTranData, "tradeType");
		String appType = getValue(upTranData, "appType");
		
		if ("".equals(orderId) || "".equals(fee) || "".equals(resultUrl) || "".equals(subject)) {
			logger.debug("[abcPay] 缺少必要的参数！");
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
		logger.debug("[abcPay] 查询配置数据，返回：" + lstData);
		if (null != lstData && lstData.size() > 0) {
			Map<String, Object> rMap = lstData.get(0);
			String paraExtend = rMap.get("paraExtend").toString();
			ObjectMapper mapper = new ObjectMapper();
			
			Map<String, String> dbExtend = mapper.readValue(paraExtend, Map.class);
			String bkInterfaceName = getValue(dbExtend, "interfaceName");
			String bkInterfaceVersion = getValue(dbExtend, "interfaceVersion");
			String MerchantID = getValue(dbExtend, "MerchantID");
			String PayTypeID = getValue(dbExtend, "PayTypeID");
			String notifyUrl = getValue(dbExtend, "notify_url");
			String configIndex = dbExtend.get("configIndex");//getValue(dbExtend, "configIndex");
			String timeOut = getValue(dbExtend, "timeOut");
			if ("".equals(PayTypeID) || "".equals(notifyUrl) || "".equals(configIndex) || "".equals(timeOut)) {
				logger.debug("[abcPay] 获取支付参数为空");
				throw new Exception("获取支付参数为空");
			}
			
			String merUrl = Global.getConfig("epay.notify.url") + notifyUrl;
			Date date = new Date();
			String dateStr = getDateTime(date, "yyyy/MM/dd");
			String timeStr = getDateTime(date, "HH:mm:ss");
			String orderTimeOutDate = getOrderTimeoutDate(date, Integer.valueOf(timeOut));
//			String notifyUrl = PaySetting.getAbcBackUrl(type);

			//1、生成订单对象
			PaymentRequest tPaymentRequest = new PaymentRequest();
			tPaymentRequest.dicOrder.put("PayTypeID", PayTypeID);                  		//设定交易类型ImmediatePay
			tPaymentRequest.dicOrder.put("OrderDate", dateStr);                  		//设定订单日期 （必要信息 - YYYY/MM/DD）
			tPaymentRequest.dicOrder.put("OrderTime", timeStr);                  		//设定订单时间 （必要信息 - HH:MM:SS）
			tPaymentRequest.dicOrder.put("commitVoTimeoutDate", orderTimeOutDate);     	//设定订单有效期
			tPaymentRequest.dicOrder.put("OrderNo", orderId);            				//设定订单编号 （必要信息）
			tPaymentRequest.dicOrder.put("CurrencyCode", "156");             			//设定交易币种
			tPaymentRequest.dicOrder.put("OrderAmount", totalFee);      				//设定交易金额
			tPaymentRequest.dicOrder.put("InstallmentMark", "0");       				//分期标识
			tPaymentRequest.dicOrder.put("CommodityType", "0202");           			//设置商品种类
//			tPaymentRequest.dicOrder.put("BuyIP", commitVo.getBuyIP());                 //IP
			tPaymentRequest.dicOrder.put("ExpiredDate", "30");               			//设定订单保存时间(天)

			//2、订单明细
			LinkedHashMap<String, Object> orderitem = new LinkedHashMap<String, Object>();
			orderitem.put("ProductID", "");												//商品代码，预留字段
			orderitem.put("ProductName", subject);										//商品名称
			orderitem.put("UnitPrice", totalFee);										//商品总价
			orderitem.put("Qty", "1");													//商品数量
			orderitem.put("ProductRemarks", subject + "-农行支付"); 						//商品备注项
			orderitem.put("ProductType", "茅台电商产品");									//商品类型
			orderitem.put("ProductDiscount", "0.0");									//商品折扣
			tPaymentRequest.orderitems.put(1, orderitem);

			//3、生成支付请求对象
			String paymentType = "A";
			tPaymentRequest.dicRequest.put("PaymentType", paymentType);            		//设定支付类型(1：农行卡支付 2：国际卡支付 3：农行贷记卡支付 5:基于第三方的跨行支付 A:支付方式合并 6：银联跨行支付，7:对公户)
			String paymentLinkType  = "1";												                                      
			if ("abc_pay_wap".equals(interfaceName)) {
				paymentLinkType = "2";
			}
			tPaymentRequest.dicRequest.put("PaymentLinkType", paymentLinkType);    		//设定支付接入方式(1：internet网络接入 2：手机网络接入 3:数字电视网络接入 4:智能客户端，*必输)
			if (paymentType.equals(Constants.PAY_TYPE_UCBP) && paymentLinkType.equals(Constants.PAY_LINK_TYPE_MOBILE)) {
			    tPaymentRequest.dicRequest.put("UnionPayLinkType", "0");  				//当支付类型为6，支付接入方式为2的条件满足时，需要设置银联跨行移动支付接入方式
			}
			tPaymentRequest.dicRequest.put("NotifyType", "1");							//通知方式(0：URL页面通知，1：服务器通知)
			tPaymentRequest.dicRequest.put("ResultNotifyURL", merUrl);    				//设定通知URL地址
			tPaymentRequest.dicRequest.put("IsBreakAccount", "0");      				//设定交易是否分账
			
			//4、组装保存的参数
			Map<String, String> signMap = new HashMap<>();
			signMap.put("PayTypeID", "ImmediatePay");
			signMap.put("OrderDate", dateStr);
			signMap.put("OrderTime", timeStr);
			signMap.put("commitVoTimeoutDate", orderTimeOutDate);
			signMap.put("OrderNo", orderId);
			signMap.put("CurrencyCode", "156");
			signMap.put("OrderAmount", String.valueOf(totalFee));
			signMap.put("InstallmentMark", "0");
			signMap.put("CommodityType", "0201");
			signMap.put("ExpiredDate", "30");
			signMap.put("ProductID", "");
			signMap.put("ProductName", subject);
			signMap.put("UnitPrice", String.valueOf(totalFee));
			signMap.put("Qty", "1");
			signMap.put("ProductRemarks", subject + "-农行支付");
			signMap.put("ProductType", "酒类消费");
			signMap.put("ProductDiscount", "0.0");
			signMap.put("PaymentType", paymentType);
			signMap.put("PaymentLinkType", paymentLinkType);
			signMap.put("NotifyType", "1");
			signMap.put("ResultNotifyURL", merUrl);
			signMap.put("IsBreakAccount", "0");

			String orderDate = DateUtils.DateTimeToYYYYMMDDhhmmss();
			Map<String, String> extend = new HashMap<>();
			extend.put("merUrl", merUrl);
			extend.put("merVAR", "emaotai.cn.epay");
			extend.put("orderDate", orderDate);
			extend.put("buildData", ToolsUtil.mapToJson(signMap));
			extend.put("shopCode", MerchantID);
			
			///5、写入数据库epay的epay_oder_detail表中
			logger.debug("[abcPay]保存数据，调用insertPayOrderDetail()");
			Integer retInt = insertPayOrderDetail(upTranData, upExtend, dbExtend, extend);
			logger.debug("保存detail表状态：{}", retInt);
			
			//6、发起支付请求
			Map<String, Object> payMap = new HashMap<String, Object>();
			JSON json = tPaymentRequest.extendPostRequest(Integer.valueOf(configIndex));
			String ReturnCode = json.GetKeyValue("ReturnCode");
			String ErrorMessage = json.GetKeyValue("ErrorMessage");
			logger.debug("[abc_pay] ReturnCode   = [" + ReturnCode + "]");
			logger.debug("[abc_pay] ErrorMessage = [" + ErrorMessage + "]");
			if (ReturnCode.equals("0000")) {
				logger.debug("[abc_pay] PaymentURL-->" + json.GetKeyValue("PaymentURL"));
				payMap.put("PaymentURL", json.GetKeyValue("PaymentURL"));
			} else {
				throw new Exception("支付失败：" + ErrorMessage);
			}
			return payMap;
		} else {
			throw new Exception("获取支付参数为空！");
		}
	}

	@Override
	public String notify(Map<String, String> mParam, String payCompany) throws Exception {
		try {
			String OrderNo = MapUtils.getString(mParam, "OrderNo");//mParam.get("OrderNo").toString();
			String Amount = MapUtils.getString(mParam, "Amount");//mParam.get("Amount").toString();
			String BatchNo = MapUtils.getString(mParam, "BatchNo");//mParam.get("BatchNo").toString();

			Map<String, Object> rd2 = new HashMap<String, Object>();
			rd2.put("orderid", OrderNo);
			rd2.put("payCompany", payCompany);
			List<Map<String, Object>> list = iEpayOrderDetailDao.Select(rd2);
			String resultUrl = "", interfaceName = "", amount = "", sysId = "";
			if (list != null && list.size() > 0) {
				Map<String, Object> map2 = list.get(0);
				resultUrl = MapUtils.getString(map2, "ResultUrl");
				interfaceName = MapUtils.getString(map2, "payCompany"); //map2.get("payCompany").toString();
				amount =  MapUtils.getString(map2, "amount"); //map2.get("amount").toString();
				sysId =  MapUtils.getString(map2, "Emt_sys_id"); //map2.get("amount").toString();
			} else {
				logger.debug("订单号：" + OrderNo + "不存在！");
				throw new Exception("订单号：" + OrderNo + "不存在！");
			}

			//状态查询
			Map<String, String> queryMap = iPayQueryApiSv.queryFromABC(sysId, OrderNo);
			logger.debug("[abcpay notify]查询结果：" + queryMap);
			if (null == queryMap || queryMap.size() == 0) {
				logger.debug("[abcpay notify]农行支付失败，农行没有查询到订单数据");
				throw new Exception("农行支付失败，农行没有查询到订单数据");
			}
			String qStatus = MapUtils.getString(queryMap, "status");
			//"01":"未支付";"02":"无回应";"03":"已请款";"04":"成功";"05":"已退款";"07":"授权确认成功";"00":"授权已取消";"99":"失败";
			if ("04".equals(qStatus)) {
				String tranState = "04".equals(qStatus) ? "1" : qStatus;
				//region 更新Epay的数据
				String notify_time = DateUtils.DateTimeToYYYYMMDDhhmmss(new Date());
				Map<String, Object> rd = new HashMap<String, Object>();
				rd.put("TranSerialNo", BatchNo);
				rd.put("notifyDate", notify_time);
				rd.put("tranStat", tranState);
				rd.put("notifyData", ToolsUtil.mapToJson(mParam));
				rd.put("comment", "支付成功");
				rd.put("orderid", OrderNo);
				rd.put("payCompany", payCompany);
				iEpayOrderDetailDao.Update(rd);

				//通知应用系统
				Map<String, Object> pstMap = new HashMap<String, Object>();
				pstMap.put("orderId", OrderNo);
				pstMap.put("discountAmount", Integer.valueOf(0));
				pstMap.put("resultUrl", resultUrl);
				postPayResult(pstMap);

				//组装返回参数
				Map<String, String> pMap = new HashMap<String, String>();
				pMap.put("orderId", OrderNo);
				pMap.put("payCompany", payCompany);
				pMap.put("amount", amount);
				pMap.put("tranStat", tranState);
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
				logger.debug("[abcpay notify]农行支付失败，订单未支付，状态：" + qStatus);
				throw new Exception("农行支付失败，订单未支付，状态：" + qStatus);
			}
		} catch (Exception e) {
			throw new Exception("农行通知处理出错，Error：" + e.getMessage());
		}
	}

	/**
	 * 获取日期
	 * @param date
	 * @param format
	 * @return
	 */
	public String getDateTime(Date date, String format){
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		if (null == date) {
			date = new Date();
		}
		return dateFormat.format(date);
	}


	/**
	 * 获取截止时间（当前时间往前flag分钟）
	 * @param date
	 * @param flag
	 * @return
	 */
	public String getOrderTimeoutDate(Date date, Integer flag){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		date.setTime(date.getTime() + (1000 * 60) * flag);//当前时间往后flag分钟
		return dateFormat.format(date);
	}

	/**
	 *
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
