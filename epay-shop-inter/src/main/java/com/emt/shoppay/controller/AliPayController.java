package com.emt.shoppay.controller;

import com.emt.shoppay.sv.inter.IAlipayManagerSv;
import com.emt.shoppay.util.LogAnnotation;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/epay/alipay")
public class AliPayController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private IAlipayManagerSv iAlipayManagerSv;

	// 支付宝支付成功以后返回
	@LogAnnotation(name = "WriteLog", val = true, describe = "支付宝支付成功后同步回调-pc")
	@RequestMapping(value = "/notify_pc")
	public String notify_pc(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		logger.debug("支付宝同步回调执行！-pc");
		try {
			Map<String, String> params = new HashMap<String, String>();
			Enumeration<String> list = request.getParameterNames();
			while (list.hasMoreElements()) {
				String key = list.nextElement();
				params.put(key, request.getParameter(key));
			}
			logger.debug("支付宝同步回调执行参数：" + params);
			String url = iAlipayManagerSv.notifyFront(params, "alipay_pc");
			logger.debug("支付宝同步回调执行成功，跳转页面：" + url);
			if (TextUtils.isEmpty(url)){
				logger.debug("支付宝同步回调订单支付失败，跳转地址（resultUrl）为空，请检查。");
				model.put("errormsg", "支付宝同步回调订单处理失败");
				return "/epay/error";
			}
			model.put("url", url);
//			response.getWriter().print("success");
		} catch (Exception e) {
			logger.debug("支付宝同步回调订单支付失败，error={}", e.getMessage());
			model.put("errormsg", "支付宝同步回调订单处理失败：" + e.getMessage());
			return "/epay/error";
		}
		return "/epay/gotoBack";
	}

	@LogAnnotation(name = "WriteLog", val = true, describe = "支付宝支付成功后异步回调-pc")
	@RequestMapping(value = "/notify_asyn_pc")
	public void notify_asyn_pc(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		logger.debug("支付宝异步请求回调执行！-pc");
		try {
			Map<String, String> params = new HashMap<String, String>();
			Enumeration<String> list = request.getParameterNames();
			while (list.hasMoreElements()) {
				String key = list.nextElement();
				params.put(key, request.getParameter(key));
			}
			logger.debug("支付宝异步请求回调执行参数：" + params);
			String url = iAlipayManagerSv.notifyBack(params, "alipay_pc");
			logger.debug("支付宝同步回调执行成功，跳转页面：" + url);
			if (TextUtils.isEmpty(url)){
				logger.debug("支付宝同步回调订单支付失败，跳转地址（resultUrl）为空，请检查。");
				model.put("errormsg", "支付宝同步回调订单处理失败");
				return;
			}
			logger.debug("支付宝异步回调执行成功，result:{}", url);
//			response.getWriter().print("success");
		} catch (Exception e) {
			logger.debug("支付宝异步回调执行失败，Error:{}", e.getMessage());
		}
	}
	
	// 支付宝支付成功以后返回
		@LogAnnotation(name = "WriteLog", val = true, describe = "支付宝支付成功后同步回调-wap")
		@RequestMapping(value = "/notify_wap")
		public String notify_wap(HttpServletRequest request,
				HttpServletResponse response, ModelMap model) {
			logger.debug("支付宝同步回调执行！-wap");
			try {
				Map<String, String> params = new HashMap<String, String>();
				Enumeration<String> list = request.getParameterNames();
				while (list.hasMoreElements()) {
					String key = list.nextElement();
					params.put(key, request.getParameter(key));
					logger.debug("支付宝同步回调执行参数：" + params);
				}
				String url = iAlipayManagerSv.notifyFront(params, "alipay_wap");
				if (TextUtils.isEmpty(url)){
					logger.debug("支付宝同步回调订单支付失败，跳转地址（resultUrl）为空，请检查。");
					model.put("errormsg", "支付宝同步回调订单处理失败");
					return "/epay/error";
				}
				model.put("url", url);
				logger.debug("支付宝同步回调执行成功，跳转页面：" + url);
//				response.getWriter().print("success");
			} catch (Exception e) {
				logger.debug("支付宝同步回调订单支付失败，error={}", e.getMessage());
				model.put("errormsg", "支付宝同步回调订单处理失败:" + e.getMessage());
				return "/epay/error";
			}
			return "/epay/gotoBack";
		}


		@LogAnnotation(name = "WriteLog", val = true, describe = "支付宝支付成功后异步回调-wap")
		@RequestMapping(value = "/notify_asyn_wap")
		public void notify_asyn_wap(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
			logger.debug("支付宝异步请求回调执行！-wap");
			try {
				Map<String, String> params = new HashMap<String, String>();
				Enumeration<String> list = request.getParameterNames();
				while (list.hasMoreElements()) {
					String key = list.nextElement();
					params.put(key, request.getParameter(key));
					logger.debug("支付宝异步请求回调执行参数：" + params);
				}
				String url = iAlipayManagerSv.notifyBack(params, "alipay_wap");

				if (TextUtils.isEmpty(url)){
					logger.debug("支付宝同步回调订单支付失败，跳转地址（resultUrl）为空，请检查。");
					model.put("errormsg", "支付宝同步回调订单处理失败");
					return;
				}
				logger.debug("支付宝异步回调执行成功，result:{}", url);
//				response.getWriter().print("success");
			} catch (Exception e) {
				logger.debug("支付宝异步回调执行失败，Error:{}", e.getMessage());
			}
		}

	// 支付宝支付出错以后返回页面
	@RequestMapping(value = "/error_notify")
	public void error_notify(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws IOException {

	}

//	@RequestMapping(value = "/alipay_authorize")
//	public void alipay_authorize(HttpServletRequest request,
//			HttpServletResponse response, ModelMap model) {
//		try {
//			String url = payCommonSv.alipay_authorize(model);
//			response.sendRedirect(url);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	@RequestMapping(value = "/alipay_authorize_notify")
//	public String alipay_authorize_notify(HttpServletRequest request,
//			HttpServletResponse response, String code, String error,
//			String error_description, ModelMap model) {
//		if (error != null) {
//			// 出现错误
//			model.put("error", error);
//			model.put("error_description", error_description);
//			return "/epay/authorizeError";
//		} else {
//			try {
//				// String tString = DESCoder.initKey(AlipayConfig.des_key);
//				// String key = DESCoder.initKey();
//				DESCoder.test();
//				AlipayClient client = new DefaultAlipayClient(
//						AlipayConfig.api_url, AlipayConfig.client_id,
//						AlipayConfig.private_key, "json");
//				AlipaySystemOauthTokenRequest req = new AlipaySystemOauthTokenRequest();
//				req.setGrantType("authorization_code");
//				req.setCode(code);
//				AlipaySystemOauthTokenResponse rep = client.execute(req);
//
//				String token = rep.getAccessToken();
//				String refreshToken = rep.getRefreshToken();
//
//				byte[] des = DESCoder.encrypt(token.getBytes(),
//						AlipayConfig.des_key);
//				token = DESCoder.encryptBASE64(des);
//
//				des = DESCoder.encrypt(refreshToken.getBytes(),
//						AlipayConfig.des_key);
//				refreshToken = DESCoder.encryptBASE64(des);
//
//				Map<String, Object> rd = new HashMap<String, Object>();
//				rd.put(BaseSqlBuilder.getwhereField("AppId"),
//						AlipayConfig.client_id);
//				List<Map<String, Object>> list = epayTokenInfoDaoImpl
//						.Select(rd);
//
//				if (list.size() == 0) {
//					// 没有就新增
//					Map<String, Object> insertMap = new HashMap<String, Object>();
//					insertMap.put("PayType", "alipay");
//					insertMap.put("AppId", AlipayConfig.client_id);
//					insertMap.put("Token", token);
//					insertMap.put("RefreshToken", refreshToken);
//					epayTokenInfoDaoImpl.Insert(insertMap);
//				} else {
//					// 存在就更新
//					Map<String, Object> updateMap = new HashMap<String, Object>();
//					updateMap.put("Token", token);
//					updateMap.put("RefreshToken", refreshToken);
//					updateMap.put(BaseSqlBuilder.getwhereField("AppId"),
//							AlipayConfig.client_id);
//					epayTokenInfoDaoImpl.Update(updateMap);
//				}
//
//				return "/epay/alipay_authorize";
//			} catch (Exception e) {
//				e.printStackTrace();
//				logger.debug("支付宝授权失败，error={}", e);
//				model.put("error", "10011");
//				model.put("error_description", "支付宝授权失败");
//				return "/epay/authorizeError";
//			}
//		}
//	}

//	@RequestMapping(value = "/alipay_orderQuery")
//	public ReturnObject alipay_orderQuery(HttpServletRequest request,
//			HttpServletResponse response,
//			@RequestParam(value = "orderId", required = true) String orderId,
//			@RequestParam(value = "sign", required = true) String sign,
//			@RequestParam(value = "busiid", required = true) String busiid,
//			ModelMap model) {
//		ReturnObject returnObject = new ReturnObject();
//		Map<String, Object> map = null;
//		try {
//			map = alipaySv.OrderLookByOrderid(orderId, null);
//		} catch (Exception e) {
//			logger.debug("订单查询失败，error={}", e);
//			returnObject.setRetcode(11001);
//			returnObject.setRetmsg(e.getMessage());
//		}
//		return returnObject;
//	}
}
