package start.application.context.config;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import start.application.context.exceptions.ConfigError;
import start.application.core.utils.ClassLoaderUtils;
/**
 * 全局配置
 * @author Start
 */
public final class ConfigInfo {
	
	public static final String IMPORT="import";
	public static final String CONSTANT="constant";
	public static final String PROPERTIES="properties";
	public static final String BEAN="bean";
	public static final String PATH="path";
	public static final String CONFIG="config";
	
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
		loadConfigFile("META-INF/StartConfig.xml");
	}
	
	/**
	 * 解析框架默认的配置文件
	 */
	public void loadConfigFile(String xmlfile) {
		readXml(xmlfile);
		impl.finish();
	}
	
	/**
	 * @param classpath	类路径下的文件
	 */
	private void readXml(String classpath){
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
							if(PATH.equalsIgnoreCase(nodeAtt.getNodeName())){
								//读取配置文件
								String path=nodeAtt.getNodeValue();
								configFiles.add(path);
							}else if(CONFIG.equalsIgnoreCase(nodeAtt.getNodeName())){
								//读取属性文件
								String path=nodeAtt.getNodeValue();
								resourceFiles.add(path);
							}
						}
					}else{
						impl.read(read(node));
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
		XmlTag custom=new XmlTag();
		custom.setName(PROPERTIES);
		ResourceBundle bundle=ResourceBundle.getBundle(path);
		Enumeration<String> keys=bundle.getKeys();
		while(keys.hasMoreElements()){
			String key=keys.nextElement();
			String value=bundle.getString(key);
			custom.getAttributes().put(key, value);
		}
		impl.read(custom);
	}
	
	/**
	 * 读取配置信息
	 */
	private XmlTag read(Node node){
		XmlTag custom=new XmlTag();
		//名称
		custom.setName(node.getNodeName());
		//读取属性
		NamedNodeMap nodeAtts=node.getAttributes();
		if(nodeAtts!=null){
			for(int j=0;j<nodeAtts.getLength();j++){
				Node nodeAtt=nodeAtts.item(j);
				custom.getAttributes().put(nodeAtt.getNodeName(),nodeAtt.getNodeValue());
			}
		}
		//文本内容
		custom.setTextContent(node.getTextContent());
		//读取子
		NodeList childNodes=node.getChildNodes();
		for(int j=0;j<childNodes.getLength();j++){
			Node childNode=childNodes.item(j);
			if(childNode.getNodeType()==1){
				custom.getChildTags().add(read(childNode));
			}
		}
		return custom;
	}
	
}
