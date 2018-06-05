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
    public static String getAppSecret(String merId){
        String secret = null;
        switch (merId) {
            case "1294158901":
                secret = "2E090BCC8329A298315E4891A01D42CC";
                break;
            case "1294121001":
                secret = "7a231e584a72a121d651df361fab418e";
                break;
            default:
                secret = "2E090BCC8329A298315E4891A01D42CC";
                break;
        }
        return secret;
    }
}
