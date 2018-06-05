package com.emt.shoppay.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by hdf on 2018/3/27
 */
@Mapper
public interface EpayOrderDetailMapper {


    /**
     * 查询支付订单数据
     * @param map
     * @return
     */
    List<Map<String, Object>> Select(Map<String, Object> map);


    /**
     * 更新
     * @param map
     * @return
     */
    int Update(Map<String, Object> map);

    /**
     * 更新
     * @param map
     * @return
     */
    int Insert(Map<String, Object> map);

    /**
     * 获取hour小时内未推送的订单数据
     *
     * @param hour
     * @param sysId
     * @return
     */
    List<Map<String, Object>> unPayList(@Param("hour") int hour, @Param("sysId") String sysId);
}
