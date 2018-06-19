package com.emt.shoppay.controller;

import com.abc.pay.client.JSON;
import com.abc.pay.client.ebus.PaymentResult;
import com.abc.pay.client.ebus.RefundRequest;
import com.emt.shoppay.pojo.Pc_PayUserInfo;
import com.emt.shoppay.sv.inter.IAbcManagerSv;
import com.emt.shoppay.util.LogAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 农行支付回调
 * @ClassName: AbcPayController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author huangdafei
 * @date 2016年11月30日 下午8:11:17
 *
 */
@Controller
@RequestMapping("/epay/abc")
public class AbcPayController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private IAbcManagerSv iAbcManagerSv;

	@LogAnnotation(name = "WriteLog", val = true, describe = "农行支付成功同步回调")
	@RequestMapping(value = "/abc_notify_pc")
	public String abc_notify_pc(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		try {
			Map<String, String> params = new HashMap<String, String>();
			//1、取得MSG参数，并利用此参数值生成支付结果对象
			String msg = request.getParameter("MSG");
			logger.debug("[acb_notify] msg:{}", msg);
			PaymentResult tResult = new PaymentResult(msg);
			//2、判断支付结果状态，进行后续操作
			if (tResult.isSuccess()) {
				//3、支付成功并且验签、解析成功
				params.put("TrxType", tResult.getValue("TrxType"));
				params.put("OrderNo", tResult.getValue("OrderNo"));
				params.put("Amount", tResult.getValue("Amount"));
				params.put("BatchNo", tResult.getValue("BatchNo"));
				params.put("VoucherNo", tResult.getValue("VoucherNo"));
				params.put("HostDate", tResult.getValue("HostDate"));
				params.put("HostTime", tResult.getValue("HostTime"));
				params.put("MerchantRemarks", tResult.getValue("MerchantRemarks"));
				params.put("PayType", tResult.getValue("PayType"));
				params.put("NotifyType", tResult.getValue("NotifyType"));
				params.put("TrnxNo", tResult.getValue("iRspRef"));

				logger.debug("[abc_notify_wap]TrxType         = " + tResult.getValue("TrxType"));
				logger.debug("[abc_notify_wap]OrderNo         = " + tResult.getValue("OrderNo"));
				logger.debug("[abc_notify_wap]Amount          = " + tResult.getValue("Amount"));
				logger.debug("[abc_notify_wap]BatchNo         = " + tResult.getValue("BatchNo"));
				logger.debug("[abc_notify_wap]VoucherNo       = " + tResult.getValue("VoucherNo"));
				logger.debug("[abc_notify_wap]HostDate        = " + tResult.getValue("HostDate"));
				logger.debug("[abc_notify_wap]HostTime        = " + tResult.getValue("HostTime"));
				logger.debug("[abc_notify_wap]MerchantRemarks = " + tResult.getValue("MerchantRemarks"));
				logger.debug("[abc_notify_wap]PayType         = " + tResult.getValue("PayType"));
				logger.debug("[abc_notify_wap]NotifyType      = " + tResult.getValue("NotifyType"));
				logger.debug("[abc_notify_wap]TrnxNo          = " + tResult.getValue("iRspRef"));

				String url = iAbcManagerSv.notify(params, "abc_pay_pc");
				logger.debug("[abc_notify_wap] url={} ", url);
				if (null == url || "".equals(url)) {
					model.put("errormsg", "跳转地址为空（resultUrl）");
					return "/epay/error";
				}
				model.put("url", url);
				return "/epay/gotoBack";
			} else {
				logger.debug("ReturnCode = {}， ErrorMessage = {}", tResult.getReturnCode(), tResult.getErrorMessage());
				model.put("errormsg", "错误编码：" + tResult.getReturnCode() + "，错误原因：" + tResult.getErrorMessage());
				return "/epay/error";
			}
		} catch(Exception e) {
			logger.debug("回调出错：" + e.getMessage());
			model.put("errormsg", "回调出错：" + e.getMessage());
			return "/epay/error";
		}
	}
	
	@LogAnnotation(name = "WriteLog", val = true, describe = "农行支付成功同步回调")
	@RequestMapping(value = "/abc_notify_wap")
	public String abc_notify_wap(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
		try {
			Map<String, String> params = new HashMap<String, String>();
			//1、取得MSG参数，并利用此参数值生成支付结果对象
			String msg = request.getParameter("MSG");
			logger.debug("[abc_notify_wap] msg:{}", msg);
			PaymentResult tResult = new PaymentResult(msg);
			//2、判断支付结果状态，进行后续操作
			if (tResult.isSuccess()) {
		        //3、支付成功并且验签、解析成功
				params.put("TrxType", tResult.getValue("TrxType"));
				params.put("OrderNo", tResult.getValue("OrderNo"));
				params.put("Amount", tResult.getValue("Amount"));
				params.put("BatchNo", tResult.getValue("BatchNo"));
				params.put("VoucherNo", tResult.getValue("VoucherNo"));
				params.put("HostDate", tResult.getValue("HostDate"));
				params.put("HostTime", tResult.getValue("HostTime"));
				params.put("MerchantRemarks", tResult.getValue("MerchantRemarks"));
				params.put("PayType", tResult.getValue("PayType"));
				params.put("NotifyType", tResult.getValue("NotifyType"));
				params.put("TrnxNo", tResult.getValue("iRspRef"));

				logger.debug("[abc_notify_wap]TrxType         = " + tResult.getValue("TrxType"));
				logger.debug("[abc_notify_wap]OrderNo         = " + tResult.getValue("OrderNo"));
				logger.debug("[abc_notify_wap]Amount          = " + tResult.getValue("Amount"));
				logger.debug("[abc_notify_wap]BatchNo         = " + tResult.getValue("BatchNo"));
				logger.debug("[abc_notify_wap]VoucherNo       = " + tResult.getValue("VoucherNo"));
				logger.debug("[abc_notify_wap]HostDate        = " + tResult.getValue("HostDate"));
				logger.debug("[abc_notify_wap]HostTime        = " + tResult.getValue("HostTime"));
				logger.debug("[abc_notify_wap]MerchantRemarks = " + tResult.getValue("MerchantRemarks"));
				logger.debug("[abc_notify_wap]PayType         = " + tResult.getValue("PayType"));
				logger.debug("[abc_notify_wap]NotifyType      = " + tResult.getValue("NotifyType"));
				logger.debug("[abc_notify_wap]TrnxNo          = " + tResult.getValue("iRspRef"));

				String url = iAbcManagerSv.notify(params, "abc_pay_wap");
				logger.debug("[abc_notify_wap] url={} ", url);
				if (null == url || "".equals(url)) {
					model.put("errormsg", "跳转地址为空（resultUrl）");
					return "/epay/error";
				}
				model.put("url", url);
				return "/epay/gotoBack";
			} else {
				logger.debug("ReturnCode   = " + tResult.getReturnCode());
				logger.debug("ErrorMessage = " + tResult.getErrorMessage());
				model.put("errormsg", "错误编码：" + tResult.getReturnCode() + "，错误原因：" + tResult.getErrorMessage());
				return "/epay/error";
			}
		}catch(Exception e) {
			logger.debug("回调出错：" + e.getMessage());
			model.put("errormsg", "回调出错：" + e.getMessage());
			return "/epay/error";
		}
	}

	@RequestMapping("/to_refund")
	public String to_refund(HttpServletRequest request, HttpServletResponse response){
		Pc_PayUserInfo userInfo = (Pc_PayUserInfo) request.getSession().getAttribute("userInfo");
		if (null == userInfo) {
			return "order/pc_pay/login";
		}
		return "/epay/abc/refund";
	}
	
	@RequestMapping("/refund")
	@ResponseBody
	public void refund(HttpServletRequest request, HttpServletResponse response, ModelMap model){
		//1、生成退款请求对象
        RefundRequest tRequest = new RefundRequest();
        tRequest.dicRequest.put("OrderDate", request.getParameter("txtOrderDate"));  //订单日期（必要信息）
        tRequest.dicRequest.put("OrderTime", request.getParameter("txtOrderTime")); //订单时间（必要信息）
        //tRequest.dicRequest.put("MerRefundAccountNo", request.getParameter("txtMerRefundAccountNo"));  //商户退款账号
        //tRequest.dicRequest.put("MerRefundAccountName", request.getParameter("txtMerRefundAccountName")); //商户退款名
        tRequest.dicRequest.put("OrderNo", request.getParameter("txtOrderNo")); //原交易编号（必要信息）
        tRequest.dicRequest.put("NewOrderNo", request.getParameter("txtNewOrderNo")); //交易编号（必要信息）
        tRequest.dicRequest.put("CurrencyCode", request.getParameter("txtCurrencyCode")); //交易币种（必要信息）
        tRequest.dicRequest.put("TrxAmount", request.getParameter("txtTrxAmount")); //退货金额 （必要信息）
        //tRequest.dicRequest.put("MerchantRemarks", request.getParameter("txtMerchantRemarks"));  //附言
		//如果需要专线地址，调用此方法：
		//tRequest.setConnectionFlag(true);
        //2、传送退款请求并取得退货结果
        JSON json = tRequest.postRequest();
        //3、判断退款结果状态，进行后续操作
        String ReturnCode = json.GetKeyValue("ReturnCode");
        String ErrorMessage = json.GetKeyValue("ErrorMessage");
		try {
			PrintWriter out = response.getWriter();
			if (ReturnCode.equals("0000")) {
				//4、退款成功
				out.println("ReturnCode	= [" + ReturnCode + "]");
				out.println("   Message	= [" + ErrorMessage + "]");
				out.println("   OrderNo	= [" + json.GetKeyValue("OrderNo") + "]");
				out.println("NewOrderNo	= [" + json.GetKeyValue("NewOrderNo") + "]");
				out.println(" TrxAmount	= [" + json.GetKeyValue("TrxAmount") + "]");
				out.println("   BatchNo	= [" + json.GetKeyValue("BatchNo") + "]");
				out.println(" VoucherNo	= [" + json.GetKeyValue("VoucherNo") + "]");
				out.println("  HostDate	= [" + json.GetKeyValue("HostDate") + "]");
				out.println("  HostTime	= [" + json.GetKeyValue("HostTime") + "]");
				out.println("   iRspRef	= [" + json.GetKeyValue("iRspRef") + "]");
			} else {
				out.println("ReturnCode	= [" + ReturnCode + "]");
				out.println("   Message	= [" + ErrorMessage + "]");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
