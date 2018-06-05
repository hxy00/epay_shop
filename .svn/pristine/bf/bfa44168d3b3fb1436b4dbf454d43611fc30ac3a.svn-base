package com.emt.shoppay.pojo;

import com.emt.shoppay.util.BeanToMapUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

public class BaseVO implements Serializable{

    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     */
    private static final long serialVersionUID = 1L;

    public String toJson(){
        String strJson = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            //mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            strJson = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return strJson;
    }

    public static Map<String, Object> JsonToMap(String json) throws Exception, IOException{
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mapObject = mapper.readValue(json,
                new TypeReference<Map<String, Object>>() {
                });

        return mapObject;
    }

    public static <T> T MapToBean(Class<T> type, Map<String, Object> map) throws Exception {
        return BeanToMapUtil.MapToBean(type, map);
    }

    public static Map<String, Object> BeanToMap(Object bean) throws Exception{
        return BeanToMapUtil.BeanToMap(bean);
    }

    public  Map<String, Object> toMap() throws Exception{
        return BeanToMapUtil.BeanToMap(this);
    }
}
