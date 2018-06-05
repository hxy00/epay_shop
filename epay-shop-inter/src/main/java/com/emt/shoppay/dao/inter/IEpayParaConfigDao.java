package com.emt.shoppay.dao.inter;

import java.util.List;
import java.util.Map;

public interface IEpayParaConfigDao {
    int Update(Map<String, Object> rd);


    int Insert(Map<String, Object> rd);


    List<Map<String, Object>> Select(Map<String, Object> rd) throws Exception;


    int Delete(Map<String, Object> rd);


    List<Map<String, Object>>  RecordCount(String sql);
}
