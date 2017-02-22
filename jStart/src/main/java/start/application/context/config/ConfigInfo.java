package start.application.context.config;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import start.application.context.exceptions.ConfigError;
import start.application.core.beans.BeanInfo;
import start.application.core.utils.ClassLoaderUtils;
/**
 * 全局配置
 * @author Start
 */
public final class ConfigInfo {
	
	public static final String IMPORT="import";
	public static final String CONSTANT="constant";
	public static final String BEAN="bean";
	
	private DocumentBuilder builder;
	private ConfigImpl impl;
	
	public ConfigInfo(ConfigImpl impl){
		this.impl=impl;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new ConfigError(e);
		}
	}
	
	/**
	 * 解析框架默认的配置文件
	 */
	public void loadConfigFile() {
		readXml("META-INF/StartConfig.xml");
		impl.finish();
	}
	
	/**
	 * @param classpath	类路径下的文件
	 */
	public void readXml(String classpath){
		try{
			readXml(builder.parse(ClassLoaderUtils.getResourceAsStream(classpath,getClass())));
		} catch (Exception e) {
			throw new ConfigError(classpath+"打开失败， 请检查XML配置文件，错误信息："+e.getMessage());
		}
	}
	
	private void readXml(Document document) throws Exception{
		NodeList  childNodeList =document.getChildNodes();
		List<String> configFiles=new ArrayList<String>();
		List<String> resourceFiles=new ArrayList<String>();
		for(int i=0;i<childNodeList.getLength();i++){
			Node childNode=childNodeList.item(i);
			NodeList nodes=childNode.getChildNodes();
			for(int j=0;j<nodes.getLength();j++){
				Node node=nodes.item(j);
				if(node.getNodeType()==1){
					if(IMPORT.equalsIgnoreCase(node.getNodeName())){
						//加载自定义配置文件
						NamedNodeMap beanAttributes=node.getAttributes();
						for(int k=0;k<beanAttributes.getLength();k++){
							Node nodeAtt=beanAttributes.item(k);
							if("path".equalsIgnoreCase(nodeAtt.getNodeName())){
								//读取配置文件
								String path=nodeAtt.getNodeValue();
								configFiles.add(path);
							}else if("config".equalsIgnoreCase(nodeAtt.getNodeName())){
								//读取属性文件
								String path=nodeAtt.getNodeValue();
								resourceFiles.add(path);
							}
						}
					}else if(CONSTANT.equalsIgnoreCase(node.getNodeName())){
						readConstant(node);
					}else if(BEAN.equalsIgnoreCase(node.getNodeName())){
						readBean(node);
					}else{
						readCustom(node);
					}
				}
			}
		}
		//加载导入的配置文件
		for(String path:configFiles){
			readXml(path);
		}
		//加载导入的资源文件
		for(String path:resourceFiles){
			readResourceBundle(path);
		}
	}
	
	
	/**
	 * 读取资源文件
	 */
	private void readResourceBundle(String path){
		ResourceBundle bundle=ResourceBundle.getBundle(path);
		Enumeration<String> keys=bundle.getKeys();
		while(keys.hasMoreElements()){
			String key=keys.nextElement();
			String value=bundle.getString(key);
			Map<String,String> attributes=new HashMap<String,String>();
			attributes.put(key, value);
			impl.read(CONSTANT, attributes, null);
		}
	}
	
	/**
	 * 常量配置
	 */
	private void readConstant(Node node){
		String key=null,value=null;
		NamedNodeMap nodeAtts=node.getAttributes();
		for(int j=0;j<nodeAtts.getLength();j++){
			Node nodeAtt=nodeAtts.item(j);
			if("name".equalsIgnoreCase(nodeAtt.getNodeName())){
				key=nodeAtt.getNodeValue();
			}else if("value".equalsIgnoreCase(nodeAtt.getNodeName())){
				value=nodeAtt.getNodeValue();
			}
		}
		if(value==null){
			value=node.getTextContent();
		}
		Map<String,String> attributes=new HashMap<String,String>();
		attributes.put(key, value);
		impl.read(node.getNodeName().toLowerCase(), attributes, null);
	}
	
	/**
	 * 读取Bean信息
	 */
	private void readBean(Node node){
		NamedNodeMap beanAttributes=node.getAttributes();
		BeanInfo bean=new BeanInfo();
		for(int i=0;i<beanAttributes.getLength();i++){
			Node beanObjAttributes=beanAttributes.item(i);
			String key=beanObjAttributes.getNodeName();
			String value=beanAttributes.getNamedItem(key).getNodeValue();
			bean.getAttributes().put(key, value);
		}
		NodeList propertyNodes=node.getChildNodes();
		for(int k=0;k<propertyNodes.getLength();k++){
			Node childNode=propertyNodes.item(k);
			if("property".equalsIgnoreCase(childNode.getNodeName())){
				NamedNodeMap propertyAttributes=childNode.getAttributes();
				if(propertyAttributes!=null){
					String name=null;
					String value=null;
					for(int l=0;l<propertyAttributes.getLength();l++){
						Node propertyNodeAttributes=propertyAttributes.item(l);
						name=propertyAttributes.getNamedItem("name").getNodeValue();
						if("value".equalsIgnoreCase(propertyNodeAttributes.getNodeName())){
							value=propertyAttributes.getNamedItem("value").getNodeValue();
							bean.getValues().put(name, value);
						}else if("ref".equalsIgnoreCase(propertyNodeAttributes.getNodeName())){
							value=propertyAttributes.getNamedItem("ref").getNodeValue();
							bean.getRefs().put(name, value);
						}
					}
					if(value==null){
						value=childNode.getTextContent();
						bean.getValues().put(name, value);
					}
				}
			}
		}
		impl.readBean(bean);
	}
	
	/**
	 * 自定义标签
	 */
	private void readCustom(Node node){
		Map<String,String> vals=new HashMap<String,String>();
		NamedNodeMap nodeAtts=node.getAttributes();
		for(int j=0;j<nodeAtts.getLength();j++){
			Node nodeAtt=nodeAtts.item(j);
			if(node.getNodeType()==1){
				vals.put(nodeAtt.getNodeName(),nodeAtt.getNodeValue());
			}
		}
		impl.read(node.getNodeName().toLowerCase(), vals, null);
	}
	
}
