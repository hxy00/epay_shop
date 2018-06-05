package com.emt.shoppay.sv.impl;

import com.emt.shoppay.dao.inter.IEpayOrderDetailDao;
import com.emt.shoppay.pojo.EpayOrderDetail;
import com.emt.shoppay.sv.inter.IBaseSv;
import com.emt.shoppay.util.*;
import com.emt.shoppay.util.httpclient.HttpsClientUtil;
import com.emt.shoppay.util.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收银台公共基础类
 * @ClassName: BaseSvImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author huangdafei
 * @date 2017年5月5日 下午2:47:59
 *
 */
public class BaseSvImpl implements IBaseSv {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    protected IEpayOrderDetailDao iEpayOrderDetailDao;

    /*
     * 保存到详单表-公共方法
    * Title: insertPayOrderDetail
    *Description:
    * @param upTranData
    * @param upExtend
    * @param dbExtend
    * @param extend
    * @return
    * @throws Exception
    * @see com.emt.modules.epay.sv.inter.IBaseSv#insertPayOrderDetail(java.util.Map, java.util.Map, java.util.Map, java.util.Map)
     */
    @Override
    public Integer insertPayOrderDetail(Map<String, String> upTranData,
                                        Map<String, Object> upExtend, Map<String, String> dbExtend, Map<String, String> extend) throws Exception {
        //从客户端上送的交易数据中取值
        String orderId = getValue(upTranData, "orderId");
        String subject = getValue(upTranData, "subject");
        String totalFee = getValue(upTranData, "totalFee");
        String notifyUrl = getValue(upTranData, "notifyUrl");

        //从客户端上送的其他字段中取值
        String payCompany = getValue(upExtend, "interfaceName", null);//系统接口名称
        String interfaceVersion = getValue(upExtend, "interfaceVersion", null);//系统接口版本
        String qid = getValue(upExtend, "qid", null);
        String clientType = getValue(upExtend, "clientType", null);
        String busiid = getValue(upExtend, "busiid", null);
        String sysId = getValue(upExtend, "sysId", null);
        String merReference = getValue(upExtend, "merReference", null);

        //从临时构建中获取
        String merURL = getValue(extend, "merUrl");
        String merVAR = getValue(extend, "merVAR");
        String orderDate = getValue(extend, "orderDate");
        String buildData = getValue(extend, "buildData");
        String shopCode = getValue(extend, "shopCode");

        //从数据库查询出的数据中取值
        String bkInterfaceName = getValue(dbExtend, "interfaceName");//银联接口名称
        String bkInterfaceVersion = getValue(dbExtend, "interfaceVersion");//银联接口版本

        EpayOrderDetail orderDetail = new EpayOrderDetail();
        orderDetail.setAmount(Long.valueOf(totalFee));
        orderDetail.setClientType(clientType);
        orderDetail.setEmt_sys_id(Integer.valueOf(sysId));
        orderDetail.setInterfaceName(bkInterfaceName);
        orderDetail.setInterfaceVersion(bkInterfaceVersion);
        orderDetail.setMerURL(merURL);
        orderDetail.setMerVAR(merVAR);
        orderDetail.setOrderDate(orderDate);
        orderDetail.setOrderid(orderId);
        orderDetail.setPayCompany(payCompany);
        orderDetail.setTranData(buildData);
        orderDetail.setResultUrl(notifyUrl);
        orderDetail.setQid(Long.valueOf(qid));
        orderDetail.setShopCode(shopCode);

        Map<String, Object> rd = new HashMap<String, Object>();
        rd.put("orderid", orderDetail.getOrderid());
        rd.put("payCompany", orderDetail.getPayCompany());
        List<Map<String, Object>> list = iEpayOrderDetailDao.Select(rd);
        if (list != null && list.size() > 0) {
            rd.clear();
            rd = BeanToMapUtil.BeanToMap(orderDetail);
            rd.put("updateTime", "now");//设置定为now，表示不为空，构建sql时
//			rd.put("orderid", orderDetail.getOrderid());
//			rd.put("payCompany", orderDetail.getPayCompany());
            return iEpayOrderDetailDao.Update(rd);
        } else {
            return iEpayOrderDetailDao.Insert(orderDetail.toMap());
        }
    }

    /**
     * 从Map<String, String>获取key的值
     * @Title: getValue
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param map
     * @param @param key
     * @param @return  参数说明
     * @return String    返回类型
     * @throws
     */
    public String getValue(Map<String, String> map, String key) {
        if (map.containsKey(key) && map.get(key) != null)
            return map.get(key);
        return "";
    }

    /**
     * 从Map<String, Object>获取key的值
     * @Title: getValue
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param map
     * @param @param key
     * @param @return  参数说明
     * @return String    返回类型
     * @throws
     */
    public String getValue(Map<String, Object> map, String... key) {
        if (map.containsKey(key[0]) && map.get(key[0]) != null)
            return map.get(key[0]).toString();
        return "";
    }

