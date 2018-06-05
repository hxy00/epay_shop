package com.emt.shoppay.pojo;

import java.io.Serializable;
import java.util.Date;


/**
 *
 * @ClassName: Epay订单详情表
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author huangdafei
 * @date 2017年4月28日 下午2:20:37
 *
 */
public class EpayOrderDetail extends BaseVO implements Serializable {

    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     */
    private static final long serialVersionUID = 1L;

    private String orderid;
    private String payCompany;
    private String interfaceName;
    private String interfaceVersion;
    private Long Qid;
    private Long amount;
    private String orderDate;
    private String merURL;
    private String clientType;
    private String merVAR;
    private String notifyType;
    private String notifyData;
    private String TranSerialNo;
    private String notifyDate;
    private String tranStat = "0";
    private String comment;
    private Date Create_date = new Date();
    private Date Update_date = new Date();
    private Integer Emt_sys_id;
    private String TranData;
    private String ResultUrl;
    private String phone;
    private String shopCode;
    private Integer IsPost;
    private Integer Times;
    private Integer IsSend;

    public String getOrderid() {
        return orderid;
    }
    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }
    public String getPayCompany() {
        return payCompany;
    }
    public void setPayCompany(String payCompany) {
        this.payCompany = payCompany;
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
    public Long getQid() {
        return Qid;
    }
    public void setQid(Long qid) {
        Qid = qid;
    }
    public Long getAmount() {
        return amount;
    }
    public void setAmount(Long amount) {
        this.amount = amount;
    }
    public String getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
    public String getMerURL() {
        return merURL;
    }
    public void setMerURL(String merURL) {
        this.merURL = merURL;
    }
    public String getClientType() {
        return clientType;
    }
    public void setClientType(String clientType) {
        this.clientType = clientType;
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
    public String getNotifyData() {
        return notifyData;
    }
    public void setNotifyData(String notifyData) {
        this.notifyData = notifyData;
    }
    public String getTranSerialNo() {
        return TranSerialNo;
    }
    public void setTranSerialNo(String tranSerialNo) {
        TranSerialNo = tranSerialNo;
    }
    public String getNotifyDate() {
        return notifyDate;
    }
    public void setNotifyDate(String notifyDate) {
        this.notifyDate = notifyDate;
    }
    public String getTranStat() {
        return tranStat;
    }
    public void setTranStat(String tranStat) {
        this.tranStat = tranStat;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Date getCreate_date() {
        return Create_date;
    }
    public void setCreate_date(Date create_date) {
        Create_date = create_date;
    }
    public Date getUpdate_date() {
        return Update_date;
    }
    public void setUpdate_date(Date update_date) {
        Update_date = update_date;
    }
    public Integer getEmt_sys_id() {
        return Emt_sys_id;
    }
    public void setEmt_sys_id(Integer emt_sys_id) {
        Emt_sys_id = emt_sys_id;
    }
    public String getTranData() {
        return TranData;
    }
    public void setTranData(String tranData) {
        TranData = tranData;
    }
    public String getResultUrl() {
        return ResultUrl;
    }
    public void setResultUrl(String resultUrl) {
        ResultUrl = resultUrl;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getShopCode() {
        return shopCode;
    }
    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }
    public Integer getIsPost() {
        return IsPost;
    }
    public void setIsPost(Integer isPost) {
        IsPost = isPost;
    }
    public Integer getTimes() {
        return Times;
    }
    public void setTimes(Integer times) {
        Times = times;
    }
    public Integer getIsSend() {
        return IsSend;
    }
    public void setIsSend(Integer isSend) {
        IsSend = isSend;
    }

}
