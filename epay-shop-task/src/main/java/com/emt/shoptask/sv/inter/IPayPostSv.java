package com.emt.shoptask.sv.inter;

import java.util.Map;

import org.springframework.ui.ModelMap;

public interface IPayPostSv {

	/**
	 * 支付成功以后，调用该方法，post支付内容到支付发起服务器
	 * 
	 * @param  pMap
	 */
	public void postPayResult(Map<String, Object> pMap) throws Exception;

	/**
	 * 查询已支付且未推送的数据进行推送
	 */
	public void alreadyPayPost();

}
