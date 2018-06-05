package com.emt.shoppay.sv.inter;

import java.util.List;
import java.util.Map;

public interface IEpayOrderDetailSv {

    /**
     * 查询
     * @param rd
     * @return
     */
    List<Map<String, Object>> Select(Map<String, Object> rd);

    /**
     * 更新
     * @param rd
     * @return
     */
    int Update(Map<String, Object> rd);

    /**
     * 更新
     * @param rd
     * @return
     */
    int Insert(Map<String, Object> rd);

    /**
     * 获取hour小时内未推送的订单数据
     *
     * @param hour
     * @return
     */
    public List<Map<String, Object>> unPayList(int hour, String sysId);
}
