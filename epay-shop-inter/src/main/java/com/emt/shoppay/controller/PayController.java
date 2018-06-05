package com.emt.shoppay.controller;

import com.emt.shoppay.pojo.CalendarUtil;
import com.emt.shoppay.pojo.ReturnObject;
import com.emt.shoppay.sv.impl.ValidataSvImpl;
import com.emt.shoppay.sv.inter.*;
import com.emt.shoppay.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * 支付入口
* @ClassName: PayController 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author huangdafei
* @date 2017年5月5日 下午4:21:10 
*
 */
@Controller
@RequestMapping("/epay/pay")
public class PayController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(name = "validataSvImpl", type = ValidataSvImpl.class)
	private IValidataSv validataSv;

	@Resource
	private IEpayOrderDetailSv iEpayOrderDetailSv;

	@Resource
	private IIcbcManagerSv iIcbcManagerSv;
	
	@Resource
	private IAbcManagerSv iAbcManagerSv;
	
	@Resource
	private IAlipayManagerSv iAlipayManagerSv;
	
	@Resource
	private ICcbManagerSv iCcbManagerSv;
	
	@Resource
	private IUnionpayManagerSv iUnionpayManagerSv;

	@Autowired
	private IBocManagerSv iBocManagerSv;

	@SuppressWarnings("unchecked")
	@LogAnnotation(name = "WriteLog", val = true, describe = "支付开始")
	@RequestMapping(value = "/orderPay2")
	public String orderPay2(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "interfaceName", required = true) String interfaceName,// 接口名称
			@RequestParam(value = "interfaceVersion", required = true) String interfaceVersion,// 接口版本号
			@RequestParam(value = "qid", required = true) Long qid,// 返回结果序号
			@RequestParam(value = "tranData", required = true) String tranData,// 接口参数数据
			@RequestParam(value = "signData", required = true) String signData,// 签名
			@RequestParam(value = "clientType", required = true) String clientType,// 客户端类型
			@RequestParam(value = "busiid", required = true) String busiid,// 业务ID
			@RequestParam(value = "sysId", required = true) String sysId,// 系统ID
			ModelMap model) {
		logger.debug(tranData);
		if ("1.0.0.2".equals(interfaceVersion)) {
			try {
				//检查订单是否在可支付时段
				Date date = new Date();
				Calendar cal = Calendar.getInstance();
				boolean isPay = CalendarUtil.checkNonPaymentDate(date, cal);
				logger.debug("[orderPay2] 是否月底结算时段：" + isPay);
				if(isPay) {//检查不能支付的时段
					logger.debug("[orderPay2] 每月最后一日22:00至次日凌晨02:00点为月底结算时段，系统不能进行支付，给您带来的不便，我们深表歉意！");
					model.put("errormsg", "每月最后一日22:00至次日凌晨02:00点为月底结算时段，系统不能进行支付，给您带来的不便，我们深表歉意！");
					return "/epay/error";
				}
				
				//数据解密并判断正确性
				String deTranData = Base64Util.decodeBase64(tranData, "UTF-8");
				ObjectMapper mapper = new ObjectMapper();
				Map<String, String> map = mapper.readValue(deTranData, Map.class);
				// 验证数据正确性
				if (!validataSv.ValidataData(busiid, map, signData)) {
					logger.debug("[orderPay2] 错误编码：0011，错误原因：非法的交易数据！Sign验证未通过");
					model.put("errormsg", "错误编码：0011，错误原因：非法的交易数据！Sign验证未通过");
					return "/epay/error";
				}
				
				if (StringUtils.isEmpty(sysId)) {
					logger.debug("[orderPay2] 错误编码：0012，错误原因：系统Id为空");
					model.put("errormsg", "错误编码：0012，错误原因：系统Id为空");
					return "/epay/error";
				}
				
				logger.debug("[orderPay2] 系统：" + sysId + "，订单开始支付");
				// 验证是否已经在支付中心支付过了
				String orderId = map.get("orderId");
				Map<String, Object> rd = new HashMap<String, Object>();
				rd.put("orderid", orderId);
				rd.put("tranStat", "1");
				//检测是否已支付
				List<Map<String, Object>> orderMap = iEpayOrderDetailSv.Select(rd);
				if (null != orderMap && orderMap.size() > 0) {
					logger.debug("[orderPay2] 订单{}已经支付，不可再进行支付。", orderId);
					model.put("errormsg", "订单:" + orderId + "已经支付，不可再进行支付。");
					return "/epay/error";
				}
				//构建支付数据
				String serverName = request.getServerName();
				Map<String, Object> upExtend = new HashMap<>();
				upExtend.put("interfaceName", interfaceName);// interfaceName,// 接口名称
				upExtend.put("interfaceVersion", interfaceVersion);
				upExtend.put("qid", qid);
				upExtend.put("clientType", clientType);
				upExtend.put("merReference", serverName);
				upExtend.put("busiid", busiid);
				upExtend.put("sysId", sysId);
				String result = null;
				switch (interfaceName.toLowerCase()) {
//				case "icbc_wap":
//					result = icbcWap(map, upExtend, model);
//					break;
				case "icbc_pc":
					result = icbcPc(map, upExtend, model);
					break;
//				case "abc_pay_wap":
//					result = abcWap(map, upExtend, model);
//					break;
				case "abc_pay_pc":
					result = abcWap(map, upExtend, model);
					break;
				case "alipay_wap":
					result = alipayWap(map, upExtend, model);
					break;
				case "alipay_pc":
					result = alipayPc(map, upExtend, model);
					break;
				case "ccb_pay":
					result = ccbPay(map, upExtend, model);
					break;
				case "unionpay_emt"://官方商城
					result = unionpay(map, upExtend, model);
					break;
				case "boc_b2c_pc"://官方中行
					result = bocPayPcB2C(map, upExtend, model);
					break;
				default:
					model.put("errormsg", "没有相应支付方式，请检查。");
					logger.debug("[orderPay2] 没有相应支付方式，请检查。");
					result = "/epay/error";
					break;
				}
				if (null == result || "".equals(result)) {
					logger.debug("[orderPay2] 错误编码：0011，错误原因：非法的交易数据！未返回任何数据！");
					model.put("errormsg", "错误编码：0011，错误原因：非法的交易数据！未返回任何数据！");
					return "/epay/error";
				}
				return result;
			} catch (Exception e) {
				model.put("errormsg", "错误编码：0011，错误原因：非法的交易数据！" + e.getMessage());
				logger.debug("[orderPay2] 错误编码：0011，错误原因：非法的交易数据！" + e.getMessage());
				return "/epay/error";
			}
		} else {
			model.put("errormsg", "错误编码：0011，错误原因：非法的交易数据！版本号不正确!!!");
			logger.debug("[orderPay2] 错误编码：0011，错误原因：非法的交易数据！版本号不正确!!!");
			return "/epay/error";
		}
	}
	
	/**
	 * 收银台查询接口
	* @Title: queryOrders 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param request
	* @param @param response
	* @param @param orderId
	* @param @return
	* @param @throws Exception  参数说明 
	* @return ReturnObject    返回类型 
	* @throws
	 */
	@RequestMapping(value = "/queryOrders")
	public @ResponseBody
	ReturnObject queryOrders(HttpServletRequest request,
							 HttpServletResponse response, String orderId) throws Exception{
		Map<String, Object> rd = new HashMap<String, Object>();
		logger.debug("[PayController queryOrders] orderId:{}", orderId);
		logger.debug("[PayController queryOrders] tranStat:{}", 1);
		rd.put("orderid", orderId);
		rd.put("tranStat", 1);
		List<Map<String, Object>> list = iEpayOrderDetailSv.Select(rd);
		logger.debug("[PayController queryOrders] list:{}", list);
		if(list == null || list.size() == 0){
			return new ReturnObject(ReturnObject.SuccessEnum.success, "0", 0, 1);
		} else {
			Map<String, Object> orderMap = list.get(0);
			String amount = orderMap.get("amount").toString();
			String tranStat = orderMap.get("tranStat").toString();
			String payCompany = orderMap.get("payCompany").toString();
			String resultUrl = orderMap.get("ResultUrl").toString();
			
			Map<String, String> map = new HashMap<>();
			map.put("orderid", orderId);
			map.put("status", tranStat);
			map.put("amount", amount);
			map.put("interfaceName", payCompany);
			map.put("resultUrl", resultUrl);
			
			String json = "amount=" + amount + "&orderId=" + orderId + "&status=" + tranStat;
			String sign = validataSv.getValidataString("10001", json);
			map.put("sign", sign);
			
			return new ReturnObject(ReturnObject.SuccessEnum.success, "1", map, 1);
		}
	}

	/**
	 * 应用系统主动查询
	* @Title: orderQuery 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param request
	* @param @param response
	* @param @param interfaceName
	* @param @param interfaceVersion
	* @param @param qid
	* @param @param tranData
	* @param @param signData
	* @param @param clientType
	* @param @param busiid
	* @param @param model  参数说明 
	* @return void    返回类型 
	* @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/orderQuery")
	public void orderQuery(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "interfaceName", required = true) String interfaceName,// 接口名称
			@RequestParam(value = "interfaceVersion", required = true) String interfaceVersion,// 接口版本号
			@RequestParam(value = "qid", required = true) Long qid,// 返回结果序号
			@RequestParam(value = "tranData", required = true) String tranData,// 接口参数数据
			@RequestParam(value = "signData", required = true) String signData,// 签名
			@RequestParam(value = "clientType", required = true) String clientType,// 客户端类型
			@RequestParam(value = "busiid", required = true) String busiid,// 业务ID
			ModelMap model) {
		ReturnObject returnObject = new ReturnObject();
		PrintWriter out;
		try {
			out = response.getWriter();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		// 目前仅有1.0.0.2版本
		if ("1.0.0.2".equals(interfaceVersion)) {
			try {
				Map<String, String> map = null;

				tranData = Base64Util.decodeBase64(tranData, "UTF-8");

				ObjectMapper mapper = new ObjectMapper();
				map = mapper.readValue(tranData, Map.class);

				// 验证数据正确性
				if (!validataSv.ValidataData(busiid, map, signData)) {
					returnObject.setRetcode(11);
					returnObject.setRetmsg("错误原因：非法的交易数据！");
					out.write(returnObject.toJson());
					return;
				}

				String orderId = map.get("orderId");

				Map<String, Object> rd = new HashMap<String, Object>();
				rd.put("orderid", orderId);
//				rd.put(BaseSqlBuilder.getOrderByField(), "tranStat desc");
				List<Map<String, Object>> orderList = iEpayOrderDetailSv.Select(rd);

				if (orderList.size() == 0) {
					returnObject.setRetcode(12);
					returnObject.setRetmsg("无此订单的支付记录！");
					out.write(returnObject.toJson());
					return;
				}

				String tranStat = "";
				String payCompany = "";

				for (int i = 0; i < orderList.size(); i++) {
					Map<String, Object> order = orderList.get(i);
					tranStat = order.get("tranStat").toString();
					payCompany = order.get("payCompany").toString();
					if ("1".equals(tranStat)) {
						// 已经支付成功了
						returnObject.setRetcode(ReturnObject.SuccessEnum.success.getSuccessEnum());
						returnObject.setRetmsg("成功");

						Long amount = (Long) order.get("amount");


						Map<String, String> pMap = new HashMap<String, String>();
					    pMap.put("orderId", orderId);
					    pMap.put("payCompany", payCompany);
					    pMap.put("amount", String.valueOf(amount));
					    pMap.put("tranStat", "1");
					    pMap.put("discountAmount", "0.00");
					    pMap.put("interfaceName", interfaceName);
						
						String tranMapJson = ToolsUtil.mapToJson(pMap);
						tranData = Base64Util.encodeBase64(tranMapJson);
						String sign = validataSv.getValidataString("10001", tranData);
						
						Map<String, Object> paramMap = new HashMap<>();
						paramMap.put("tranData", tranData);
						paramMap.put("sign", sign);
						
						returnObject.setData(paramMap);
						returnObject.setCount(1);

						out.write(returnObject.toJson());
						return;
					}
				}
				// 未支付过
				returnObject.setRetcode(11003);
				returnObject.setRetmsg("未支付");

				Map<String, String> pMap = new HashMap<String, String>();
			    pMap.put("orderId", orderId);
			    pMap.put("payCompany", payCompany);
			    pMap.put("amount", "0");
			    pMap.put("tranStat", "0");
			    pMap.put("discountAmount", "0.00");
			    pMap.put("interfaceName", interfaceName);
				
				String tranMapJson = ToolsUtil.mapToJson(pMap);
				tranData = Base64Util.encodeBase64(tranMapJson);
				String sign = validataSv.getValidataString("10001", tranData);
				
				Map<String, Object> paramMap = new HashMap<>();
				paramMap.put("tranData", tranData);
				paramMap.put("sign", sign);
				
				returnObject.setData(paramMap);
				returnObject.setCount(1);
				
				out.write(returnObject.toJson());
				return;

			} catch (Exception e) {
				returnObject.setRetcode(11002);
				returnObject.setRetmsg(e.getMessage());
				out.write(returnObject.toJson());
				return;
			}
		}
		returnObject.setRetcode(11001);
		returnObject.setRetmsg("错误原因：版本号错误！");
		out.write(returnObject.toJson());
	}
	
	//**********************************
	private String icbcWap(Map<String, String> tranMap, Map<String, Object> upExtend, ModelMap model){
		try {
			Map<String, Object> mReturn = iIcbcManagerSv.icbcPayWapB2C(tranMap, upExtend);
			if (null == mReturn) {
				model.put("errormsg", "获取支付关键失败，请检查。");
				logger.debug("[orderPay2] 获取支付关键失败，请检查。");
				return "/epay/error";
			}
			model.put("interfaceName", mReturn.get("interfaceName"));
			model.put("interfaceVersion", mReturn.get("interfaceVersion"));
			model.put("tranData", mReturn.get("tranData"));
			model.put("merSignMsg", mReturn.get("merSignMsg"));
			model.put("merCert", mReturn.get("merCert"));
			model.put("clientType", mReturn.get("clientType"));
			model.put("wap_b2c_url", mReturn.get("req_url"));
			return "/epay/icbc_wap";
		} catch (Exception e) {
			model.put("errormsg", "支付失败！" + e.getMessage());
			logger.debug("[orderPay2] 支付失败！" + e.getMessage());
			return "/epay/error";
		}
	}
	
	private String icbcPc(Map<String, String> tranMap, Map<String, Object> upExtend, ModelMap model){
		try {
			Map<String, Object> mReturn = iIcbcManagerSv.icbcPayPcB2C(tranMap, upExtend);
			if (null == mReturn) {
				model.put("errormsg", "获取支付关键失败，请检查。");
				logger.debug("[orderPay2] 获取支付关键失败，请检查。");
				return "/epay/error";
			}
			model.put("interfaceName", mReturn.get("interfaceName"));
			model.put("interfaceVersion", mReturn.get("interfaceVersion"));
			model.put("tranData", mReturn.get("tranData"));
			model.put("merSignMsg", mReturn.get("merSignMsg"));
			model.put("merCert", mReturn.get("merCert"));
			model.put("clientType", mReturn.get("clientType"));
			model.put("b2c_url", mReturn.get("req_url"));
			return "/epay/icbc_pc";
		} catch (Exception e) {
			model.put("errormsg", "支付失败！" + e.getMessage());
			logger.debug("[orderPay2] 支付失败！" + e.getMessage());
			return "/epay/error";
		}
	}
	
	private String abcWap(Map<String, String> tranMap, Map<String, Object> upExtend, ModelMap model){
		try {
			Map<String, Object> mReturn = iAbcManagerSv.abcPay(tranMap, upExtend);
			if (null == mReturn) {
				model.put("errormsg", "获取支付关键失败，请检查。");
				logger.debug("[orderPay2] 获取支付关键失败，请检查。");
				return "/epay/error";
			}
			model.put("PaymentURL", mReturn.get("PaymentURL"));
			return "/epay/abc_pay";
		} catch (Exception e) {
			model.put("errormsg", "支付失败！" + e.getMessage());
			logger.debug("[orderPay2] 支付失败！" + e.getMessage());
			return "/epay/error";
		}
	}
	
	private String alipayWap(Map<String, String> tranMap, Map<String, Object> upExtend, ModelMap model){
		try {
			Map<String, Object> mReturn = iAlipayManagerSv.alipayWap(tranMap, upExtend);
			if (null == mReturn) {
				model.put("errormsg", "获取支付关键失败，请检查。");
				logger.debug("[orderPay2] 获取支付关键失败，请检查。");
				return "/epay/error";
			}
			model.put("submitUrl", mReturn.get("submitUrl"));
			model.put("service", mReturn.get("service"));
			model.put("partner", mReturn.get("partner"));
			model.put("_input_charset", mReturn.get("_input_charset"));
			model.put("sign_type", mReturn.get("sign_type"));
			model.put("sign", mReturn.get("sign"));
			model.put("return_url", mReturn.get("return_url"));
			model.put("out_trade_no", mReturn.get("out_trade_no"));
			model.put("subject", mReturn.get("subject"));
			model.put("total_fee", mReturn.get("total_fee"));
			model.put("seller_id", mReturn.get("seller_id"));
			model.put("payment_type", mReturn.get("payment_type"));
			model.put("error_notify_url", mReturn.get("error_notify_url"));
			model.put("notify_url", mReturn.get("notify_url"));
			model.put("rn_check", "T");
			model.put("show_url", mReturn.get("show_url"));
			logger.debug("[alipayWap] model：{}", model);

			return "/epay/alipay_wap";
		} catch (Exception e) {
			model.put("errormsg", "支付失败！" + e.getMessage());
			logger.debug("[orderPay2] 支付失败！" + e.getMessage());
			return "/epay/error";
		}
	}
	
	private String alipayPc(Map<String, String> tranMap, Map<String, Object> upExtend, ModelMap model){
		try {
			Map<String, Object> mReturn = iAlipayManagerSv.alipayPc(tranMap, upExtend);
			if (null == mReturn) {
				model.put("errormsg", "获取支付关键失败，请检查。");
				logger.debug("[orderPay2] 获取支付关键失败，请检查。");
				return "/epay/error";
			}
			model.put("submitUrl", mReturn.get("submitUrl"));
			model.put("service", mReturn.get("service"));
			model.put("partner", mReturn.get("partner"));
			model.put("seller_email", mReturn.get("seller_email"));
			model.put("sign_type", mReturn.get("sign_type"));
			model.put("out_trade_no", mReturn.get("out_trade_no"));
			model.put("subject", mReturn.get("subject"));
			model.put("payment_type", mReturn.get("payment_type"));
			model.put("total_fee", mReturn.get("total_fee"));
			model.put("sign", mReturn.get("sign"));
			model.put("return_url", mReturn.get("return_url"));
			model.put("notify_url", mReturn.get("notify_url"));
			model.put("error_notify_url", mReturn.get("error_notify_url"));
			model.put("_input_charset", mReturn.get("_input_charset"));
			
//			model.put("seller_id", mReturn.get("seller_id"));
//			model.put("rn_check", "T");
//			model.put("show_url", mReturn.get("show_url"));
			logger.debug("[alipayPc] model：{}", model);

			return "/epay/alipay_pc";
		} catch (Exception e) {
			model.put("errormsg", "支付失败！" + e.getMessage());
			logger.debug("[orderPay2] 支付失败！" + e.getMessage());
			return "/epay/error";
		}
	}
	
	private String ccbPay(Map<String, String> tranMap, Map<String, Object> upExtend, ModelMap model){
		try {
			Map<String, Object> mReturn = iCcbManagerSv.ccbPay(tranMap, upExtend);
			if (null == mReturn) {
				model.put("errormsg", "获取支付关键失败，请检查。");
				logger.debug("[orderPay2] 获取支付关键失败，请检查。");
				return "/epay/error";
			}
			
			model.put("MERCHANTID", mReturn.get("MERCHANTID"));
			model.put("POSID", mReturn.get("POSID"));
			model.put("BRANCHID", mReturn.get("BRANCHID"));
			model.put("ORDERID", mReturn.get("ORDERID"));
			model.put("PAYMENT", mReturn.get("PAYMENT"));
			model.put("CURCODE", mReturn.get("CURCODE"));
			model.put("TXCODE", mReturn.get("TXCODE"));
			model.put("TYPE", mReturn.get("TYPE"));
			model.put("MAC", mReturn.get("MAC"));
			model.put("PROINFO", mReturn.get("PROINFO"));
			model.put("GATEWAY", mReturn.get("GATEWAY"));
			model.put("CLIENTIP", mReturn.get("CLIENTIP"));
			model.put("REGINFO", mReturn.get("REGINFO"));
			model.put("REMARK1", mReturn.get("REMARK1"));
			model.put("REMARK2", mReturn.get("REMARK2"));
			model.put("REFERER", mReturn.get("REFERER"));
			return "/epay/ccb_wap";
		} catch (Exception e) {
			model.put("errormsg", "支付失败！" + e.getMessage());
			logger.debug("[orderPay2] 支付失败！" + e.getMessage());
			return "/epay/error";
		}		
	}
	
	private String unionpay(Map<String, String> tranMap, Map<String, Object> upExtend, ModelMap model){
		try {
			Map<String, Object> mReturn = null;
			String interfaceName = upExtend.get("interfaceName").toString();
			if(interfaceName.toLowerCase().equals("unionpay_b2c_applepay") || interfaceName.toLowerCase().equals("unionpay_b2c_controls_pay")){
				mReturn = iUnionpayManagerSv.unionpayQuickPass(tranMap, upExtend);//云闪付
				if (null == mReturn) {
					model.put("errormsg", "获取支付关键参数失败，请检查。");
					logger.debug("[orderPay2] 获取支付关键参数失败，请检查。");
					return "/epay/error";
				}
				ReturnObject returnObject = (ReturnObject) mReturn.get("returnObject");
				return returnObject.toJson();
			} else {
				mReturn = iUnionpayManagerSv.unionpay(tranMap, upExtend);
				if (null == mReturn) {
					model.put("errormsg", "获取支付关键参数失败，请检查。");
					logger.debug("[orderPay2] 获取支付关键参数失败，请检查。");
					return "/epay/error";
				}
			}
			model.put("version", mReturn.get("version"));
			model.put("encoding", mReturn.get("encoding"));
			model.put("signMethod", mReturn.get("signMethod"));
			model.put("txnType", mReturn.get("txnType"));
			model.put("txnSubType", mReturn.get("txnSubType"));
			model.put("bizType", mReturn.get("bizType"));
			model.put("channelType", mReturn.get("channelType"));
			model.put("merId", mReturn.get("merId"));
			model.put("accessType", mReturn.get("accessType"));
			model.put("orderId", mReturn.get("orderId"));
			model.put("txnTime", mReturn.get("txnTime"));
			model.put("currencyCode", mReturn.get("currencyCode"));
			model.put("txnAmt", mReturn.get("txnAmt"));
			model.put("frontUrl", mReturn.get("frontUrl"));
			model.put("backUrl", mReturn.get("backUrl"));
			model.put("accType", mReturn.get("accType"));
			model.put("certId", mReturn.get("certId"));
			model.put("signature", mReturn.get("signature"));
			model.put("submitUrl", mReturn.get("submitUrl"));
			model.put("orderDesc", mReturn.get("orderDesc"));
			return "/epay/unop";
		} catch (Exception e) {
			model.put("errormsg", "支付失败！" + e.getMessage());
			logger.debug("[orderPay2] 支付失败！" + e.getMessage());
			return "/epay/error";
		}
	}

	private String bocPayPcB2C(Map<String, String> tranMap, Map<String, Object> upExtend, ModelMap model){
		try {
			Map<String, Object> mReturn = iBocManagerSv.bocPayPcB2C(tranMap, upExtend);
			if (null == mReturn) {
				model.put("errormsg", "获取支付关键失败，请检查。");
				logger.debug("[orderPay2] 获取支付关键失败，请检查。");
				return "/epay/error";
			}

			// 将参数放置到request对象
			model.put("merchantNo", MapUtils.getString(mReturn, "merchantNo"));
			model.put("payType", MapUtils.getString(mReturn, "payType"));
			model.put("orderNo", MapUtils.getString(mReturn, "orderNo"));
			model.put("curCode", MapUtils.getString(mReturn, "curCode"));
			model.put("orderAmount", MapUtils.getString(mReturn, "orderAmount"));
			model.put("orderTime", MapUtils.getString(mReturn, "orderTime"));
			model.put("orderNote", MapUtils.getString(mReturn, "orderNote"));
			model.put("orderUrl", MapUtils.getString(mReturn, "orderUrl"));
			model.put("orderTimeoutDate", MapUtils.getString(mReturn, "orderTimeoutDate"));
			model.put("mchtCustIP", MapUtils.getString(mReturn, "mchtCustIP"));
			model.put("signData", MapUtils.getString(mReturn, "signData"));
			model.put("action", MapUtils.getString(mReturn, "action"));
			logger.debug("[boc_pc_b2c] model：{}", model);

			return "/epay/boc_pc_b2c";
		} catch (Exception e) {
			model.put("errormsg", "支付失败！" + e.getMessage());
			logger.debug("[orderPay2] 支付失败！" + e.getMessage());
			return "/epay/error";
		}
	}

	@RequestMapping("index")
	public String index(){
		return "/index";
	}
}