/**
 * 
 */
package com.rapidminer.beans.utils;

import com.rapidminer.annotations.Parameter;

/**
 * This class is a wrapper for the different types of parameter annotations,
 * supported by RapidMiner Beans library. Each parameter annotation is mapped to
 * this objects of this class, parameter types are then created from these
 * objects instead of using the annotation classes directly.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 */
public class ParameterInfo {

	final String name;
	final String description;
	final String[] values;
	final boolean required;
	final String defaultValue;
	final Double min;
	final Double max;

	public ParameterInfo(stream.annotations.Parameter param) {
		name = param.name();
		description = param.description();
		required = param.required();
		defaultValue = param.defaultValue();
		min = param.min();
		max = param.max();
		values = param.values();
	}

	public ParameterInfo(Parameter param) {
		name = param.name();
		description = param.description();
		required = param.required();
		defaultValue = param.defaultValue();
		min = param.min();
		max = param.max();
		values = param.values();
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