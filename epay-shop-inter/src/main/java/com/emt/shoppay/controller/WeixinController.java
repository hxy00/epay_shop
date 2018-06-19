package com.emt.shoppay.controller;

import com.emt.shoppay.pojo.ReturnObject;
import com.emt.shoppay.pojo.WeixinConfig;
import com.emt.shoppay.util.ValidataUtil;
import com.emt.shoppay.sv.inter.IWeixinManagerSv;
import com.emt.shoppay.util.WeixinUtil;
import com.emt.shoppay.util.Base64Util;
import com.emt.shoppay.util.LogAnnotation;
import com.emt.shoppay.util.ToolsUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/epay/weixin")
public class WeixinController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private IWeixinManagerSv iWeixinManagerSv;

	@LogAnnotation(name = "WriteLog", val = true, describe = "微信支付成功异步回调")
	@RequestMapping(value = "/notify_wap")
	public void notify_wap(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		logger.debug("微信支付回调成功！");
		ByteArrayOutputStream outSteam = null;
		InputStream inStream = null;
		try {
			inStream = request.getInputStream();
			outSteam = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}

			String result = new String(outSteam.toByteArray(), "utf-8");// 获取微信调用notify_url的返回信息
			logger.debug("微信支付成功返回数据：{}", result);

			Map<String, String> map = WeixinUtil.parse_wap_b2c(result);
			String ret = iWeixinManagerSv.notify(map);
			logger.debug("[weixinpay] 查询订单返回：" + ret);
		} catch (Exception e) {
			logger.debug("回调处理失败，error={}", e); return;
		} finally {
			try{
				if(null != outSteam)
					outSteam.close();
				if(null != outSteam)
					inStream.close();
			} catch (IOException e) {
				logger.debug("回调处理失败，error={}", e); return;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@LogAnnotation(name = "WriteLog", val = true, describe = "微信支付获取统一支付码")
	@RequestMapping(value = "/weixin_unifiedorder")
	public void weixin_unifiedorder(
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
		ReturnObject returnObject = null;
		PrintWriter out = null;
		if ("1.0.0.2".equals(interfaceVersion)) {
			try {
				response.setContentType("text/html; charset=UTF-8");
				out = response.getWriter();

				tranData = Base64Util.decodeBase64(tranData, "UTF-8");
				ObjectMapper mapper = new ObjectMapper();
				Map<String, String> map = mapper.readValue(tranData, Map.class);
				// 验证数据正确性
				if (!ValidataUtil.ValidataData(busiid, map, signData)) {
					returnObject = new ReturnObject();
					returnObject.setRetcode(11);
					returnObject.setRetmsg("错误原因：非法的交易数据！");
					out.write(returnObject.toJson());
					return;
				}

				Map<String, Object> upExtend = new HashMap<>();
				upExtend.put("interfaceName", interfaceName);// interfaceName,// 接口名称
				upExtend.put("interfaceVersion", interfaceVersion);
				upExtend.put("qid", qid);
				upExtend.put("clientType", clientType);
				upExtend.put("busiid", busiid);
				upExtend.put("sysId", sysId);
				
				Map<String, String> resultMap = iWeixinManagerSv.weixinPay(map, upExtend);
				//验证数据的准确性
				String return_code = getValue(resultMap, "return_code");
				String result_code = getValue(resultMap, "result_code");
				String out_trade_no = getValue(resultMap, "out_trade_no");
				String nonce_str = getValue(resultMap, "nonce_str");
				if ("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)) {
					logger.debug("[weixin_unifiedorder] three");
					// 返回数据为成功时
					String prepay_id = resultMap.get("prepay_id");
					logger.debug("[weixin_unifiedorder] four");

					String tempString = "orderId=" + out_trade_no + "&prepay_id=" + prepay_id + "&noncestr=" + nonce_str;
					String sign = ValidataUtil.getValidataString("10001", tempString);
					logger.debug("[weixin_unifiedorder] five");

					Map<String, String> jsonData = new HashMap<String, String>();
					logger.debug("[weixin_unifiedorder] six");

					jsonData.put("orderId", out_trade_no);
					jsonData.put("prepay_id", prepay_id);
					jsonData.put("noncestr", nonce_str);
					jsonData.put("sign", sign);

					returnObject = new ReturnObject();
					returnObject.setRetcode(0);
					returnObject.setRetmsg("成功");
					returnObject.setData(jsonData);
					returnObject.setCount(1);
					String log = returnObject.toJson();
					logger.debug(log);
					out.write(returnObject.toJson());
				} else {
					returnObject = new ReturnObject();
					returnObject.setRetcode(10);
					returnObject.setRetmsg(MapUtils.getString(resultMap, "return_msg"));

					String json = returnObject.toJson();
					logger.debug("[weixin_unifiedorder]" + json);
					out.write(json);
				}
			} catch (Exception e) {
				returnObject = new ReturnObject();
				returnObject.setRetcode(11);
				returnObject.setRetmsg("创建预支付订单失败！");

				String json = returnObject.toJson();
				logger.debug("[weixin_unifiedorder]" + json);
				logger.debug("[weixin_unifiedorder]" + e.getMessage());
				out.write(json);
			}
		}
	}

	/**
	 * 验证微信签名
	* @Title: verifySign 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param resultMap
	* @param @param sign
	* @param @return  参数说明 
	* @return boolean    返回类型 
	* @throws
	 */
	public boolean verifySign(Map<String, String> resultMap, String sign){
		if (null == resultMap || TextUtils.isEmpty(sign)) {
			return false;
		}
		try {
			//获取商户号
			String merId = getValue(resultMap, "mch_id");
			//根据商户号获取key
			String key = WeixinConfig.key;
			//组装签名数据
			List<String> list = new ArrayList<String>();
			list.add("sign");
			list.add("sign_type");
			Map<String, String> signMap = ToolsUtil.paraFilter(resultMap, list, true);
			String prestr = ToolsUtil.createLinkString(signMap);
			prestr = prestr + "&key=" + key;
			//生成签名
			String mySign = DigestUtils.md5Hex(prestr.getBytes("UTF-8"));
			//进行比较并返回
			return mySign.toUpperCase().equals(sign.toUpperCase());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 构建签名
	* @Title: sign 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param text
	* @param @param key
	* @param @return
	* @param @throws Exception  参数说明 
	* @return String    返回类型 
	* @throws
	 */
	private String sign(String text, String key) throws Exception {
		text = text + "&key=" + key;
		return DigestUtils.md5Hex(text.getBytes("UTF-8"));
	}

	/**
	 * 从Map<String, String>获取key的值
	 * @Title: getValue
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param map
	 * @param @param key
	 * @param @return  参数说明
	 * @return String    返回类型
	 * @throws
	 */
	public String getValue(Map<String, String> map, String key) {
		if (map.containsKey(key) && map.get(key) != null)
			return map.get(key);
		return "";
	}

	/**
	 * 从Map<String, Object>获取key的值
	 * @Title: getValue
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param map
	 * @param @param key
	 * @param @return  参数说明
	 * @return String    返回类型
	 * @throws
	 */
	public String getValue(Map<String, Object> map, String... key) {
		if (map.containsKey(key[0]) && map.get(key[0]) != null)
			return map.get(key[0]).toString();
		return "";
	}
}
