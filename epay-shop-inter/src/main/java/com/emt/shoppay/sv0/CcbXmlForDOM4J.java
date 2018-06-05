package com.emt.shoppay.sv0;

import com.alibaba.druid.util.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CcbXmlForDOM4J {
	public static Map<String, Object> getMapByXml(String xml) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			Document doc = (Document) DocumentHelper.parseText(xml);
			Element books = doc.getRootElement();
			Iterator Elements = books.elementIterator();
			while (Elements.hasNext()) {
				Element element = (Element) Elements.next();

				if (!StringUtils.isEmpty(element.getTextTrim())) {
					resultMap.put(element.getName(), element.getTextTrim());
				}

				if ("QUERYORDER".equals(element.getName())) {
					Map<String, String> orderInfoMap = new HashMap<String, String>();
					List subElements = element.elements();
					//得到全部子节点
                    for (int i = 0; i < subElements.size(); i++) {
                        Element ele = (Element) subElements.get(i);
                        orderInfoMap.put(ele.getName(), ele.getTextTrim());
                    }
                    resultMap.put("QUERYORDER", orderInfoMap);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultMap;
	}
}
