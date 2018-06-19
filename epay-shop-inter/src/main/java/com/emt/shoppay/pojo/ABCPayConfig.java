package com.emt.shoppay.pojo;

/**
 * 农行支付配置
 */
public class ABCPayConfig {
    public static String bkInterfaceVersion = "3.1.0";
    public static String merchantID = "103882399990122";
    public static String payTypeID = "ImmediatePay";
    public static String notifyUrlPC = "/epay/abc/abc_notify_pc";
    public static String notifyUrlWAP = "/epay/abc/abc_notify_wap";
    public static String configIndex = "2";
    public static String timeOut = "30";
    public static String queryDetail = "0";//0查订单状态，1查订单详情
}
