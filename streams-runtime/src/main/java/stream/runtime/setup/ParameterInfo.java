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
package stream.runtime.setup;

/**
 * This class is a wrapper for the different types of parameter annotations,
 * supported by RapidMiner Beans library. Each parameter annotation is mapped to
 * this objects of this class, parameter types are then created from these
 * objects instead of using the annotation classes directly.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 */
public class ParameterInfo {

	protected final String name;
	protected final String description;
	protected final String[] values;
	protected final boolean required;
	protected final String defaultValue;
	protected final Double min;
	protected final Double max;

	public ParameterInfo(stream.annotations.Parameter param) {
		name = param.name();
		description = param.description();
		required = param.required();
		defaultValue = param.defaultValue();
		min = param.min();
		max = param.max();
		values = param.values();
	}

	public ParameterInfo(String name, String description, boolean required,
			String defaultValue, Double min, Double max, String[] values) {
		this.name = name;
		this.description = description;
		this.required = required;
		this.defaultValue = defaultValue;
		this.min = min;
		this.max = max;
		this.values = values;
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