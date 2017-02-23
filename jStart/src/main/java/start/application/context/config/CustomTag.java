package start.application.context.config;

import java.util.Map;

public class CustomTag {
	
	private String name;
	
	private Map<String,String> attributes;
	
	private CustomTag childTag;

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

	public CustomTag getChildTag() {
		return childTag;
	}

	public void setChildTag(CustomTag childTag) {
		this.childTag = childTag;
	}
	
}
