package com.emt.shoppay.sv.inter;

import java.util.Map;

/**
 * 支付宝支付
* @ClassName: IAlipayManagerSv 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author huangdafei
* @date 2017年5月3日 上午11:11:50 
*
 */
public interface IAlipayManagerSv {

	public Map<String, Object> alipayWap(Map<String, String> upTranData,
                                         Map<String, Object> upExtend) throws Exception;

	public Map<String, Object> alipayPc(Map<String, String> upTranData,
                                        Map<String, Object> upExtend) throws Exception;
	
	public String notifyFront(Map<String, String> map, String payCompany) throws Exception;
	
	public String notifyBack(Map<String, String> map, String payCompany) throws Exception;

}
