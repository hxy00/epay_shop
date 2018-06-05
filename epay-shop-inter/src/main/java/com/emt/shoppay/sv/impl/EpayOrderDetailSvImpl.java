package com.emt.shoppay.sv.impl;

import com.emt.shoppay.dao.inter.IEpayOrderDetailDao;
import com.emt.shoppay.sv.inter.IEpayOrderDetailSv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018-03-27.
 */
@Service
public class EpayOrderDetailSvImpl implements IEpayOrderDetailSv {

    @Autowired
    private IEpayOrderDetailDao iEpayOrderDetailDao;

    /**
     * 获取hour小时内未推送的订单数据
     *
     * @param hour
     * @return
     */
    public List<Map<String, Object>> unPayList(int hour, String sysId){
        return iEpayOrderDetailDao.unPayList(hour, sysId);
    }

    @Override
    public List<Map<String, Object>> Select(Map<String, Object> rd) {
        return iEpayOrderDetailDao.Select(rd);
    }

    @Override
    public int Update(Map<String, Object> rd) {
        return iEpayOrderDetailDao.Update(rd);
    }

    @Override
    public int Insert(Map<String, Object> rd) {
        return iEpayOrderDetailDao.Insert(rd);
    }
}
