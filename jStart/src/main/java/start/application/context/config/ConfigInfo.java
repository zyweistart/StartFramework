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
import start.application.core.exceptions.ApplicationException;
import start.application.core.utils.ClassLoaderUtils;
/**
 * 全局配置
 * @author Start
 */
public final class ConfigInfo {
	
	private DocumentBuilder builder;
	
	private Map<String,String> constants;
	private Map<String, BeanInfo> beans;
	private List<String> interceptors;
	
	public ConfigInfo(){
		constants=new HashMap<String,String>();
		interceptors=new ArrayList<String>();
		beans = new HashMap<String, BeanInfo>();
		
		DocumentBuilderFactory factory=null;
		try {
			factory=DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new ApplicationException(e);
		}
	}
	
	/**
	 * 解析框架默认的配置文件
	 */
	public void loadDefaultConfigFile() {
		readXml("META-INF/StartConfig.xml");
	}
	
	/**
	 * @param classpath	类路径下的文件
	 */
	public void readXml(String classpath){
		try{
			readXml(builder.parse(ClassLoaderUtils.getResourceAsStream(classpath,ConfigInfo.class)));
		} catch (Exception e) {
			throw new ConfigError("导入文件:"+classpath+"打开失败。错误信息："+e.getMessage());
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
				if("import".equalsIgnoreCase(node.getNodeName())){
					//加载自定义配置文件
					NamedNodeMap beanAttributes=node.getAttributes();
					for(int k=0;k<beanAttributes.getLength();k++){
						Node nodeAtt=beanAttributes.item(k);
						if("path".equalsIgnoreCase(nodeAtt.getNodeName())){
							String path=nodeAtt.getNodeValue();
							configFiles.add(path);
						}else if("config".equalsIgnoreCase(nodeAtt.getNodeName())){
							String path=nodeAtt.getNodeValue();
							resourceFiles.add(path);
						}
					}
				}else if("constants".equalsIgnoreCase(node.getNodeName())){
					readConstant(node);
				}else if("beans".equalsIgnoreCase(node.getNodeName())){
					readBeans(node);
				}else if("interceptors".equalsIgnoreCase(node.getNodeName())){
					readInterceptor(node);
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
			constants.put(key, value);
		}
	}
	
	/**
	 * 常量配置
	 */
	private void readConstant(Node node){
		NodeList childNodes=node.getChildNodes();
		for(int i=0;i<childNodes.getLength();i++){
			Node childNode=childNodes.item(i);
			if("constant".equalsIgnoreCase(childNode.getNodeName())){
				String key=null,value=null;
				NamedNodeMap nodeAtts=childNode.getAttributes();
				for(int j=0;j<nodeAtts.getLength();j++){
					Node nodeAtt=nodeAtts.item(j);
					if("name".equalsIgnoreCase(nodeAtt.getNodeName())){
						key=nodeAtt.getNodeValue();
					}else if("value".equalsIgnoreCase(nodeAtt.getNodeName())){
						value=nodeAtt.getNodeValue();
					}
				}
				if(value==null){
					value=childNode.getTextContent();
				}
				constants.put(key, value);
			}
		}
	}
	
	/**
	 * 读取Bean信息
	 */
	private void readBeans(Node node){
		NodeList childNodes=node.getChildNodes();
		for(int k=0;k<childNodes.getLength();k++){
			Node childNode=childNodes.item(k);
			if("bean".equalsIgnoreCase(childNode.getNodeName())){
				this.readBean(childNode);
			}
		}
	}
	
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
		beans.put(bean.getName(), bean);
	}
	
	/**
	 * 拦截器配置
	 */
	private void readInterceptor(Node node){
		NodeList childNodes=node.getChildNodes();
		for(int i=0;i<childNodes.getLength();i++){
			Node childNode=childNodes.item(i);
			if("interceptor".equalsIgnoreCase(childNode.getNodeName())){
				NamedNodeMap nodeAtts=childNode.getAttributes();
				for(int j=0;j<nodeAtts.getLength();j++){
					Node nodeAtt=nodeAtts.item(j);
					if("ref".equalsIgnoreCase(nodeAtt.getNodeName())){
						//加载时不进行初始化操作,只有在调用newInstance()方法才进行初始化操作
						interceptors.add(nodeAtt.getNodeValue());
					}
				}
			}
		}
	}

	public Map<String, String> getConstants() {
		return constants;
	}

	public List<String> getInterceptors() {
		return interceptors;
	}

	public Map<String, BeanInfo> getBeans() {
		return beans;
	}
	
}
