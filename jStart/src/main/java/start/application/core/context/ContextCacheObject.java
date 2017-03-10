package start.application.core.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import start.application.core.config.XmlTag;

public class ContextCacheObject {
	
	private static Map<String,String> constants=new HashMap<String,String>();
	private static Map<String,List<XmlTag>> xmlTags=new HashMap<String,List<XmlTag>>();
	
	/**
	 * 注册全局常量值
	 * @param key
	 * @param value
	 */
	public static void registerConstant(String key,String value){
		constants.put(key, value);
	}
	
	/**
	 * 获取常量
	 */
	public static String getConstant(String key) {
		String value=constants.get(key);
		if(value==null){
			throw new NullPointerException("不存在:"+key+"对应的常量值");
		}
		return value;
	}
	
	/**
	 * 注册自定义标签
	 * @param tagName
	 * @param values
	 */
	public static void registerCustom(String name,XmlTag xmlTag){
		List<XmlTag> tagValues=xmlTags.get(name);
		if(tagValues==null){
			tagValues=new ArrayList<XmlTag>();
		}
		tagValues.add(xmlTag);
		xmlTags.put(name, tagValues);
	}
	
	/**
	 * 获取自定义标签数据
	 * @param tagName
	 * @return
	 */
	public static List<XmlTag> getCustom(String tagName){
		List<XmlTag>values=xmlTags.get(tagName);
		if(values==null){
			throw new NullPointerException("未定义"+tagName+"对应的数据");
		}
		return values;
	}
	
}
