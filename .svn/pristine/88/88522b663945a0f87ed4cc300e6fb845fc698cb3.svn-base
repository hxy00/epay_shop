package com.emt.shoppay.controller;

import com.emt.shoppay.sv.inter.IUnionpayManagerSv;
import com.emt.shoppay.util.LogAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/epay/unionpay")
public class UnionpayController {
	private Logger logger = LoggerFactory.getLogger(getClass());
 
	@Resource
	private IUnionpayManagerSv iUnionpayManagerSv;
	
	// 银联支付成功以后返回
	@LogAnnotation(name = "WriteLog", val = true, describe = "银联支付成功同步回调")
	@RequestMapping(value = "/notify")
	public String notify(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
		logger.debug("银联订单支付成功");
		try {
			Map<String, String> map = new HashMap<String, String>();
			Enumeration<String> list = request.getParameterNames();
			while (list.hasMoreElements()) {
				String key = list.nextElement();
				map.put(key, request.getParameter(key));
				logger.debug("[Unionpay notify]" + key + "------->" + request.getParameter(key));
			}
			String url = iUnionpayManagerSv.notify(map);
			logger.debug("银联订单支付处理成功，跳转页面：" + url);
			if (null != url && !"".equals(url)) {
				model.put("url", url);
				return "/epay/gotoBack";
			} else {
				logger.debug("!!!银联订单支付返回参数不正确");
				model.put("errormsg", "银联订单支付返回参数不正确");
				return "/epay/error";
			}
		} catch (Exception e) {
			logger.debug("[Ccb_notify]回调出错：" + e.getMessage());
			model.put("errormsg", "回调出错：" + e.getMessage());
			return "/epay/error";
		}
	}

	@LogAnnotation(name = "WriteLog", val = true, describe = "银联支付成功异步回调")
	@RequestMapping(value = "/back_notify")
	public void back_notify(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
		logger.debug("银联订单异步支付成功");
		try {
			Map<String, String> map = new HashMap<String, String>();
			Enumeration<String> list = request.getParameterNames();
			while (list.hasMoreElements()) {
				String key = list.nextElement();
				map.put(key, request.getParameter(key));
				logger.debug("[UnionpayController back_notify]" + key + "------->" + request.getParameter(key));
			}
			String url = iUnionpayManagerSv.notify(map);
			logger.debug("银联订单支付处理成功，返回地址url:{}" , url);
		} catch (Exception e) {
			logger.debug("[Ccb_notify]回调出错：" + e.getMessage());
			model.put("errormsg", "回调出错：" + e.getMessage());
			return;
		}
	}

}
