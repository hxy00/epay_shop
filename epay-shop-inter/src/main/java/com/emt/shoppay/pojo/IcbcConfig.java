package com.emt.shoppay.pojo;

import com.emt.shoppay.util.Config;

import java.util.HashMap;
import java.util.Map;

public class IcbcConfig {
	public static String bkInterfaceNamePC = "ICBC_PERBANK_B2C";
	public static String bkInterfaceVersionPC = "1.0.0.11";
	public static String notifyUrlPC = "/epay/icbc/notify_pc_b2c";
	public static String reqUrlPc = "https://B2C.icbc.com.cn/servlet/ICBCINBSEBusinessServlet";

	public static String bkInterfaceNameWAP = "ICBC_WAPB_B2C";
	public static String bkInterfaceVersionWAP = "1.0.0.6";
	public static String notifyUrlWAP = "/epay/icbc/notify_wap_b2c";
	public static String reqUrlWap = "https://mywap2.icbc.com.cn/ICBCWAPBank/servlet/ICBCWAPEBizServlet";

	public static String merID = "2402EE20110012";
	public static String merAcct = "2402003409000081534";
	public static String password = "123456";
	public static String crtFilePath = "shop.crt_file_path";
	public static String keyFilePath = "shop.key_file_path";
	public static String jksFlePath = "shop.jks_file_path";
	public static String timeOut = "30";


	private static Map<String, String> mapValue = new HashMap<String, String>();
	static{
		mapValue.put("40972", "API查询的订单不存在");
		mapValue.put("40973", "API查询过程中系统异常");
		mapValue.put("40976", "API查询系统异常");
		mapValue.put("40977", "商户证书信息错");
		mapValue.put("40978", "解包商户请求数据报错");
		mapValue.put("40979", "查询的订单不存在");
		mapValue.put("40980", "API查询过程中系统异常");
		mapValue.put("40981", "给商户打包返回数据错");
		mapValue.put("40982", "系统错误");
		mapValue.put("40983", "查询的订单不唯一");
		mapValue.put("40987", "请求数据中接口名错误");
		mapValue.put("40947", "商户代码或者商城账号有误");
		mapValue.put("40948", "商城状态非法");
		mapValue.put("40949", "商城类别非法");
		mapValue.put("40950", "商城应用类别非法");
		mapValue.put("40951", "商户证书id状态非法");
		mapValue.put("40952", "商户证书id未绑定");
		mapValue.put("40953", "商户id权限非法");
		mapValue.put("40954", "检查商户状态时数据库异常");
		mapValue.put("42022", "业务类型上送有误");
		mapValue.put("42023", "商城种类上送有误");
		mapValue.put("42020", "ID未开通汇总记账清单功能");
		mapValue.put("42021", "汇总记账明细清单功能已到期");
		mapValue.put("40990", "商户证书格式错误");
		mapValue.put("41160", "商户未开通外卡支付业务");
		mapValue.put("41161", "商户id对商城账号没有退货权限");
		mapValue.put("41177", "外卡的当日退货必须为全额退货");
		mapValue.put("26012", "找不到记录");
		mapValue.put("26002", "数据库操作异常");
		mapValue.put("26034", "退货交易重复提交");
		mapValue.put("26036", "更新支付表记录失败");
		mapValue.put("26042", "退货对应的支付订单未清算，不能退货");
	}
	
	public static String getErrorDesc(String errorCode){
		if(mapValue.containsKey(errorCode)){
			return mapValue.get(errorCode);
		}
		return null;
	}

	public static String getCrtFilePath(String operatingSystem, String crtFilePath){
		String crtFile = null;
		if("Linux".equals(operatingSystem)) {
			crtFile = Config.getConfig("pay/icbc/icbc_conf_linux.properties", crtFilePath);
		} else {
			crtFile = Config.getConfig("pay/icbc/icbc_conf_windows.properties", crtFilePath);
		}
		return crtFile;
	}

	public static String getKeyFilePath(String operatingSystem, String keyFilePath){
		String keyFile = null;
		if("Linux".equals(operatingSystem)) {
			keyFile = Config.getConfig("pay/icbc/icbc_conf_linux.properties", keyFilePath);
		} else{
			keyFile = Config.getConfig("pay/icbc/icbc_conf_windows.properties", keyFilePath);
		}
		return keyFile;
	}

	public static String getJksFilePath(String operatingSystem, String jksFilePath){
		String _jksFilePath = null;
		if("Linux".equals(operatingSystem)){
			_jksFilePath = Config.getConfig("pay/icbc/icbc_conf_linux.properties", jksFilePath);
		} else {
			_jksFilePath = Config.getConfig("pay/icbc/icbc_conf_windows.properties", jksFilePath);
		}
		return _jksFilePath;
	}

}
