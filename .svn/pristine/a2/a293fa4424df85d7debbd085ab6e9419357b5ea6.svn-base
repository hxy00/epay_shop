package com.emt.shoppay.dao.inter;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface IEpayOrderDetailDao {

    /**
     * 查询
     * @param rd
     * @return
     */
    List<Map<String, Object>> Select(@Param("rd") Map<String, Object> rd);

    /**
     * 更新
     * @param rd
     * @return
     */
    int Update(@Param("rd") Map<String, Object> rd);

    /**
     * 更新
     * @param rd
     * @return
     */
    int Insert(@Param("rd") Map<String, Object> rd);

    /**
     * 获取hour小时内未推送的订单数据
     *
     * @param hour
     * @return
     */
    List<Map<String, Object>> unPayList(@Param("hour") int hour, @Param("sysId") String sysId);
}
