package com.emt.shoppay.sv.inter;

import java.util.Map;

/**
 * 工商银行支付
 * 
 * @ClassName: IIcbcManagerSv
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author huangdafei
 * @date 2017年4月28日 下午3:02:05
 * 
 */
public interface IIcbcManagerSv {

	public Map<String, Object> icbcPayWapB2C(Map<String, String> upTranData,
                                             Map<String, Object> upExtend) throws Exception;

	public Map<String, Object> icbcPayPcB2C(Map<String, String> upTranData,
                                            Map<String, Object> upExtend) throws Exception;
	
	public String notifyWapB2C(Map<String, String> mParam) throws Exception;
	
	public String notifyPcB2C(Map<String, String> mParam) throws Exception;

}
