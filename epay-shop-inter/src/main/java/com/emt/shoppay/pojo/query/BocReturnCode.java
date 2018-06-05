package com.emt.shoppay.pojo.query;

/**
 * boc pay code 映射
 */
public class BocReturnCode {
    public static String getMessage(String status){
        String comment = "未支付";
        switch (status) {
            case "0":
                comment = "初始";
                break;
            case "1":
                comment = "成功";
                break;
            case "2":
                comment = "失败";
                break;
            case "3":
                comment = "银行处理中";
                break;
            case "4":
                comment = "扣款成功";
                break;
            default:
                comment = "未支付";
                break;
        }
        return comment;
    }
}
