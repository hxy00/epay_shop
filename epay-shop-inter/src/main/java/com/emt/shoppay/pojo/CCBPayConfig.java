package com.emt.shoppay.pojo;

public class CCBPayConfig {
    public static String bkInterfaceVersion = "1.0";
    public static String PMERCHANTID = "105520373990014";
    public static String POSID = "902839972";
    public static String BRANCHID = "520000000";
    public static String CURCODE = "01";
    public static String TXCODE = "520100";
    public static String REMARK1 = "shoppay";
    public static String REMARK2 = "shoppay";
    public static String TYPE = "1";
    public static String PUB = "30819d300d06092a864886f70d010101050003818b0030818702818100a8da38ea2712cd00ab459f14e8663238f5233a0ae0c632a19c5eda7862bf9a4d9059f5f94d4c02efbf99e7e3c9a18f395872e285437aabcc035ae63cebecbc46e16f53196d074c08879eba9587f240e710a80d8989de54e9489db01446a439615af3df60bb93608a101959756786816886177616ca13f63edc2e32fc4e5e6c4d020113";
    public static String notifyUrl = "/epay/ccb/Ccb_notify";
    public static String timeOut = "30";

    //query
    public static String QMERCHANTID = "105520373990014";
    public static String QBRANCHID = "520000000";
    public static String QPOSID = "902839972";
    public static String QTXCODE = "410408";
    public static String QUPWD = "cmaotai11ww";
    public static String QSEL_TYPE = "3";
    public static String bankURL = "https://ibsbjstar.ccb.com.cn/CCBIS/ccbMain";
}
