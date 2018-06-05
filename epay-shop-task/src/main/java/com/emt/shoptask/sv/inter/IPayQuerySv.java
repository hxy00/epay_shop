package com.emt.shoptask.sv.inter;

import java.util.List;
import java.util.Map;

public interface IPayQuerySv {
	/**
	 * 查询银行支付状态
	* @Title: QueryBankState 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param payCompany
	* @param @param orderId
	* @param @return  参数说明 
	* @return String    返回类型 
	* @throws
	 */
	String queryOrderState(String orderId);
	
	
	void unPayQuery(String orderId);
}
