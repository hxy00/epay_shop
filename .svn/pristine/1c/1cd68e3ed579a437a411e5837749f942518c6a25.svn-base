package com.emt.shoppay.sv.inter;

import java.util.Map;

/**
 * 查询接口
 */
public interface IPayQueryApiSv {

	Map<String, String> queryFromABC(String sysId, String orderId);

	Map<String, String> queryFromAlipay(String sysId, String orderId);

	Map<String, String> queryFromBoc(String sysId, String orderId);

	Map<String, String> queryFromCcb(String sysId, String orderId, String orderDate);

	Map<String, String> queryFromIcbc(String sysId, String orderId, String tranDate);

	Map<String, String>  queryFromUnionpay(String sysId, String orderId, String orderDate);

	Map<String, String> queryFromWeixinpay(String orderId, String payCompany);
}
