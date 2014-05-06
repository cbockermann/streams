package stream.runtime.setup.factory;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import stream.CopiesUtils;
import stream.Copy;
import stream.util.Variables;

public class ProcessConfiguration implements Cloneable {

	private String id;
	private Copy copy;
	private String processType;

	private String processClass;
	private Map<String, String> attributes;
	private String input;
	private String output;
	private Variables variables;
	private Element element;

	public ProcessConfiguration() {
		processType = "process";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		if (variables != null)
			variables.put(processType + ".id", id);

	}

	public Copy getCopy() {
		return copy;
	}

	public void setCopy(Copy copy) {
		this.copy = copy;
		if (variables != null)
			CopiesUtils.addCopyIds(variables, copy);
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
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
		this.variables = new Variables(variables);
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = (Element) element.cloneNode(true);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ProcessConfiguration c = new ProcessConfiguration();
		c.setAttributes(new HashMap<String, String>(this.getAttributes()));

		c.setId(this.getId());
		c.setInput(this.getInput());
		c.setOutput(this.getOutput());
		c.setProcessClass(this.getProcessClass());
		c.setVariables(this.getVariables());
		c.setCopy(this.getCopy());
		c.setElement(this.element);

		return c;

	}

}
