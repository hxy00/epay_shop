package com.emt.shoppay.dao.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Author: Mr.Huang
 * @Date: 2018-05-08
 * @Description:
 */
@Mapper
public interface IEpayParaConfigMapper {
    int Update(Map<String, Object> rd);


    int Insert(Map<String, Object> rd);


    List<Map<String, Object>> Select(Map<String, Object> rd) throws Exception;


    int Delete(Map<String, Object> rd);


    List<Map<String, Object>>  RecordCount(String sql);
}
