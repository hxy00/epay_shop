package com.emt.shoppay.dao.impl;

import com.emt.shoppay.dao.inter.IEpayParaConfigDao;
import com.emt.shoppay.dao.mapper.IEpayParaConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 支付参数管理
* @ClassName: EpayParaConfigurationDaoImpl 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author huangdafei
* @date 2017年5月5日 下午8:01:37 
*
 */
@Repository
public class EpayParaConfigDaoImpl implements IEpayParaConfigDao {

	@Autowired
	private IEpayParaConfigMapper iEpayParaConfigurationMapper;


	@Override
	public int Update(Map<String, Object> rd) {
		return iEpayParaConfigurationMapper.Update(rd);
	}

	@Override
	public int Insert(Map<String, Object> rd) {
		return iEpayParaConfigurationMapper.Insert(rd);
	}

	@Override
	public List<Map<String, Object>> Select(Map<String, Object> rd)
			throws Exception {
		return iEpayParaConfigurationMapper.Select(rd);
	}

	@Override
	public int Delete(Map<String, Object> rd) {
        return iEpayParaConfigurationMapper.Delete(rd);
    }

	@Override
	public List<Map<String, Object>> RecordCount(String sql) {
        return iEpayParaConfigurationMapper.RecordCount(sql);
    }
	
}
