package com.emt.shoppay.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BeanToMapUtil {
    
    /**
     * 将一个 Map 对象转化为一个 JavaBean
     * @param type 要转化的类型
     * @param map 包含属性值的 map
     * @return 
     * @return 转化出来的 JavaBean 对象
     * @throws IntrospectionException
     *             如果分析类属性失败
     * @throws IllegalAccessException
     *             如果实例化 JavaBean 失败
     * @throws InstantiationException
     *             如果实例化 JavaBean 失败
     * @throws InvocationTargetException
     *             如果调用属性的 setter 方法失败
     */
    public static <T> T MapToBean(Class<T> type, Map<String, Object> map) 
            throws Exception {
        
        BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
        T obj = type.newInstance(); // 创建 JavaBean 对象

        // 给 JavaBean 对象的属性赋值
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();

            if (map.containsKey(propertyName)) {
                // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                Object value = map.get(propertyName);
                if(value!=null){
                    Object[] args = new Object[1];
                    args[0] = value;
    
                    descriptor.getWriteMethod().invoke(obj, args);
                }
            }
        }
        return obj;
    }

    /**
     * 将一个 JavaBean 对象转化为一个  Map
     * @param bean 要转化的JavaBean 对象
     * @return 转化出来的  Map 对象
     * @throws IntrospectionException 如果分析类属性失败
     * @throws IllegalAccessException 如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */  
    public static Map<String, Object> BeanToMap(Object bean)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        Class type = bean.getClass();
        Map returnMap = new HashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);

        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    returnMap.put(propertyName, result);
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }
       
    
    public static void main(String args[]) throws Exception {
        class user{
            String name = "123";
            long age = 13;
            boolean sex = false;
            Date d=new Date();
            long u_tel = 13511987879L;
            
            public long getU_tel() {
                return u_tel;
            }
            public void setU_tel(long u_tel) {
                this.u_tel = u_tel;
            }
            public String getName() {
                return name;
            }
            public void setName(String name) {
                this.name = name;
            }
            public long getAge() {
                return age;
            }
            public void setAge(long age) {
                this.age = age;
            }
            public boolean isSex() {
                return sex;
            }
            public void setSex(boolean sex) {
                this.sex = sex;
            }
            public Date getD() {
                return d;
            }
            public void setD(Date d) {
                this.d = d;
            }  
            
            public String toJson(){
                String strJson = "";
                ObjectMapper mapper = new ObjectMapper(); 
                try {
                    strJson = mapper.writeValueAsString(this);
                } catch (JsonProcessingException e) {            
                    e.printStackTrace();
                }
                return strJson;
            }
        }
        Map<String, Object>  map= BeanToMap(new user());
        user u =  MapToBean(user.class ,map);
        System.out.println(u.toJson());
        System.out.println(map);
    }
}