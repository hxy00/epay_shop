package com.emt.shoppay.sv.inter;

import java.util.Map;

/**
 * 微信支付 获取统一支付码
* @ClassName: IWeixinManagerSv 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author huangdafei
* @date 2017年5月5日 上午9:48:29 
*
 */
public interface IWeixinManagerSv {
	public Map<String, String> weixinPay(Map<String, String> upTranData,
                                         Map<String, Object> upExtend) throws Exception;
	
	public String notify(Map<String, String> resultMap)throws Exception;

}
