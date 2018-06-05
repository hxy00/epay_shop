package com.emt.shoppay.pojo.query;

/**
 * abc pay code 映射
 */
public class AbcReturnCode {
    public static String getMessage(String status){
        String comment = "未支付";
        switch (status) {
            case "01":
                comment = "未支付";
                break;
            case "02":
                comment = "无回应";
                break;
            case "03":
                comment = "已请款";
                break;
            case "04":
                comment = "成功";
                break;
            case "05":
                comment = "已退款";
                break;
            case "07":
                comment = "授权确认成功";
                break;
            case "00":
                comment = "授权已取消";
                break;
            case "99":
                comment = "失败";
                break;
            default:
                comment = "未支付";
                break;
        }
        return comment;
    }
}
