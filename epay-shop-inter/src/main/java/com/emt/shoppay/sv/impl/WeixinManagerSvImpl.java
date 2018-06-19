package com.emt.shoppay.sv.impl;

import com.emt.shoppay.dao.inter.IEpayParaConfigDao;
import com.emt.shoppay.pojo.WeixinConfig;
import com.emt.shoppay.sv.inter.IPayQueryApiSv;
import com.emt.shoppay.sv.inter.IWeixinManagerSv;
import com.emt.shoppay.util.WeixinUtil;
import com.emt.shoppay.util.DateUtils;
import com.emt.shoppay.util.Global;
import com.emt.shoppay.util.StringUtils;
import com.emt.shoppay.util.ToolsUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 微信支付管理
* @ClassName: WeixinManagerSvImpl 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author huangdafei
* @date 2017年5月5日 下午2:41:41 
*
 */
@Service
public class WeixinManagerSvImpl extends BaseSvImpl implements IWeixinManagerSv {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IPayQueryApiSv iPayQueryApiSv;

	@Override
	public Map<String, String> weixinPay(Map<String, String> upTranData,
			Map<String, Object> upExtend) throws Exception {
		String orderId = getValue(upTranData, "orderId");
		String subject = getValue(upTranData, "subject");
		String fee = getValue(upTranData, "totalFee");
		String tradeType = getValue(upTranData, "tradeType");
		String notifyUrl = getValue(upTranData, "notifyUrl");
//		String appType = getValue(upTranData, "appType");//云商用来区分android或iOS支付请求，可空
		String openId = getValue(upTranData, "openId");
		String interfaceName = getValue(upExtend, "interfaceName", null);
		String ip = getValue(upExtend, "ip", null);
		
		if (StringUtils.isEmpty(orderId) || StringUtils.isEmpty(fee) || StringUtils.isEmpty(notifyUrl)
				|| StringUtils.isEmpty(subject)) {
			logger.debug("[weixinPay] 缺少必要的参数！");
			throw new Exception("缺少必要的参数！");
		}
		if("JSAPI".equals(tradeType) && TextUtils.isEmpty(openId)){
			logger.debug("[weixinPay] JSAPI缺少必要的参数，openId = " + openId);
			throw new Exception("JSAPI缺少必要的参数，openId = " + openId);
		}


		String bkInterfaceVersion = WeixinConfig.bkInterfaceVersion;//getValue(dbExtend, "interfaceVersion");
		String reqUrl = WeixinConfig.reqUrl;//getValue(dbExtend, "reqUrl");
		String appId = WeixinConfig.appId;//getValue(dbExtend, "appId");
		String mchId = WeixinConfig.mchId;//getValue(dbExtend, "mchId");
		String appSecret = WeixinConfig.appSecret;//MapUtils.getString(dbExtend, "appSecret");//AppSecret是APPID对应的接口密码，用于获取接口调用凭证access_token时使用。
		String key = WeixinConfig.key;//MapUtils.getString(dbExtend, "key");//交易过程生成签名的密钥，仅保留在商户系统和微信支付后台
		String payNotifyUrl = WeixinConfig.notyfiUrl;//getValue(dbExtend, "notyfiUrl");
		String timeOut = WeixinConfig.timeOut;//getValue(dbExtend, "timeOut");
		logger.debug("[weixinPay] appId:{}, mchId:{}, appSecret:{}", appId, mchId, key);
		if (StringUtils.isEmpty(payNotifyUrl) || StringUtils.isEmpty(timeOut)) {
			logger.debug("[weixinPay] 获取支付参数为空");
			throw new Exception("获取支付参数为空");
		}

		Integer totalFee = Integer.valueOf(fee);
		payNotifyUrl = Global.getConfig("epay.notify.url") + payNotifyUrl;
		String orderDate = DateUtils.DateTimeToYYYYMMDDhhmmss();
		Date date = new Date();
		String time_start = DateUtils.DateTimeToYYYYMMDDhhmmss();
		String time_expire = getOrderTimeoutDate(date, Integer.valueOf(timeOut));
		//生成签名
		Map<String, String> map = new HashMap<String, String>();
		Long randomLong = System.currentTimeMillis();
		map.put("appid", appId);
		map.put("mch_id", mchId);
		map.put("nonce_str", randomLong.toString());
		map.put("body", subject);
		map.put("out_trade_no", orderId);
		map.put("total_fee", String.valueOf(totalFee));
		map.put("spbill_create_ip", ip);
		map.put("notify_url", payNotifyUrl);
		map.put("trade_type", tradeType);
		map.put("time_start", time_start);
		map.put("time_expire", time_expire);
		if (tradeType.equals("JSAPI")) {
			map.put("openid", openId);
		}

		List<String> list = new ArrayList<String>();
		list.add("sign");
		list.add("sign_type");
		map = ToolsUtil.paraFilter(map, list, true);
		String prestr = ToolsUtil.createLinkString(map);
		String mySign = wxPaySign(prestr, key).toUpperCase();

		//组装保存数据
		Map<String, String> data = new HashMap<String, String>();
		data.put("submitUrl", reqUrl);
		data.put("appid", appId);
		data.put("mch_id", mchId);
		data.put("nonce_str", randomLong.toString());
		data.put("sign", mySign);
		data.put("body", subject);
		data.put("out_trade_no", orderId);
		data.put("total_fee", String.valueOf(totalFee));
		data.put("spbill_create_ip", ip);
		data.put("notify_url", payNotifyUrl);
		data.put("trade_type", tradeType);
		data.put("time_start", time_start);
		data.put("time_expire", time_expire);

		Map<String, String> extend = new HashMap<>();
		extend.put("interfaceName", interfaceName);
		extend.put("interfaceVersion", bkInterfaceVersion);
		extend.put("merUrl", payNotifyUrl);
		extend.put("merVAR", "pay.cmaotai.com");
		extend.put("orderDate", orderDate);
		extend.put("buildData", ToolsUtil.mapToJson(data));
		extend.put("shopCode", mchId);

		// 写入数据库epay的epay_oder_detail表中
		Integer retInt = insertPayOrderDetail(upTranData, upExtend, extend);
		logger.debug("[weixinPay]保存detail表状态：{}", retInt);

		logger.debug("[weixinPay]发起支付请求......");
		//组装请求数据
		Map<String, String> payMap = new HashMap<String, String>();
		payMap.put("appid", appId);
		payMap.put("mch_id", mchId);
		payMap.put("nonce_str", randomLong.toString());
		payMap.put("sign", mySign);
		payMap.put("body", subject);
		payMap.put("out_trade_no", orderId);
		payMap.put("total_fee", String.valueOf(totalFee));
		payMap.put("spbill_create_ip", ip);
		payMap.put("notify_url", payNotifyUrl);
		payMap.put("trade_type", tradeType);
		payMap.put("time_start", time_start);
		payMap.put("time_expire", time_expire);
		if (tradeType.equals("JSAPI")) {
			payMap.put("openid", openId);
		}

		String xml = ToolsUtil.mapToXml(payMap, null, null);
		String resultXml = WeixinUtil.httpReq(reqUrl, xml);
		logger.debug("[weixin_unifiedorder] one：" + resultXml);

		Map<String, String> resultMap = null;
		if (!StringUtils.isEmpty(resultXml)) {
			resultMap = WeixinUtil.parse_wap_b2c(resultXml);
		}
		logger.debug("[weixin_unifiedorder] two：" + resultMap);
		return resultMap;
	}
	
