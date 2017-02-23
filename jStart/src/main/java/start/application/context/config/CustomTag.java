package start.application.context.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomTag {
	
	private String name;
	
	private Map<String,String> attributes=new HashMap<String,String>();
	
	private String textContent;
	
	private List<CustomTag> childTags=new ArrayList<CustomTag>();

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

	public List<CustomTag> getChildTags() {
		return childTags;
	}

	public void setChildTags(List<CustomTag> childTags) {
		this.childTags = childTags;
	}

}
