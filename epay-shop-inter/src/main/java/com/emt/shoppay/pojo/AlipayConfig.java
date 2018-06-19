package com.emt.shoppay.pojo;

public class AlipayConfig {
    public static String bkInterfaceVersion = "1.0";
    public static String reqUrl = "https://mapi.alipay.com/gateway.do?";
    public static String partner = "2088511535991599";
    public static String sellerEmail = "mtjtwssc@126.com";
    public static String key = "zlqlaznm9bgn0gvcf4mc2uavjfa1pgke";
    public static String inputCharset = "utf-8";
    public static String signType = "MD5";
    public static String paymentType = "1";
    public static String clientId = "";
    public static String privateKey = "";
    public static String desKey = "";
    public static String logPath = "";
    public static String returnUrlPC = "/epay/alipay/notify_pc";
    public static String returnUrlWAP = "/epay/alipay/notify_wap";
    public static String notifyUrlPC = "/epay/alipay/notify_asyn_pc";
    public static String notifyUrlWAP = "/epay/alipay/notify_asyn_wap";
    public static String errorUrl = "/epay/alipay/error_notify";
    public static String timeOut = "30";

    public static String queryService = "single_trade_query";
}
