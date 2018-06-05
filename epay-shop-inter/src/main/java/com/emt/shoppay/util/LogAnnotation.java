package com.emt.shoppay.util;

 
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface LogAnnotation {
    
   String name();
    
   String describe()  default "";
   
   boolean val();
}