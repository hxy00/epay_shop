package com.emt.shoppay.sv.inter;

import java.util.Map;

public interface IUnionpayManagerSv {

	public Map<String, Object> unionpay(Map<String, String> upTranData,
                                        Map<String, Object> upExtend) throws Exception;

//	public Map<String, Object> unionpayB2c(Map<String, String> upTranData,
//			Map<String, Object> upExtend) throws Exception;
//	
	public Map<String, Object> unionpayQuickPass(Map<String, String> upTranData,
                                                 Map<String, Object> upExtend) throws Exception;
	
//	public Map<String, Object> unionpayControls(Map<String, String> upTranData,
//			Map<String, Object> upExtend) throws Exception;
	
//	public Map<String, Object> unionpayEmt(Map<String, String> upTranData,
//			Map<String, Object> upExtend) throws Exception;
//
//	public Map<String, Object> unionpayB2cWap(Map<String, String> upTranData,
//			Map<String, Object> upExtend) throws Exception;
	
	public String notify(Map<String, String> params)throws Exception;
}
