package com.emt.shoppay.pojo;


import com.emt.shoppay.util.DateUtils;

public class IcbcOrderInfoWapVo extends BaseVO {
    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     */
    private static final long serialVersionUID = 1L;
    public String interfaceName = "ICBC_WAPB_B2C";							// 接口名称
    public String interfaceVersion = "1.0.0.6";
    public String orderDate = DateUtils.DateTimeToYYYYMMDDhhmmss();			// 交易日期 时间 格式为：YYYYMMDDHHmmss
    public String orderid = DateUtils.DateTimeToYYYYMMDDhhmmss() + "001";	// 订单号
    public Integer amount = 2;						// 订单金额 以分为单位
    public Integer installmentTimes = 1;			// 分期付款期数
    public String curType = "001";					// 支付币种 目前工行只支持使用人民币（001）支付
    public String merID = "2403EC26104821";			// ShopCode 商户代码 B2C ： 2403EC26104821 ；B2B ：2403EC14717196
    public String merAcct = "2403025119200022508";	// ShopAccount 商户账号
    public Integer verifyJoinFlag = 0;				// 联名校验标志 手机银行订单必输0，不校验
    public String Language = "zh_CN";				// 语言版本
    public Long qid;

    public String goodsID = "";						// 商品编号 选输
    public String goodsName = "";					// 商品名称 选输
    public String goodsNum = "";					// 商品数量 选输
    public String carriageAmt = "";					// 已含运费金额 选输

    public String merHint = "";						// 商城提示 选输
    public String remark1 = "";						// 备注字段1
    public String remark2 = "";						// 备注字段2
    public String merURL = "http://game.cdev.gzmt.pub/epay/icbc/notify";// 通知商户URL
    // 必须合法的URL，交易结束，银行使用HTTP协议POST方式向此地址发送通知信息；目前只支持80端口
    public String merVAR = "emaotai.cn.epay";		// 返回商户变量
    public String notifyType = "HS";				// 通知类型 ： HS 通知（以HTTP协议POST方式） ； AG 不通知
    public String resultType = "0";					// 结果发送类型 取值“0”：无论支付成功或者失败，银行都向商户发送交易通知信息；
    // 取值“1”，银行只向商户发送交易成功的通知信息。
    public String backup1 = "";
    public String backup2 = "";
    public String backup3 = "";
    public String backup4 = "";
    public String resultUrl = "";
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

    public void setQid(Long qid) {
        this.qid = qid;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
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

    public String getBackup1() {
        return backup1;
    }

    public void setBackup1(String backup1) {
        this.backup1 = backup1;
    }

    public String getBackup2() {
        return backup2;
    }

    public void setBackup2(String backup2) {
        this.backup2 = backup2;
    }

    public String getBackup3() {
        return backup3;
    }

    public void setBackup3(String backup3) {
        this.backup3 = backup3;
    }

    public String getBackup4() {
        return backup4;
    }

    public void setBackup4(String backup4) {
        this.backup4 = backup4;
    }

    public String toTranData() {
        StringBuffer stringBuilder = new StringBuffer();
        stringBuilder
                .append("<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"no\"?>");
        stringBuilder.append("<B2CReq>");
        stringBuilder.append("<interfaceName>" + this.interfaceName
                + "</interfaceName>");
        stringBuilder.append("<interfaceVersion>" + this.interfaceVersion
                + "</interfaceVersion>");
        stringBuilder.append("<orderInfo>");
        stringBuilder.append("<orderDate>" + this.orderDate + "</orderDate>");
        stringBuilder.append("<orderid>" + this.orderid + "</orderid>");
        stringBuilder.append("<amount>" + this.amount + "</amount>");
        stringBuilder.append("<installmentTimes>" + this.installmentTimes
                + "</installmentTimes>");
        stringBuilder.append("<curType>" + this.curType + "</curType>");
        stringBuilder.append("<merID>" + this.merID + "</merID>");
        stringBuilder.append("<merAcct>" + this.merAcct + "</merAcct>");
        stringBuilder.append("</orderInfo>");
        stringBuilder.append("<custom>");
        stringBuilder.append("<verifyJoinFlag>" + this.verifyJoinFlag
                + "</verifyJoinFlag>");
        stringBuilder.append("<Language>" + this.Language + "</Language>");
        stringBuilder.append("</custom>");
        stringBuilder.append("<message>");
        stringBuilder.append("<goodsID>" + this.goodsID + "</goodsID>");
        stringBuilder.append("<goodsName>" + this.goodsName + "</goodsName>");
        stringBuilder.append("<goodsNum>" + this.goodsNum + "</goodsNum>");
        stringBuilder.append("<carriageAmt>" + this.carriageAmt
                + "</carriageAmt>");
        stringBuilder.append("<merHint>" + this.merHint + "</merHint>");
        stringBuilder.append("<remark1>" + this.remark1 + "</remark1>");
        stringBuilder.append("<remark2>" + this.remark2 + "</remark2>");
        stringBuilder.append("<merURL>" + this.merURL + "</merURL>");
        stringBuilder.append("<merVAR>" + this.merVAR + "</merVAR>");
        stringBuilder
                .append("<notifyType>" + this.notifyType + "</notifyType>");
        stringBuilder
                .append("<resultType>" + this.resultType + "</resultType>");
        stringBuilder.append("<backup1>" + this.backup1 + "</backup1>");
        stringBuilder.append("<backup2>" + this.backup2 + "</backup2>");
        stringBuilder.append("<backup3>" + this.backup3 + "</backup3>");
        stringBuilder.append("<backup4>" + this.backup4 + "</backup4>");
        stringBuilder.append("</message>");
        stringBuilder.append("</B2CReq>");
        return stringBuilder.toString();
    }

}
