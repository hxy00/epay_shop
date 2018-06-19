package com.emt.shoppay.controller;

import com.emt.shoppay.sv.inter.IIcbcManagerSv;
import com.emt.shoppay.util.LogAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/epay/icbc")
public class ICBCController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private IIcbcManagerSv iIcbcManagerSv;

	// 网银PC端b2c支付
//	private static String pc_b2c_url = "https://B2C.icbc.com.cn/servlet/ICBCINBSEBusinessServlet";
	// 手机B2C支付地址：
//	private static String wap_b2c_url = "https://mywap2.icbc.com.cn/ICBCWAPBank/servlet/ICBCWAPEBizServlet";

//	private static String epay_notify_b2b_url = "/epay/icbc/notify_wap_b2b";

//	private static String epay_notify_b2b_pc_url = "/epay/icbc/notify_pc_b2b";

//	@RequestMapping(value = "/wap_b2b_g")
//	public String wap_b2b_g(
//			HttpServletRequest request,
//			HttpServletResponse response,
//			@RequestParam(value = "interfaceName", required = true) String interfaceName,// 接口名称
//			@RequestParam(value = "interfaceVersion", required = true) String interfaceVersion,// 接口版本号
//			@RequestParam(value = "qid", required = true) Long qid,// 返回结果序号
//			@RequestParam(value = "tranData", required = true) String tranData,// 接口参数数据
//			@RequestParam(value = "signData", required = true) String signData,// 签名
//			@RequestParam(value = "clientType", required = true) String clientType,// 客户端类型
//			@RequestParam(value = "busiid", required = true) String busiid,// 业务ID
//			ModelMap model) {
//		try {
//			if (!validataSv.ValidataParamData(busiid, tranData, signData)) {
//				model.put("errormsg", "错误编码：0011，错误原因：非法的交易数据！");
//				return "/epay/error";
//			}
//
//			tranData = new String(BaseCoder.decryptBASE64(tranData), "UTF-8");
//
//			String orderid = "";
//			String orderDate = "";
//			Integer amount = 0;
//			String url = "";
//
//			ObjectMapper mapper = new ObjectMapper();
//			Map<String, Object> tranMap = mapper.readValue(tranData, Map.class);
//
//			if (tranMap.keySet().contains("orderId")
//					&& tranMap.get("orderId") != null) {
//				orderid = tranMap.get("orderId").toString();
//			}
//
//			if (tranMap.keySet().contains("url") && tranMap.get("url") != null) {
//				url = tranMap.get("url").toString();
//			}
//
//			List<Map<String, Object>> rdList = hishopOrderInSv
//					.SelectOrderInfo(orderid);
//
//			if (rdList != null && rdList.size() > 0) {
//				Map<String, Object> map = rdList.get(0);
//				orderDate = DateUtils.DateTimeToYYYYMMDDhhmmss();// 取当前时间
//				BigDecimal OrderTotalY = (BigDecimal) map.get("OrderTotal");// 单位：元
//				Double OrderTotalF = OrderTotalY.doubleValue() * 100f;// 分
//				amount = OrderTotalF.intValue();
//			} else {
//				model.put("errormsg", "错误编码：0011，错误原因：非法的交易数据！");
//				return "/epay/error";
//			}
//
//			IcbcOrderInfoWapVo orderDetailVo = new IcbcOrderInfoWapVo();
//
//			orderDetailVo.setOrderDate(orderDate);
//			orderDetailVo.setOrderid(orderid);
//			orderDetailVo.setAmount(amount);
//
//			String httpUrl = PaySetting.getICBCB2BNotifyUrl("wap");//getB2BServerUrl(request, "wap");
//			logger.debug("[pc_b2b_p] httpUrl:{}", httpUrl);
//			orderDetailVo.setMerURL(httpUrl);
//			orderDetailVo.setResultUrl(url);
//
//			IcbcOrderCommitVo icbcOrderCommitVo = icbcOrderPaySv
//					.ICBC_WAPB_B2B(orderDetailVo);
//
//			logger.debug("httpUrl=\n\r{}\n\r", httpUrl);
//
//			model.put("interfaceName", interfaceName);
//			model.put("interfaceVersion", interfaceVersion);
//			model.put("tranData", icbcOrderCommitVo.getTranData());
//			model.put("merSignMsg", icbcOrderCommitVo.getMerSignMsg());
//			model.put("merCert", icbcOrderCommitVo.getMerCert());
//			model.put("clientType", icbcOrderCommitVo.getClientType());
////			model.put("wap_b2c_url", wap_b2c_url);
//			model.put("wap_b2c_url", PaySetting.icbc_wap_b2c_url);
//
//			return "/epay/icbc_wap";// 最后的参数为false代表以post方式提交请求
//		} catch (Exception e) {
//			logger.debug("订单支付失败，error={}", e);
//			model.put("errormsg", "错误编码：0012，错误原因：内部处理错误！");
//			return "/epay/error"; // 最后的参数为false post; true get方式提交请求
//		}
//	}
//
//	@RequestMapping(value = "/pc_b2b_p")
//	public String pc_b2b_p(
//			HttpServletRequest request,
//			HttpServletResponse response,
//			@RequestParam(value = "interfaceName", required = true) String interfaceName,// 接口名称
//			@RequestParam(value = "interfaceVersion", required = true) String interfaceVersion,// 接口版本号
//			@RequestParam(value = "qid", required = true) Long qid,// 返回结果序号
//			@RequestParam(value = "tranData", required = true) String tranData,// 接口参数数据
//			@RequestParam(value = "signData", required = true) String signData,// 签名
//			@RequestParam(value = "clientType", required = true) String clientType,// 客户端类型
//			@RequestParam(value = "busiid", required = true) String busiid,// 业务ID
//			ModelMap model) {
//		try {
//			if (!validataSv.ValidataParamData(busiid, tranData, signData)) {
//				model.put("errormsg", "错误编码：0011，错误原因：非法的交易数据！");
//				return "/epay/error";
//			}
//
//			tranData = new String(BaseCoder.decryptBASE64(tranData), "UTF-8");
//
//			String orderId = "";
//			String orderDate = "";
//			Integer amount = 0;
//			String url = "";
//			ObjectMapper mapper = new ObjectMapper();
//			Map<String, Object> tranMap = mapper.readValue(tranData, Map.class);
//
//			if (tranMap.keySet().contains("orderId")
//					&& tranMap.get("orderId") != null) {
//				orderId = tranMap.get("orderId").toString();
//			}
//
//			if (tranMap.keySet().contains("url") && tranMap.get("url") != null) {
//				url = tranMap.get("url").toString();
//			}
//
//			List<Map<String, Object>> rdList = hishopOrderInSv
//					.SelectOrderInfo(orderId);
//
//			if (rdList != null && rdList.size() > 0) {
//				Map<String, Object> map = rdList.get(0);
//				orderDate = DateUtils.DateTimeToYYYYMMDDhhmmss();// 取当前时间
//				BigDecimal OrderTotalY = (BigDecimal) map.get("OrderTotal");// 单位：元
//				Double OrderTotalF = OrderTotalY.doubleValue() * 100f;// 分
//				amount = OrderTotalF.intValue();
//			} else {
//				return "/epay/error";
//			}
//
//			IcbcOrderInfoPcVo orderDetailVo = new IcbcOrderInfoPcVo();
//			orderDetailVo.setOrderDate(orderDate);
//			orderDetailVo.setOrderid(orderId);
//			orderDetailVo.setAmount(amount);
//			orderDetailVo.setResultUrl(url);
//			orderDetailVo.setQid(qid);
//
//			String strReference = request.getServerName();
//			logger.debug("[pc_b2b_p] strReference:{}", strReference);
//			orderDetailVo.setMerReference(strReference);
//
//			String httpUrl = PaySetting.getICBCB2BNotifyUrl("pc");//getB2BServerUrl(request, "pc");
//			logger.debug("[pc_b2b_p] httpUrl:{}", httpUrl);
//			orderDetailVo.setMerURL(httpUrl);
//			System.out.println(orderDetailVo.toTranData());
//
//			IcbcOrderCommitVo icbcOrderCommitVo = icbcOrderPaySv
//					.ICBC_PERBANK_B2B(orderDetailVo);
//
//			model.put("interfaceName", interfaceName);
//			model.put("interfaceVersion", interfaceVersion);
//			model.put("tranData", icbcOrderCommitVo.getTranData());
//			model.put("merSignMsg", icbcOrderCommitVo.getMerSignMsg());
//			model.put("merCert", icbcOrderCommitVo.getMerCert());
////			model.put("b2c_url", pc_b2c_url);
//			model.put("b2c_url", PaySetting.icbc_pc_b2c_url);
//
//			return "/epay/icbc_pc";// //最后的参数为false代表以post方式提交请求
//		} catch (Exception e) {
//			logger.debug("订单支付失败，error={}", e);
//			return "/epay/error";
//		}
//	}

	/**
	 * 通知接收地址
	 * @param request
	 * @param response
	 * @param merVAR
	 * @param notifyData
	 * @param signMsg
	 * @param model
	 * @throws IOException
	 */
	@LogAnnotation(name = "WriteLog", val = true, describe = "工行线上POS支付成功同步回调")
	@RequestMapping(value = "/notify_wap_b2c")
	public void notify_wap_b2c(HttpServletRequest request,
			HttpServletResponse response, String merVAR,// 返回商户变量
			String notifyData,// 通知结果数据
			String signMsg,// 银行对通知结果的签名数据
			ModelMap model) {
		try {
			logger.debug("[notify_wap_b2c] merVAR={}", merVAR);
			logger.debug("[notify_wap_b2c] notifyData={}", notifyData);
			logger.debug("[notify_wap_b2c] signMsg={}", signMsg);
//			url = icbcOrderPaySv.notify_wap_b2c(merVAR, notifyData, signMsg);
			Map<String, String> mParam = new HashMap<String, String>();

			mParam.put("notifyData", notifyData);
			mParam.put("signMsg", signMsg);
			String url = iIcbcManagerSv.notifyWapB2C(mParam);

			logger.debug("[notify_wap_b2c print] url:{}", url);
			response.getWriter().print(url);
		} catch (Exception e) {
			logger.debug("[notify_wap_b2c] 订单支付失败，error={}", e);
		}
	}

	@LogAnnotation(name = "WriteLog", val = true, describe = "工行线上POS支付成功异步回调")
	@RequestMapping(value = "/notify_pc_b2c")
	public void notify_pc_b2c(HttpServletRequest request,
			HttpServletResponse response, String merVAR,// 返回商户变量
			String notifyData,// 通知结果数据
			String signMsg,// 银行对通知结果的签名数据
			ModelMap model) throws Exception {
		try {
			logger.debug("[notify_pc_b2c] merVAR={}", merVAR);
			logger.debug("[notify_pc_b2c] notifyData={}", notifyData);
			logger.debug("[notify_pc_b2c] signMsg={}", signMsg);
//			url = icbcOrderPaySv.notify_pc_b2c(merVAR, notifyData, signMsg);

			Map<String, String> mParam = new HashMap<String, String>();
			mParam.put("notifyData", notifyData);
			mParam.put("signMsg", signMsg);
			String url = iIcbcManagerSv.notifyPcB2C(mParam);

			logger.debug("[notify_pc_b2c print] url:{}", url);
			response.getWriter().print(url);
		} catch (Exception e) {
			logger.debug("[notify_pc_b2c] 订单支付失败，error={}", e);
		}
	}

	// @RequestMapping(value = "/notify_pc_b2b")
	// public void notify_pc_b2b(HttpServletRequest request,
	// HttpServletResponse response, String merVAR,// 返回商户变量
	// String notifyData,// 通知结果数据
	// String signMsg,// 银行对通知结果的签名数据
	// ModelMap model) throws IOException {
	// String url = "";
	// try {
	// logger.debug("merVAR={}, notifyData={}, signMsg={}", merVAR,
	// notifyData, signMsg);
	// url = icbcOrderPaySv.notify_pc_b2c(merVAR, notifyData, signMsg);
	// // return "success";
	// } catch (Exception e) {
	// logger.debug("订单支付失败，error={}", e);
	// // return "fail" ;
	// }
	// response.getWriter().print(url);
	// }

	// 通知接收地址
	// http://192.168.10.222:8080/eMaotai/epay/icbc/OrderLookByOrderid?orderDate=20151126164059&orderid=20151126164059001
//	@RequestMapping(value = "/OrderLookByOrderid")
//	@ResponseBody
//	public ReturnObject OrderLookByOrderid(HttpServletRequest request,
//			HttpServletResponse response, String orderDate, // 订单日期
//			String orderid,// 订单id
//			String sysId,
//			ModelMap model) throws IOException {
//		ReturnObject returnObject = new ReturnObject();
//		Map<String, Object> map = null;
//		try {
//			logger.debug("orderDate={}, orderid={}, signMsg={}", orderDate, orderid);
//			map = iIcbcManagerSv.queryOrderFromBank(sysId, orderid, orderDate);
//			returnObject.setData(map);
//			returnObject.setCount(1);
//		} catch (Exception e) {
//			logger.debug("订单支付失败，error={}", e);
//			returnObject.setRetcode(11001);
//			returnObject.setRetmsg(e.getMessage());
//		}
//		return returnObject;
//	}

}
