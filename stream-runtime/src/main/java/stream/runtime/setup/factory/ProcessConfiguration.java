/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
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
