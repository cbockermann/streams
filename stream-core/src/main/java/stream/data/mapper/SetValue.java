package stream.data.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stream.ConditionedProcessor;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.expressions.ExpressionResolver;

@Description(group = "Data Stream.Processing.Transformations.Data")
public class SetValue extends ConditionedProcessor {
	protected String key;
	protected String value;
	protected List<String> scope;

	public SetValue() {
		super();
		scope = new ArrayList<String>();
		scope.add("data");
	}

	/**
     * 
     */
	@Override
	public Data processMatchingData(Data data) {
		if (key != null && value != null) {
			String val = String.valueOf(ExpressionResolver.resolve(value,
					context, data));
			if (scope.contains("data"))
				data.put(key, val);
			if (scope.contains("process"))
				context.set(key, val);
		}

		return data;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the scope
	 */
	public String[] getScope() {
		return scope.toArray(new String[scope.size()]);
	}

	/**
	 * @param scope
	 *            scope=data and scope=process
	 */
	@Parameter(defaultValue = "data", required = false)
	public void setScope(String[] scope) {
		this.scope = Arrays.asList(scope);
	}

}