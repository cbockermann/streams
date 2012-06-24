/**
 * 
 */
package stream.node.shell.client;

import java.io.Serializable;
import java.util.Map;

/**
 * @author chris
 * 
 */
public class ElementDescription implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = 324636247911895370L;
	String type;
	String className;
	Map<String, Class<?>> parameters;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the parameters
	 */
	public Map<String, Class<?>> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(Map<String, Class<?>> parameters) {
		this.parameters = parameters;
	}
}
