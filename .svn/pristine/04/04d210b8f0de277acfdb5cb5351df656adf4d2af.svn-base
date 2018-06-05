package com.emt.shoppay.pojo;


import com.emt.shoppay.util.DateUtils;

public class IcbcOrderInfoPcVo extends BaseVO {
	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 1L;
	public String interfaceName = "ICBC_PERBANK_B2C";						// 接口名称
	public String interfaceVersion = "1.0.0.11";
	public String orderDate = DateUtils.DateTimeToYYYYMMDDhhmmss();			// 交易日期时间 格式为：YYYYMMDDHHmmss
	public String orderid = DateUtils.DateTimeToYYYYMMDDhhmmss() + "001";	// 订单号
	public Integer amount = 2;						// 订单金额 以分为单位
	public Integer installmentTimes = 1;			// 分期付款期数
	public String merAcct = "2403025119200022508";	// 商户账号
	public String goodsID = "10001";				// 商品编号 选输
	public String goodsName = "茅台云商产品";			// 商品名称 必输	*
	public String goodsNum = "1";					// 商品数量 选输
	public String carriageAmt = "1";				// 已含运费金额 选输
	public Integer verifyJoinFlag = 0;				// 联名校验标志 手机银行订单必输0，不校验 *
	public String Language = "ZH_CN";				// 语言版本
	public String curType = "001";					// 支付币种 目前工行只支持使用人民币（001）支付
	public String merID = "2403EC26104821";			// 商户代码 B2C ： 2403EC26104821 ；B2B：2403EC14717196
	public Integer creditType = 2;					// 支持订单支付的银行卡种类 ;
													// 取值范围为0、1、2，其中0表示仅允许使用借记卡支付，1表示仅允许使用信用卡支付，2表示借记卡和信用卡都能对订单进行支付
	public String notifyType = "HS";				// 通知类型 ： HS 通知（以HTTP协议POST方式） ； AG 不通知
	public String resultType = "1";					// 结果发送类型 取值“0”：无论支付成功或者失败，银行都向商户发送交易通知信息；
													// 取值“1”，银行只向商户发送交易成功的通知信息。

	public String merReference = "www.cmaotai.cn";	// 商户reference 选输
	public String merCustomIp = "";					// merCustomIp 客户端 选输
	public String goodsType = "1";					// 虚拟商品/实物商品标志位 选输 取值“0”：虚拟商品； 取值“1”，实物商品。

	public String merCustomID = "";					// 买家用户号 选输
	public String merCustomPhone = "";				// 买家联系电话 选输
	public String goodsAddress = "";				// 收货地址 选输
	public String merOrderRemark = "";				// 订单备注 选输

	public String merHint = "";						// 商城提示 选输
	public String remark1 = "";						// 备注字段1
	public String remark2 = "";						// 备注字段2

	public String merURL = "http://game.cdev.gzmt.pub/epay/notify/icbc";// 通知商户URL
																		// 必须合法的URL，交易结束，银行使用HTTP协议POST方式向此地址发送通知信息；目前只支持80端口
	public String merVAR = "cmaotai.cn.epay";		// 返回商户变量
	public String e_isMerFlag = ""; 				// 工银e支付注册标志 选输
	public String e_Name = "";						// 客户姓名 选输
	public String e_TelNum = ""; 					// 客户手机号 选输
	public String e_CredType = ""; 					// 客户证件类型 选输
	public String e_CredNum = ""; 					// 客户证件号 选输
	public String e_CardNo = ""; 					// 待注册工银e支付的卡/账号 选输
	public String orderFlag_ztb = ""; 				// 招投标订单标志 选输
	private String resultUrl = "";
	private Long qid;
	private String shopCode;

