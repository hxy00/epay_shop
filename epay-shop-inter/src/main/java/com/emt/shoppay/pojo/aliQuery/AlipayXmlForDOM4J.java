package com.emt.shoppay.pojo.aliQuery;

import com.alibaba.druid.util.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * alipay xml解析
* @ClassName: AlipayXmlForDOM4J 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author huangdafei
* @date 2016年12月16日 下午1:39:34 
*
 */
public class AlipayXmlForDOM4J {
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
				if ("request".equals(element.getName())) {
					Map<String, String> reqMap = new HashMap<String, String>();
					List subElements = element.elements();
					for (int i = 0; i < subElements.size(); i++) {
                        Element ele = (Element) subElements.get(i);
                        Attribute attr = ele.attribute(0);
                        reqMap.put(attr.getValue(), ele.getTextTrim());
                    }
                    resultMap.put("request", reqMap);
				}
				if ("response".equals(element.getName())) {
					Map<String, String> respMap = new HashMap<String, String>();
					Element elChild = element.element("trade");
					List<Element> lstChildEle = elChild.elements();
					//得到全部子节点
                    for (int i = 0; i < lstChildEle.size(); i++) {
                        Element ele = (Element) lstChildEle.get(i);
                        respMap.put(ele.getName(), ele.getTextTrim());
                    }
                    resultMap.put("response", respMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
}