	@Override
	public String notify(Map<String, String> resultMap) throws Exception {
		try {
			String merId = getValue(resultMap, "mch_id");//params.get("mch_id");
			String return_code = getValue(resultMap, "return_code");
			String result_code = getValue(resultMap, "result_code");
			String orderId = getValue(resultMap, "out_trade_no");

			Map<String, Object> pMap = new HashMap<String, Object>();
			pMap.put("orderid", orderId);
			pMap.put("payCompany", "weixin_pay");
			List<Map<String, Object>> myOrderList = iEpayOrderDetailDao.Select(pMap);
			String resultUrl = "", interfaceName = "";
			if (null != myOrderList && myOrderList.size() > 0){
				Map<String, Object> myOrderMap = myOrderList.get(0);
				interfaceName = MapUtils.getString(myOrderMap, "payCompany");//myOrderMap.get("payCompany").toString();
				resultUrl = MapUtils.getString(myOrderMap, "ResultUrl");//myOrderMap.get("ResultUrl").toString();
			} else {
				logger.debug("订单号：" + orderId + "不存在！");
				throw new Exception("订单号：" + orderId + "不存在！");
			}

			//验证数据的准确性
			if (!"SUCCESS".equals(return_code) || !"SUCCESS".equals(result_code)) {
				logger.debug("微信支付失败！return_code = " + return_code + ";result_code = " + result_code);
				throw new Exception("微信支付失败！return_code = " + return_code + ";result_code = " + result_code);
			}

			//向微信平台发起查询请求
			Map<String, String> queryMap = iPayQueryApiSv.queryFromWeixinpay(orderId, "400001");
			if (null == queryMap || queryMap.size() == 0) {
				logger.debug("[weixinpay notify]到微信平台查询订单状态，微信平台没有查询到订单数据");
				throw new Exception("到微信平台查询订单状态，微信平台没有查询到订单数据");
			}

			String returnCode = getValue(queryMap, "return_code");
			String resultCode = getValue(queryMap, "result_code");
			String tradeState = getValue(queryMap, "trade_state");
			String total_fee = getValue(queryMap, "total_fee");
			String transactionId = getValue(queryMap, "transaction_id");
			String timeEnd = getValue(queryMap, "time_end");
			String errCodeDes = getValue(queryMap, "err_code_des");

			//判断查询返回的结果, 修改订单支付信息
			Map<String, Object> returnMap = new HashMap<String, Object>();
			if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode) && "SUCCESS".equals(tradeState)) {
				String status = "SUCCESS".equals(tradeState) ? "1" : "0";
				// 更新预支付
				Map<String, Object> rd = new HashMap<String, Object>();
				rd.put("tranStat", status);
				rd.put("TranSerialNo", transactionId);
				rd.put("notifyDate", timeEnd);
				rd.put("notifyData", ToolsUtil.mapToJson(queryMap));
				rd.put("comment", "支付成功");
				rd.put("orderid", orderId);
				rd.put("payCompany", "weixin_pay");
				iEpayOrderDetailDao.Update(rd);

				//推送订单
				Map<String, Object> pstMap = new HashMap<String, Object>();
				pstMap.put("orderId", orderId);
				pstMap.put("discountAmount", Integer.valueOf(0));
				pstMap.put("resultUrl", resultUrl);
				postPayResult(pstMap);

				logger.debug("[weixinpay] 更新云商订单的状态");
				return ToolsUtil.mapToJson(queryMap);
			} else {
				logger.debug("[weixinpay notify]微信支付回调处理出错：" + errCodeDes);
				throw new Exception(errCodeDes);
			}
		}catch (Exception e	){
			logger.debug("[weixinpay notify]微信支付回调处理出错，Error：" + e.getMessage());
			throw new Exception("微信支付回调处理出错，Error：" + e.getMessage());
		}
	}


	/**
	 * 获取截止时间（当前时间往前flag分钟）
	 * @Title: getOrderTimeoutDate
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param date
	 * @param @return  参数说明
	 * @return String    返回类型
	 * @throws
	 */
	public String getOrderTimeoutDate(Date date, Integer flag){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		date.setTime(date.getTime() + (1000 * 60) * flag);//当前时间往后flag分钟
		return dateFormat.format(date);
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
	public boolean verifySign(Map<String, String> resultMap){
		if (null == resultMap || resultMap.size() == 0) {
			return false;
		}
		try {
			//签名
			String sign = getValue(resultMap, "sign");
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
//			prestr = prestr + "&key=" + key;
			//生成签名
			String mySign = wxPaySign(prestr, key);//DigestUtils.md5Hex(prestr.getBytes("UTF-8"));
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
	 * weixinpay 签名生成
	 * @Title: sign
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param text
	 * @param @param key
	 * @param @return
	 * @param @throws Exception  参数说明
	 * @return String    返回类型
	 * @throws
	 */
	public String wxPaySign(String text, String key) throws Exception {
		text = text + "&key=" + key;
		return DigestUtils.md5Hex(text.getBytes("UTF-8"));
	}
}