	public String getShopCode() {
		return shopCode;
	}

	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}

	public Long getQid() {
		return qid;
	}

	public void setQid(long qid) {
		this.qid = qid;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getInterfaceVersion() {
		return interfaceVersion;
	}

	public void setInterfaceVersion(String interfaceVersion) {
		this.interfaceVersion = interfaceVersion;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getInstallmentTimes() {
		return installmentTimes;
	}

	public void setInstallmentTimes(Integer installmentTimes) {
		this.installmentTimes = installmentTimes;
	}

	public String getCurType() {
		return curType;
	}

	public void setCurType(String curType) {
		this.curType = curType;
	}

	public String getMerID() {
		return merID;
	}

	public void setMerID(String merID) {
		this.merID = merID;
	}

	public String getMerAcct() {
		return merAcct;
	}

	public void setMerAcct(String merAcct) {
		this.merAcct = merAcct;
	}

	public Integer getVerifyJoinFlag() {
		return verifyJoinFlag;
	}

	public void setVerifyJoinFlag(Integer verifyJoinFlag) {
		this.verifyJoinFlag = verifyJoinFlag;
	}

	public String getLanguage() {
		return Language;
	}

	public void setLanguage(String language) {
		Language = language;
	}

	public String getGoodsID() {
		return goodsID;
	}

	public void setGoodsID(String goodsID) {
		this.goodsID = goodsID;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(String goodsNum) {
		this.goodsNum = goodsNum;
	}

	public String getCarriageAmt() {
		return carriageAmt;
	}

	public void setCarriageAmt(String carriageAmt) {
		this.carriageAmt = carriageAmt;
	}

	public String getMerHint() {
		return merHint;
	}

	public void setMerHint(String merHint) {
		this.merHint = merHint;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

	public String getMerURL() {
		return merURL;
	}

	public void setMerURL(String merURL) {
		this.merURL = merURL;
	}

	public String getMerVAR() {
		return merVAR;
	}

	public void setMerVAR(String merVAR) {
		this.merVAR = merVAR;
	}

	public String getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public Integer getCreditType() {
		return creditType;
	}

	public void setCreditType(Integer creditType) {
		this.creditType = creditType;
	}

	public String getMerReference() {
		return merReference;
	}

	public void setMerReference(String merReference) {
		this.merReference = merReference;
	}

	public String getMerCustomIp() {
		return merCustomIp;
	}

	public void setMerCustomIp(String merCustomIp) {
		this.merCustomIp = merCustomIp;
	}

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public String getMerCustomID() {
		return merCustomID;
	}

	public void setMerCustomID(String merCustomID) {
		this.merCustomID = merCustomID;
	}

	public String getMerCustomPhone() {
		return merCustomPhone;
	}

	public void setMerCustomPhone(String merCustomPhone) {
		this.merCustomPhone = merCustomPhone;
	}

	public String getGoodsAddress() {
		return goodsAddress;
	}

	public void setGoodsAddress(String goodsAddress) {
		this.goodsAddress = goodsAddress;
	}

	public String getMerOrderRemark() {
		return merOrderRemark;
	}

	public void setMerOrderRemark(String merOrderRemark) {
		this.merOrderRemark = merOrderRemark;
	}

	public String getE_isMerFlag() {
		return e_isMerFlag;
	}

	public void setE_isMerFlag(String e_isMerFlag) {
		this.e_isMerFlag = e_isMerFlag;
	}

	public String getE_Name() {
		return e_Name;
	}

	public void setE_Name(String e_Name) {
		this.e_Name = e_Name;
	}

	public String getE_TelNum() {
		return e_TelNum;
	}

	public void setE_TelNum(String e_TelNum) {
		this.e_TelNum = e_TelNum;
	}

	public String getE_CredType() {
		return e_CredType;
	}

	public void setE_CredType(String e_CredType) {
		this.e_CredType = e_CredType;
	}

	public String getE_CredNum() {
		return e_CredNum;
	}

	public void setE_CredNum(String e_CredNum) {
		this.e_CredNum = e_CredNum;
	}

	public String getE_CardNo() {
		return e_CardNo;
	}

	public void setE_CardNo(String e_CardNo) {
		this.e_CardNo = e_CardNo;
	}

	public String getOrderFlag_ztb() {
		return orderFlag_ztb;
	}

	public void setOrderFlag_ztb(String orderFlag_ztb) {
		this.orderFlag_ztb = orderFlag_ztb;
	}

	public String getResultUrl() {
		return resultUrl;
	}

	public void setResultUrl(String resultUrl) {
		this.resultUrl = resultUrl;
	}
	
	public String toTranData() {
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"no\"?>");
		stringBuilder.append("<B2CReq>");
		stringBuilder.append("<interfaceName>" + this.interfaceName + "</interfaceName>");
		stringBuilder.append("<interfaceVersion>" + this.interfaceVersion + "</interfaceVersion>");
		stringBuilder.append("<orderInfo>");
		stringBuilder.append("<orderDate>" + this.orderDate + "</orderDate>");
		stringBuilder.append("<curType>" + this.curType + "</curType>");
		stringBuilder.append("<merID>" + this.merID + "</merID>");
		stringBuilder.append("<subOrderInfoList>");
		stringBuilder.append("<subOrderInfo>");
		stringBuilder.append("<orderid>" + this.orderid + "</orderid>");
		stringBuilder.append("<amount>" + this.amount + "</amount>");
		stringBuilder.append("<installmentTimes>" + this.installmentTimes + "</installmentTimes>");
		stringBuilder.append("<merAcct>" + this.merAcct + "</merAcct>");
		stringBuilder.append("<goodsID>" + this.goodsID + "</goodsID>");
		stringBuilder.append("<goodsName>" + this.goodsName + "</goodsName>");
		stringBuilder.append("<goodsNum>" + this.goodsNum + "</goodsNum>");
		stringBuilder.append("<carriageAmt>" + this.carriageAmt + "</carriageAmt>");
		stringBuilder.append("</subOrderInfo>");
		stringBuilder.append("</subOrderInfoList>");
		stringBuilder.append("</orderInfo>");

		stringBuilder.append("<custom>");
		stringBuilder.append("<verifyJoinFlag>" + this.verifyJoinFlag + "</verifyJoinFlag>");
		stringBuilder.append("<Language>" + this.Language + "</Language>");
		stringBuilder.append("</custom>");

		stringBuilder.append("<message>");
		stringBuilder.append("<creditType>" + this.creditType + "</creditType>");
		stringBuilder.append("<notifyType>" + this.notifyType + "</notifyType>");
		stringBuilder.append("<resultType>" + this.resultType + "</resultType>");
		stringBuilder.append(" <merReference>" + this.merReference + "</merReference>");
		stringBuilder.append("<merCustomIp>" + this.merCustomIp + "</merCustomIp>");
		stringBuilder.append("<goodsType>" + this.goodsType + "</goodsType>");
		stringBuilder.append("<merCustomID>" + this.merCustomID + "</merCustomID>");
		stringBuilder.append("<merCustomPhone>" + this.merCustomPhone + "</merCustomPhone>");
		stringBuilder.append("<goodsAddress>" + this.goodsAddress + "</goodsAddress>");
		stringBuilder.append("<merOrderRemark>" + this.merOrderRemark + "</merOrderRemark>");
		stringBuilder.append("<merHint>" + this.merHint + "</merHint>");
		stringBuilder.append("<remark1>" + this.remark1 + "</remark1>");
		stringBuilder.append("<remark2>" + this.remark2 + "</remark2>");
		stringBuilder.append("<merURL>" + this.merURL + "</merURL>");
		stringBuilder.append("<merVAR>" + this.merVAR + "</merVAR>");
		stringBuilder.append("</message>");

		stringBuilder.append("<extend>");
		stringBuilder.append("<e_isMerFlag>" + this.e_isMerFlag + "</e_isMerFlag>");
		stringBuilder.append("<e_Name>" + this.e_Name + "</e_Name>");
		stringBuilder.append("<e_TelNum>" + this.e_TelNum + "</e_TelNum>");
		stringBuilder.append("<e_CredType>" + this.e_CredType + "</e_CredType>");
		stringBuilder.append("<e_CredNum>" + this.e_CredNum + "</e_CredNum>");
		stringBuilder.append("<e_CardNo>" + this.e_CardNo + "</e_CardNo>");
		stringBuilder.append("<orderFlag_ztb>" + this.orderFlag_ztb + "</orderFlag_ztb>");
		stringBuilder.append("</extend>");
		stringBuilder.append("</B2CReq>");

		return stringBuilder.toString();
	}

}
