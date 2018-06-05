package com.emt.shoppay.controller;

import com.emt.shoppay.sv.inter.ICcbManagerSv;
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
@RequestMapping("/epay/ccb")
public class CcbPayController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private ICcbManagerSv iCcbManagerSv;

	@LogAnnotation(name = "WriteLog", val = true, describe = "建行支付成功同步回调")
	@RequestMapping(value = "/Ccb_notify")
	public String Ccb_notify(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		logger.debug("[Ccb_notify] 建行同步回调执行！");
		try {
			Map<String, String> params = new HashMap<String, String>();
			Enumeration<String> list = request.getParameterNames();
			while (list.hasMoreElements()) {
				String key = list.nextElement();
				params.put(key, request.getParameter(key));
			}
			logger.debug("[Ccb_notify] params:{}", params);

			String url = iCcbManagerSv.notify(params);
			logger.debug("[Ccb_notify] url:{}", url);
			if (null != url && !"".equals(url)) {
				model.put("url", url);
				return "/epay/gotoBack";
			} else {
				model.put("errormsg", "没有查询出该订单的相关信息");
				return "/epay/error";
			}
		}catch(Exception e){
			logger.debug("[Ccb_notify]回调出错：" + e.getMessage());
			model.put("errormsg", "回调出错：" + e.getMessage());
			return "/epay/error";
		}
	}

	@LogAnnotation(name = "WriteLog", val = true, describe = "建行支付成功异步回调")
	@RequestMapping(value = "/Ccb_notify_asyn")
	public void Ccb_notify_asyn(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		logger.debug("[Ccb_notify_asyn] 建行异步回调执行！");
		try{
			Map<String, String> params = new HashMap<String, String>();
			Enumeration<String> list = request.getParameterNames();
			while (list.hasMoreElements()) {
				String key = list.nextElement();
				params.put(key, request.getParameter(key));
			}
			logger.debug("[Ccb_notify_asyn] params:{}", params);
			String url = iCcbManagerSv.notify(params);
			logger.debug("[Ccb_notify_asyn] url:{}", url);
		} catch (Exception e) {
			logger.debug("[Ccb_notify_asyn]回调出错：" + e.getMessage());
		}
	}
}
