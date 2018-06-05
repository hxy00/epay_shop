package com.emt.shoppay.dao.impl;

import com.emt.shoppay.dao.inter.IEpayOrderOperRecordDao;
import com.emt.shoppay.dao.mapper.IEpayOrderOperRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * epay详单操作记录
 * 
 * @ClassName: EpayOrderOperRecordMappers
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author huangdafei
 * @date 2016年12月12日 下午4:15:53
 * 
 */
@Repository("epayOrderOperRecordImpl")
public class EpayOrderOperRecordImpl implements IEpayOrderOperRecordDao {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IEpayOrderOperRecordMapper iEpayOrderOperRecordMapper;


	@Override
	public int Insert(Map<String, Object> rd) {
		int iresult = 0;
		try {
			return iEpayOrderOperRecordMapper.Insert(rd);
		} catch (Exception e){
			logger.error("epayOrderOperRecordImpl  Insert   失败:{}", e);
		}
		return iresult;
	}


}
