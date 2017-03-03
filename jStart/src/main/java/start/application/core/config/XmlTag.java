package start.application.core.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlTag {
	
	private String name;
	
	private Map<String,String> attributes=new HashMap<String,String>();
	
	private String textContent;
	
	private List<XmlTag> childTags=new ArrayList<XmlTag>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public List<XmlTag> getChildTags() {
		return childTags;
	}

	public void setChildTags(List<XmlTag> childTags) {
		this.childTags = childTags;
	}

}
