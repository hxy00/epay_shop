package com.emt.shoppay.pojo.aliQuery;


import com.emt.shoppay.util.StringUtils;

/**
 * alipay查询返回代码转义
* @ClassName: ReturnCode 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author huangdafei
* @date 2016年12月19日 下午4:12:03 
*
 */
public class ReturnCode {
	
	public static String getMessage(String code){
		String returnMessage = "未知参数";
		if (StringUtils.isEmpty(code)) {
			return returnMessage;
		}
		switch (code) {
		case "TRADE_SUCCESS":
			returnMessage = "支付成功";
			break;
		case "WAIT_BUYER_PAY":
			returnMessage = "等待买家付款";
			break;
		case "WAIT_SELLER_SEND_GOODS":
			returnMessage = "买家已付款，等待卖家发货";
			break;
		case "WAIT_BUYER_CONFIRM_GOODS":
			returnMessage = "卖家已发货，等待买家确认";
			break;
		case "TRADE_FINISHED":
			returnMessage = "交易成功结束";
			break;
		case "TRADE_CLOSED":
			returnMessage = "交易中途关闭（已结束，未成功完成）";
			break;
		case "WAIT_SYS_CONFIRM_PAY":
			returnMessage = "支付宝确认买家银行汇款中，暂勿发货";
			break;
		case "WAIT_SYS_PAY_SELLER":
			returnMessage = "买家确认收货，等待支付宝打款给卖家";
			break;
		case "TRADE_REFUSE":
			returnMessage = "立即支付交易拒绝";
			break;
		case "TRADE_REFUSE_DEALING":
			returnMessage = "立即支付交易拒绝中";
			break;
		case "TRADE_CANCEL":
			returnMessage = "立即支付交易取消";
			break;
		case "TRADE_PENDING":
			returnMessage = "等待卖家收款";
			break;
		case "BUYER_PRE_AUTH":
			returnMessage = "买家已付款（语音支付）";
			break;
		case "COD_WAIT_SELLER_SEND_GOODS":
			returnMessage = "等待卖家发货（货到付款）";
			break;
		case "COD_WAIT_BUYER_PAY":
			returnMessage = "等待买家签收付款（货到付款）";
			break;
		case "COD_WAIT_SYS_PAY_SELLER":
			returnMessage = "签收成功等待系统打款给卖家（货到付款）";
			break;
		default:
			break;
		}
		return returnMessage;
	}
	
	public static String getCode(String codeStr){
		String returnCode = "-1";
		if (StringUtils.isEmpty(codeStr)) {
			return returnCode;
		}
		switch (codeStr) {
		case "TRADE_SUCCESS":
			returnCode = "1";
			break;
		case "WAIT_BUYER_PAY":
			returnCode = "2";
			break;
		case "WAIT_SELLER_SEND_GOODS":
			returnCode = "3";
			break;
		case "WAIT_BUYER_CONFIRM_GOODS":
			returnCode = "4";
			break;
		case "TRADE_FINISHED":
			returnCode = "5";
			break;
		case "TRADE_CLOSED":
			returnCode = "6";
			break;
		case "WAIT_SYS_CONFIRM_PAY":
			returnCode = "7";
			break;
		case "WAIT_SYS_PAY_SELLER":
			returnCode = "8";
			break;
		case "TRADE_REFUSE":
			returnCode = "9";
			break;
		case "TRADE_REFUSE_DEALING":
			returnCode = "10";
			break;
		case "TRADE_CANCEL":
			returnCode = "11";
			break;
		case "TRADE_PENDING":
			returnCode = "12";
			break;
		case "BUYER_PRE_AUTH":
			returnCode = "13";
			break;
		case "COD_WAIT_SELLER_SEND_GOODS":
			returnCode = "14";
			break;
		case "COD_WAIT_BUYER_PAY":
			returnCode = "15";
			break;
		case "COD_WAIT_SYS_PAY_SELLER":
			returnCode = "16";
			break;
		default:
			break;
		}
		return returnCode;
	}
	
}
