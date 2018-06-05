package com.emt.shoppay.pojo.query;

/**
 * boc pay code 映射
 */
public class CcbReturnCode {
    public static String getMessage(String status){
        String comment = "未支付";
        switch (status) {
            case "0":
                comment = "失败";
                break;
            case "1":
                comment = "成功";
                break;
            case "2":
                comment = "待银行确认";
                break;
            case "3":
                comment = "已部分退款";
                break;
            case "4":
                comment = "已全部退款";
                break;
            case "5":
                comment = "待银行确认";
                break;
            default:
                comment = "未支付";
                break;
        }
        return comment;
    }
}
