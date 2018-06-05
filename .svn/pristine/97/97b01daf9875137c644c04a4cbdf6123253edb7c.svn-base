package com.emt.shoppay.controller;

import com.emt.shoppay.pojo.BocPKCSTool;
import com.emt.shoppay.pojo.BocPayConfig;
import com.emt.shoppay.sv.impl.ValidataSvImpl;
import com.emt.shoppay.sv.inter.IBocManagerSv;
import com.emt.shoppay.sv.inter.IValidataSv;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/epay/bocpay")
public class BocPayController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(name = "validataSvImpl", type = ValidataSvImpl.class)
	private IValidataSv validataSv;

	@Resource
	private IBocManagerSv iBocManagerSv;

	/**
	 * boc_notify_pc_b2c 回调
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@LogAnnotation(name = "WriteLog", val = true, describe = "支付回调执行-boc_notify_pc_b2c")
	@RequestMapping(value = "/boc_notify_pc_b2c")
	public String boc_notify_pc_b2c(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		logger.debug("[boc_notify_pc_b2c]支付回调执行！- boc_notify_pc_b2c");
		try {
            // 获得参数
        	logger.info("[boc_notify_pc_b2c]---------- Begin ReceiveB2CNotice ----------");
            String merchantNo = request.getParameter("merchantNo");
            String orderNo = request.getParameter("orderNo");
            String orderSeq = request.getParameter("orderSeq");
            String cardTyp = request.getParameter("cardTyp");
            String payTime = request.getParameter("payTime");
            String orderStatus = request.getParameter("orderStatus");
            String payAmount = request.getParameter("payAmount");
            String acctNo = request.getParameter("acctNo");
            String holderName = request.getParameter("holderName");
            String ibknum = request.getParameter("ibknum");
            String orderIp = request.getParameter("orderIp");
            String orderRefer = request.getParameter("orderRefer");
            String bankTranSeq = request.getParameter("bankTranSeq");
            String returnActFlag = request.getParameter("returnActFlag");
            String phoneNum = request.getParameter("phoneNum");
            String custTranId = request.getParameter("custTranId");//商户交易流水（分行特色需要获取该项）
            String signData = request.getParameter("signData");
            logger.info("[merchantNo]=[" + merchantNo + "]");
            logger.info("[orderNo]=[" + orderNo + "]");
            logger.info("[orderSeq]=[" + orderSeq + "]");
            logger.info("[cardTyp]=[" + cardTyp + "]");
            logger.info("[payTime]=[" + payTime + "]");
            logger.info("[orderStatus]=[" + orderStatus + "]");
            logger.info("[payAmount]=[" + payAmount + "]");
            logger.info("[acctNo]=[" + acctNo + "]");
            logger.info("[holderName]=[" + holderName + "]");
            logger.info("[ibknum]=[" + ibknum + "]");
            logger.info("[orderIp]=[" + orderIp + "]");
            logger.info("[orderRefer]=[" + orderRefer + "]");
            logger.info("[bankTranSeq]=[" + bankTranSeq + "]");
            logger.info("[returnActFlag]=[" + returnActFlag + "]");
            logger.info("[phoneNum]=[" + phoneNum + "]");
            logger.info("[custTranId]=[" + custTranId + "]");
            logger.info("[signData]=[" + signData + "]");
            
            Map<String, String> mpData = new HashMap<String, String>();
            mpData.put("merchantNo", merchantNo);
            mpData.put("orderNo", orderNo);
            mpData.put("orderSeq", orderSeq);
            mpData.put("cardTyp", cardTyp);
            mpData.put("payTime", payTime);
            mpData.put("orderStatus", orderStatus);
            mpData.put("payAmount", payAmount);
            mpData.put("acctNo", acctNo);
            mpData.put("holderName", holderName);
            mpData.put("ibknum", ibknum);
            mpData.put("orderIp", orderIp);
            mpData.put("orderRefer", orderRefer);
            mpData.put("bankTranSeq", bankTranSeq);
            mpData.put("returnActFlag", returnActFlag);
            mpData.put("phoneNum", phoneNum);
            mpData.put("custTranId", custTranId);
            mpData.put("signData", signData);
            
            //对返回数据进行签名验证
            //组织签名原文merchantNo|orderNo|orderSeq|cardTyp|payTime|orderStatus|payAmount
            StringBuilder plainTextBuilder =  new StringBuilder();
            plainTextBuilder.append(merchantNo).append("|")
            				 .append(orderNo).append("|")
            				 .append(orderSeq).append("|")
            				 .append(cardTyp).append("|")
            				 .append(payTime).append("|")
            				 .append(orderStatus).append("|")
            				 .append(payAmount);
            String plainText = plainTextBuilder.toString();
            logger.debug("[boc_notify_pc_b2c plainText]=["+plainText+"]");
            //获取验签根证书，对P7验签使用二级根证书
            String cerPath = BocPayConfig.getCertificateFile();
            logger.info("[boc_notify_pc_b2c] cerPath={}", cerPath);
            InputStream fis4cer = new FileInputStream(cerPath);
            BocPKCSTool tool = BocPKCSTool.getVerifier(fis4cer,null);
            //验证签名,验证失败将抛出异常
            tool.p7Verify(signData, plainText.getBytes("UTF-8"));
            logger.info("[boc_notify_pc_b2c VERIFY OK]");
             //根据业务逻辑处理
            if (!orderStatus.equals("1")){
        		logger.debug("[boc_notify_wap_b2c]支付回调执行失败，Error:{}", orderStatus);
    			model.put("errormsg", "支付回调执行失败:" + orderStatus);
    			return "/epay/error";
        	}
            String resultUrl = iBocManagerSv.notify(mpData, "boc_b2c_pc");
            logger.info("[boc_notify_pc_b2c] resultUrl = " + resultUrl);
        	logger.info("[boc_notify_pc_b2c]---------- End ReceiveB2CNotice ----------");
            if (TextUtils.isEmpty(resultUrl)){
                logger.debug("支付宝同步回调订单支付失败，跳转地址（resultUrl）为空，请检查。");
                model.put("errormsg", "支付宝同步回调订单处理失败");
                return "/epay/error";
            }
        	model.put("url", resultUrl);
            return "/epay/gotoBack";
		} catch (Exception e) {
			logger.debug("[boc_notify_pc_b2c] 支付回调执行失败，error={}", e);
			model.put("errormsg", "支付回调执行失败:" + e.getMessage());
			return "/epay/error";
		}
	}

	/**
	 * boc_notify_wap_b2c 回调
	 * @param request
	 * @param response
	 * @param model
	 * @throws IOException
	 */
	@LogAnnotation(name = "WriteLog", val = true, describe = "支付回调执行 - boc_notify_wap_b2c")
	@RequestMapping(value = "/boc_notify_wap_b2c")
	public String boc_notify_wap_b2c(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		logger.debug("[boc_notify_wap_b2c]支付回调执行！- boc_notify_wap_b2c");
		try {
            // 获得参数
        	logger.info("[boc_notify_wap_b2c]---------- Begin ReceiveB2CNotice ----------");
            String merchantNo = request.getParameter("merchantNo");
            String orderNo = request.getParameter("orderNo");
            String orderSeq = request.getParameter("orderSeq");
            String cardTyp = request.getParameter("cardTyp");
            String payTime = request.getParameter("payTime");
            String orderStatus = request.getParameter("orderStatus");
            String payAmount = request.getParameter("payAmount");
            String acctNo = request.getParameter("acctNo");
            String holderName = request.getParameter("holderName");
            String ibknum = request.getParameter("ibknum");
            String orderIp = request.getParameter("orderIp");
            String orderRefer = request.getParameter("orderRefer");
            String bankTranSeq = request.getParameter("bankTranSeq");
            String returnActFlag = request.getParameter("returnActFlag");
            String phoneNum = request.getParameter("phoneNum");
            String custTranId = request.getParameter("custTranId");//商户交易流水（分行特色需要获取该项）
            String signData = request.getParameter("signData");
            logger.info("[merchantNo]=[" + merchantNo + "]");
            logger.info("[orderNo]=[" + orderNo + "]");
            logger.info("[orderSeq]=[" + orderSeq + "]");
            logger.info("[cardTyp]=[" + cardTyp + "]");
            logger.info("[payTime]=[" + payTime + "]");
            logger.info("[orderStatus]=[" + orderStatus + "]");
            logger.info("[payAmount]=[" + payAmount + "]");
            logger.info("[acctNo]=[" + acctNo + "]");
            logger.info("[holderName]=[" + holderName + "]");
            logger.info("[ibknum]=[" + ibknum + "]");
            logger.info("[orderIp]=[" + orderIp + "]");
            logger.info("[orderRefer]=[" + orderRefer + "]");
            logger.info("[bankTranSeq]=[" + bankTranSeq + "]");
            logger.info("[returnActFlag]=[" + returnActFlag + "]");
            logger.info("[phoneNum]=[" + phoneNum + "]");
            logger.info("[custTranId]=[" + custTranId + "]");
            logger.info("[signData]=[" + signData + "]");
            
            Map<String, String> mpData = new HashMap<String, String>();
            mpData.put("merchantNo", merchantNo);
            mpData.put("orderNo", orderNo);
            mpData.put("orderSeq", orderSeq);
            mpData.put("cardTyp", cardTyp);
            mpData.put("payTime", payTime);
            mpData.put("orderStatus", orderStatus);
            mpData.put("payAmount", payAmount);
            mpData.put("acctNo", acctNo);
            mpData.put("holderName", holderName);
            mpData.put("ibknum", ibknum);
            mpData.put("orderIp", orderIp);
            mpData.put("orderRefer", orderRefer);
            mpData.put("bankTranSeq", bankTranSeq);
            mpData.put("returnActFlag", returnActFlag);
            mpData.put("phoneNum", phoneNum);
            mpData.put("custTranId", custTranId);
            mpData.put("signData", signData);
            
            //对返回数据进行签名验证
            //组织签名原文merchantNo|orderNo|orderSeq|cardTyp|payTime|orderStatus|payAmount
            StringBuilder plainTextBuilder =  new StringBuilder();
            plainTextBuilder.append(merchantNo).append("|")
            				 .append(orderNo).append("|")
            				 .append(orderSeq).append("|")
            				 .append(cardTyp).append("|")
            				 .append(payTime).append("|")
            				 .append(orderStatus).append("|")
            				 .append(payAmount);
            String plainText = plainTextBuilder.toString();
            logger.debug("[boc_notify_wap_b2c plainText]=["+plainText+"]");
            //获取验签根证书，对P7验签使用二级根证书
            InputStream fis4cer = new FileInputStream(BocPayConfig.getCertificateFile());
            BocPKCSTool tool = BocPKCSTool.getVerifier(fis4cer,null);
            //验证签名,验证失败将抛出异常
            tool.p7Verify(signData, plainText.getBytes("UTF-8"));
            logger.info("[boc_notify_wap_b2c VERIFY OK]");
            //根据业务逻辑处理
            if (!orderStatus.equals("1")){
        		logger.debug("[boc_notify_wap_b2c]支付回调执行失败，Error:{}", orderStatus);
    			model.put("errormsg", "支付回调执行失败:" + orderStatus);
    			return "/epay/error";
        	}
			
            String resultUrl = iBocManagerSv.notify(mpData, "boc_b2c_wap");
            logger.info("[boc_notify_wap_b2c] resultUrl = " + resultUrl);
        	logger.info("[boc_notify_wap_b2c]---------- End ReceiveB2CNotice ----------");
            if (TextUtils.isEmpty(resultUrl)){
                logger.debug("支付宝同步回调订单支付失败，跳转地址（resultUrl）为空，请检查。");
                model.put("errormsg", "支付宝同步回调订单处理失败");
                return "/epay/error";
            }
            model.put("url", resultUrl);
            return "/epay/gotoBack";
		} catch (Exception e) {
			logger.debug("[boc_notify_wap_b2c]支付回调执行失败，Error:{}", e.getMessage());
			model.put("errormsg", "支付回调执行失败:" + e.getMessage());
			return "/epay/error";
		}
	}
}
