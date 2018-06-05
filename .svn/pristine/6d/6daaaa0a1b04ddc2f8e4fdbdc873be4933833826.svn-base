package com.emt.shoppay.dao.impl;

import com.emt.shoppay.dao.inter.IEpayOrderDetailDao;
import com.emt.shoppay.dao.mapper.EpayOrderDetailMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author hdf
 *
 */
@Repository
public class EpayOrderDetailDaoImpl implements IEpayOrderDetailDao {

    @Autowired
    private EpayOrderDetailMapper epayOrderDetailMapper;

     /**
     *
     * @param rd
     * @return
     */
    @Override
    public List<Map<String, Object>> Select(Map<String, Object> rd) {
        return epayOrderDetailMapper.Select(rd);
    }

    /**
     * 更新
     *
     * @param rd
     * @return
     */
    @Override
    public int Update(Map<String, Object> rd) {
        return epayOrderDetailMapper.Update(rd);
    }

    /**
     * 更新
     *
     * @param rd
     * @return
     */
    @Override
    public int Insert(Map<String, Object> rd) {
        return epayOrderDetailMapper.Insert(rd);
    }

    /**
     * 获取hour小时内未推送的订单数据
     *
     * @param hour
     * @return
     */
    @Override
    public List<Map<String, Object>> unPayList(int hour,  String sysId){
        return epayOrderDetailMapper.unPayList(hour, sysId);
    }
}
