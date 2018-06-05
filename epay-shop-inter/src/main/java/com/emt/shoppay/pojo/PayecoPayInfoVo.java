package com.emt.shoppay.pojo;


public class PayecoPayInfoVo extends BaseVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4730274211145261742L;
	String bankCardNo; // 借记卡
	String IDNumber; // 身份证号
	String mobileNumber; // 手机号
	String orderAmount; // 订单金额
	String merOrderNo; // 订单号
	String orderDescription; // 订单描述
	String url;              //回调地址
	String custName;    //持卡人姓名

	public String getBankCardNo() {
		return bankCardNo;
	}

	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}

	public String getIDNumber() {
		return IDNumber;
	}

	public void setIDNumber(String iDNumber) {
		IDNumber = iDNumber;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getMerOrderNo() {
		return merOrderNo;
	}

	public void setMerOrderNo(String merOrderNo) {
		this.merOrderNo = merOrderNo;
	}

	public String getOrderDescription() {
		return orderDescription;
	}

	public void setOrderDescription(String orderDescription) {
		this.orderDescription = orderDescription;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	
}
