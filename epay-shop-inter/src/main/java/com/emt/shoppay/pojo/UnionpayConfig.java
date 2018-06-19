package com.emt.shoppay.pojo;

public class UnionpayConfig {
    public static String bkInterfaceVersion = "5.0.0";
    public static String encoding = "UTF-8";
    public static String merId = "105520159982268";
    public static String bizType = "000201";
    public static String notifyUrl = "/epay/unionpay/notify";
    public static String backUrl = "/epay/unionpay/back_notify";
    public static String timeOut = "30";

    //query
    public static String reqUrl = "https://gateway.95516.com/gateway/api/queryTrans.do";
}
