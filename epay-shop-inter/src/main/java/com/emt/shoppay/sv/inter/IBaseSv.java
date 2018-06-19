package com.emt.shoppay.sv.inter;

import java.util.Map;

public interface IBaseSv {

    public Integer insertPayOrderDetail(Map<String, String> upTranData,
                                        Map<String, Object> upExtend, Map<String, String> extend) throws Exception;
}
