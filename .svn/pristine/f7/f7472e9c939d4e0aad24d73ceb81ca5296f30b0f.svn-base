package com.emt.shoppay.controller;

import com.emt.shoppay.pojo.ReturnObject;
import com.emt.shoppay.pojo.WeixinConfig;
import com.emt.shoppay.sv.impl.ValidataSvImpl;
import com.emt.shoppay.sv.inter.IValidataSv;
import com.emt.shoppay.sv.inter.IWeixinManagerSv;
import com.emt.shoppay.sv0.WeixinXmlForDOM4J;
import com.emt.shoppay.util.Base64Util;
import com.emt.shoppay.util.LogAnnotation;
import com.emt.shoppay.util.StringUtils;
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

	@Resource(name = "validataSvImpl", type = ValidataSvImpl.class)
	private IValidataSv validataSv;

	@Resource
	private IWeixinManagerSv iWeixinManagerSv;

	@LogAnnotation(name = "WriteLog", val = true, describe = "微信支付成功异步回调")
	@RequestMapping(value = "/notify_wap")
	public void notify_wap(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		logger.debug("微信支付回调成功！");
		ByteArrayOutputStream outSteam = null;
		InputStream inStream = null;
		try {
			outSteam = new ByteArrayOutputStream();
			inStream = request.getInputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
		} catch (Exception e) {
			logger.debug("回调处理失败，error={}", e); return;
		} finally {
			try{
				outSteam.close();
				inStream.close();
			} catch (IOException e) {
				logger.debug("回调处理失败，error={}", e); return;
			}
		}
		try {
			// 获取微信调用notify_url的返回信息
			String result = new String(outSteam.toByteArray(), "utf-8");
			logger.debug("微信支付成功返回数据：{}", result);
			if (StringUtils.isEmpty(result)) {
				Map<String, String> map = WeixinXmlForDOM4J.parse_wap_b2c(result);
				if (null == map || map.size() == 0) {
					logger.debug("订单转换失败result——>map,result={}", result);
				} else {
					String sign = getValue(map, "sign");
					//校验签名
					if (!verifySign(map, sign)) {
						// 数据被篡改
						logger.debug("[weixinpay] !!!微信支付接口返回数据被篡改！");
					} else {
						String ret = iWeixinManagerSv.notify(map);
						logger.debug("[weixinpay] 查询订单返回：" + ret);
					}
				}
			}
		} catch (Exception e) {
			logger.debug("回调处理失败，error={}", e);
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
		PrintWriter out;
		try {
			response.setContentType("text/html; charset=UTF-8");
			out = response.getWriter();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		if ("1.0.0.2".equals(interfaceVersion)) {
			try {
				Map<String, String> map = null;

				tranData = Base64Util.decodeBase64(tranData, "UTF-8");

				ObjectMapper mapper = new ObjectMapper();
				map = mapper.readValue(tranData, Map.class);

				// 验证数据正确性
				if (!validataSv.ValidataData(busiid, map, signData)) {
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
					// 返回数据为成功时d
					String prepay_id = resultMap.get("prepay_id");
					logger.debug("[weixin_unifiedorder] four");

					String tempString = "orderId=" + out_trade_no + "&prepay_id=" + prepay_id;
					String sign = validataSv.getValidataString("10001", tempString);
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

					String log = returnObject.toJson();
					logger.debug(log);
					out.write(log);
				}
			} catch (Exception e) {
				returnObject = new ReturnObject();
				returnObject.setRetcode(11);
				returnObject.setRetmsg("创建预支付订单失败！");

				String log = returnObject.toJson();
				logger.debug(log);
				logger.debug(e.getMessage());
				out.write(log);
			}
		}
	}

	/**
	 * 微信商城订单查询接口
	* @Title: queryWeixinOrder 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param orderId
	* @param @return  参数说明 
	* @return ReturnObject    返回类型 
	* @throws
	 */
	@LogAnnotation(name = "WriteLog", val = true, describe = "微信订单状态查询")
	@RequestMapping(value = "/queryWeixinOrder")
	public @ResponseBody ReturnObject queryWeixinOrder(String orderId) {
		String appid = "wx7a2dfd9101d0bece";
		String mch_id = "1220734401";
		String appSecret = "a27ac11c644038a10399f4aabemao888";
		try {
			Long randomLong = System.currentTimeMillis();
			Map<String, String> map = new HashMap<String, String>();
			map.put("appid", appid);
			map.put("mch_id", mch_id);
			map.put("out_trade_no", orderId);
			map.put("nonce_str", randomLong.toString());
			
			List<String> list = new ArrayList<String>();
			list.add("sign");
			list.add("sign_type");
			
			map = ToolsUtil.paraFilter(map, list, true);
			String prestr = ToolsUtil.createLinkString(map);
			String mysign = sign(prestr, appSecret);
			map.put("sign", mysign);

			String xml = ToolsUtil.mapToXml(map, null, null);

			URL url = new URL("https://api.mch.weixin.qq.com/pay/orderquery");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(30000); // 设置连接主机超时（单位：毫秒)
			conn.setReadTimeout(30000); // 设置从主机读取数据超时（单位：毫秒)
			conn.setDoOutput(true); // post请求参数要放在http正文内，顾设置成true，默认是false
			conn.setDoInput(true); // 设置是否从httpUrlConnection读入，默认情况下是true
			conn.setUseCaches(false); // Post 请求不能使用缓存
			// 设定传送的内容类型是可序列化的java对象(如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestMethod("POST");// 设定请求的方法为"POST"，默认是GET
			conn.setRequestProperty("Content-Length", xml.length() + "");
			String encode = "utf-8";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), encode);
			out.write(xml);
			out.flush();
			out.close();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new Exception("请求状态：" + conn.getResponseCode());
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = "";
			StringBuffer strBuf = new StringBuffer();
			while ((line = in.readLine()) != null) {
				strBuf.append(line).append("\n");
			}
			in.close();
			String resultXml = strBuf.toString();
			Map<String, String> resultMap = WeixinXmlForDOM4J.parse_wap_b2c(resultXml);
			return new ReturnObject(ReturnObject.SuccessEnum.success, "查询成功", resultMap, 1);
		} catch (Exception e) {
			return new ReturnObject(ReturnObject.SuccessEnum.fail, "查询失败：" + e.getMessage(), null, 1);
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
			String key = WeixinConfig.getAppSecret(merId);
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
