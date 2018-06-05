package com.emt.shoppay.pojo.query;

/**
 * boc pay code 映射
 */
public class IcbcReturnCode {
    public static String getMessage(String status){
        String comment = "未支付";
        switch (status) {
            case "1":
                comment = "交易成功，已清算";
                break;
            case "2":
                comment = "交易失败";
                break;
            case "3":
                comment = "交易可疑";
                break;
            default:
                comment = "未支付";
                break;
        }
        return comment;
    }
}
