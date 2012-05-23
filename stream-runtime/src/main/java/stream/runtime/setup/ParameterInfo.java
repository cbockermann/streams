/**
 * 
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