package com.emt.shoppay.sv.inter;

import java.util.Map;

/**
 * 农行支付
 * 
 * @ClassName: IAbcManagerSv
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author huangdafei
 * @date 2018年4月28日 下午3:02:05
 * 
 */
public interface IBocManagerSv {

	public Map<String, Object> bocPayPcB2C(Map<String, String> upTranData,
                                           Map<String, Object> upExtend) throws Exception;

	public Map<String, Object> bocPayWapB2C(Map<String, String> upTranData,
                                            Map<String, Object> upExtend) throws Exception;

	public String notify(Map<String, String> mParam, String payCompany) throws Exception;

}
