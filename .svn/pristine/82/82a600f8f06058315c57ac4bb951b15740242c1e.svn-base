package com.emt.shoppay.sv0;

import com.alibaba.druid.util.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WeixinXmlForDOM4J {
	public static Map<String, String> parse_wap_b2c(String protocolXML){
		Map<String, String> mapResult = new HashMap<String, String>();
		
		try{
			Document doc = (Document) DocumentHelper.parseText(protocolXML);
			Element books = doc.getRootElement();
			
			Iterator Elements = books.elementIterator();
			
			while (Elements.hasNext()){
				Element element = (Element) Elements.next();
				if( !StringUtils.isEmpty( element.getTextTrim() )){
                    mapResult.put(element.getName(), element.getTextTrim());
                }
			}
		}catch(Exception e){
			
		}
		return mapResult;
	}
}
