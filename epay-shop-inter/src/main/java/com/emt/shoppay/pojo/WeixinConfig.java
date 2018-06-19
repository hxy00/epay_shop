package com.emt.shoppay.pojo;

/**
 * 根据商户号配置相应密钥
 * @ClassName: WeixinConfig
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author huangdafei
 * @date 2017年6月14日 下午5:36:36
 *
 */
public class WeixinConfig {
    public static String bkInterfaceVersion = "1.0.0.2";
    public static String reqUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    public static String appId = "wx7a2dfd9101d0bece";
    public static String mchId = "1220734401";
    public static String appSecret = "a27ac11c644038a10399f4aab5f07474";
    public static String key = "a27ac11c644038a10399f4aabemao888";
    public static String notyfiUrl = "/epay/weixin/notify_wap";
    public static String timeOut = "30";

    //query
    public static String queryReqUrl = "https://api.mch.weixin.qq.com/pay/orderquery";
}
