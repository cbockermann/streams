/**
 * 
 */
package com.rapidminer.beans.utils;

/**
 * This class is a wrapper for the different types of parameter annotations,
 * supported by RapidMiner Beans library. Each parameter annotation is mapped to
 * this objects of this class, parameter types are then created from these
 * objects instead of using the annotation classes directly.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 */
public class ParameterInfo extends stream.runtime.setup.ParameterInfo {

	public ParameterInfo(com.rapidminer.annotations.Parameter param) {
		super(param.name(), param.description(), param.required(), param
				.defaultValue(), param.min(), param.max(), param.values());
	}

	public ParameterInfo(stream.annotations.Parameter param) {
		super(param);
	}

	/**
	 * @return the name
	 */
	public String name() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String description() {
		return description;
	}

	/**
	 * @return the required
	 */
	public boolean required() {
		return required;
	}

	/**
	 * @return the defaultValue
	 */
	public String defaultValue() {
		return defaultValue;
	}

	/**
	 * @return the min
	 */
	public Double min() {
		return min;
	}

	/**
	 * @return the max
	 */
	public Double max() {
		return max;
	}

	/**
	 * @return the values
	 */
	public String[] values() {
		return values;
	}
}