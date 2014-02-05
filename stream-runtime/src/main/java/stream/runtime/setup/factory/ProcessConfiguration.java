package stream.runtime.setup.factory;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import stream.util.Variables;

public class ProcessConfiguration implements Cloneable{

	private String id;
	private String copyId;
	
	private String processClass;
	private Map<String, String> attributes;
	private String input;
	private String output;
	private Variables variables;
	private Element element;

	public ProcessConfiguration() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		variables.put("process.id", id);
		
	}
	
	public String getCopyId() {
		return copyId;
	}

	public void setCopyId(String copyId) {
		this.copyId = copyId;
		variables.put("copy.id", copyId);
	}

	public String getProcessClass() {
		return processClass;
	}

	public void setProcessClass(String processClass) {
		this.processClass = processClass;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Variables getVariables() {
		return variables;
	}

	public void setVariables(Variables variables) {
		this.variables = variables;
	}
	
	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = (Element) element.cloneNode(true);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		ProcessConfiguration c = new ProcessConfiguration();
		c.setAttributes(new HashMap<String,String>(this.getAttributes()));
		c.setId(new String(this.getId()));
		c.setInput(new String(this.getInput()));
		c.setOutput(new String(this.getOutput()));
		c.setProcessClass(new String(this.getProcessClass()));
		c.setVariables(new Variables(this.getVariables()));
		c.setCopyId(new String(this.getCopyId()));
		c.setElement(this.element);
		
		return c;
		
	}
	
	

}
