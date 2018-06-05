package com.emt.shoppay.util.sms;


import com.emt.shoppay.pojo.ReturnObject;
import com.emt.shoppay.util.StringUtils;
import com.emt.shoppay.util.httpclient.HttpsClientUtil;
import com.emt.shoppay.util.json.JSONObject;

/**
 * 短信发送
 */
public class SendSMS {
    private static final String SEND_SMS_URL =  "https://sv-api.cmaotai.com/s/sms/send?";
    private static final String INTERFACE_KEY = "f85d39ed5a40fd09966f13f12b6cf0f0";
    private static final String INTERFACE_NAME = "cmaotai_sms_send_ali";
    private static final String INTERFACE_VERSION = "1.0.1";
    private static final Long BUSI_ID = 56001L;

    public static ReturnObject sendSmsCode(String tel, String smsContent, String ip) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("phone", tel);
            obj.put("tcode", "SMS_100040014");
            JSONObject param = new JSONObject();
            param.put("code", String.valueOf(smsContent));
            obj.put("param", param);
            obj.put("platformId", "1");
            obj.put("ip", ip);
            String smsStr = obj.toString();

            String tranData = Des3Util.des3EncodeCBCString(INTERFACE_KEY, smsStr);
            System.out.println(tranData + ";..>:" + Des3Util.des3DecodeCBCString(INTERFACE_KEY, tranData));
            String signData = getMD5((tranData + "8e041f17a3c2afcc995b324a008709aa").getBytes());
            String url = SEND_SMS_URL + "interfaceName=" + INTERFACE_NAME + "&interfaceVersion=" + INTERFACE_VERSION + "&qid=123456&tranData=" + tranData + "&signData=" + signData + "&clientType=1&busiid=" + BUSI_ID;
            String res = HttpsClientUtil.doPost(url, "utf-8", null);
            System.out.println("短信发送res = " + res);
            if(StringUtils.isNotEmpty(res)) {
                JSONObject resultObj = new JSONObject(res);
                //成功
                if (Long.valueOf(resultObj.getInt("retcode")) == 0) {
                    return new ReturnObject(ReturnObject.SuccessEnum.success, "短信发送成功", null, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ReturnObject(ReturnObject.SuccessEnum.fail, "短信发送失败", null, 0);
    }

    private static String getMD5(byte[] source) {
        String s = null;
        char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest();          // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2];   // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0;                                // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) {    // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i];  // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];  // 取字节中高 4 位的数字转换,
                // >>> 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf];   // 取字节中低 4 位的数字转换
            }
            s = new String(str);  // 换后的结果转换为字符串

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
}