    /**
     * 生成签名
     * @Title: getSign
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param busiid
     * @param @param json
     * @param @return  参数说明
     * @return String    返回类型
     * @throws
     */
    public String getSign(String busiid, String json) {
        try {
            String secret = GoldConst.getKey(busiid);
            String secretStr = json + secret;
            return DigestUtils.md5Hex(secretStr.getBytes("UTF-8")).toUpperCase();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 将支付结果告诉给其他平台
     * @Title: postPayResult
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param map
     * @param @throws Exception  参数说明
     * @return void    返回类型
     * @throws
     */
    public void postPayResult(Map<String, Object> map) throws Exception {
        String orderId = map.get("orderId").toString();
        String discountAmount = map.get("discountAmount").toString();
        String resultUrl = map.get("resultUrl").toString();

        Map<String, Object> rd = new HashMap<String, Object>();
        rd.put("orderid", orderId);
        rd.put("tranStat", "1");
        List<Map<String, Object>> list = this.iEpayOrderDetailDao.Select(rd);
        if ((list == null) || (list.size() == 0)) {
            this.logger.debug("[postPayResult] 订单号：" + orderId + "，未查询到支付记录，未能更新IsPost、Times、IsSend字段！");
            return;
        }

        Map<String, Object> lstMap = list.get(0);
        String payCompany = lstMap.get("payCompany").toString();
        String amount = lstMap.get("amount").toString();
        String tranStat = lstMap.get("tranStat").toString();

        Map<String, String> pMap = new HashMap<String, String>();
        pMap.put("orderId", orderId);
        pMap.put("payCompany", payCompany);
        pMap.put("amount", amount);
        pMap.put("tranStat", tranStat);
        pMap.put("discountAmount", discountAmount);
        pMap.put("interfaceName", payCompany);
        pMap.put("asyn", "1");

        String tranMapJson = ToolsUtil.mapToJson(pMap);
        String tranData = Base64Util.encodeBase64(tranMapJson, "UTF-8");
        String sign = getSign("10001", tranData);

        String hostAddr = Global.getConfig("epay.pay.success.url");
        if (resultUrl.contains("/Order/pay/payOrderSuccess")) {//大額支付的
            hostAddr = Global.getConfig("epay.pay.success.url");
        } else {
            hostAddr = resultUrl;
        }
        StringBuffer url = new StringBuffer();
        url.append(hostAddr);
        url.append(hostAddr.contains("?") ? "&" : "?");
        url.append("tranData=").append(tranData);
        url.append("&sign=").append(sign);

        this.logger.debug("[postPayResult] 请求URL：" + url);
        String result = null;
        try {
            Boolean isHttps = Boolean.valueOf(hostAddr.toLowerCase().contains("https://"));
            if (isHttps){
                result = HttpsClientUtil.doGet(url.toString(), "utf-8");
            } else {
                result = HttpUtil.doGet(url.toString(), null);
            }
        } catch (Exception e) {
            this.logger.debug("[postPayResult] POST支付内容失败，订单号：" + orderId);
        }
        logger.debug("[postPayResult] 订单orderId={}，请求远程服务器回调返回json={}", orderId, result);
        try {
            if ((result != null) && (!"".equals(result))) {
                JSONObject jObject = new JSONObject(result);
                Boolean state = Boolean.valueOf(jObject.getBoolean("state"));
                Integer resultCode = Integer.valueOf(jObject.getInt("code"));

                rd.clear();
                if (state && resultCode == 0) {
                    rd.put("IsPost", Integer.valueOf(1));
                    rd.put("Times", Integer.valueOf(1));
                    rd.put("IsSend", Integer.valueOf(0));
                    rd.put("orderid", orderId);
                    rd.put("payCompany", payCompany);
                    this.iEpayOrderDetailDao.Update(rd);
                    this.logger.debug("[postPayResult] iEpayOrderDetailDao.Update：success");
                } else {
                    rd.put("IsPost", Integer.valueOf(0));
                    rd.put("Times", Integer.valueOf(1));
                    rd.put("IsSend", Integer.valueOf(0));
                    rd.put("orderid", orderId);
                    rd.put("payCompany", payCompany);
                    this.iEpayOrderDetailDao.Update(rd);
                    this.logger.debug("[postPayResult] iEpayOrderDetailDao.Update：success");
                }
            } else {
                rd.clear();
                rd.put("IsPost", Integer.valueOf(0));
                rd.put("Times", Integer.valueOf(1));
                rd.put("IsSend", Integer.valueOf(0));
                rd.put("orderid", orderId);
                rd.put("payCompany", payCompany);
                this.iEpayOrderDetailDao.Update(rd);
                this.logger.debug("[postPayResult] iEpayOrderDetailDao.Update：success");
            }
        } catch (Exception e) {
            logger.error("订单：" + orderId + "，请求远程回调时发生错误，错误原因：" + e.getMessage());
            rd.clear();
            rd.put("IsPost", Integer.valueOf(0));
            rd.put("Times", Integer.valueOf(1));
            rd.put("IsSend", Integer.valueOf(0));
            rd.put("orderid", orderId);
            rd.put("payCompany", payCompany);
            iEpayOrderDetailDao.Update(rd);
        }
    }
}
