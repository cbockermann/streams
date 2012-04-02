/**
 * 
 */
package stream.data;

import stream.runtime.annotations.Parameter;
import stream.runtime.expressions.Expression;
import stream.runtime.expressions.ExpressionCompiler;

/**
 * @author chris
 * 
 */
public abstract class ConditionedDataProcessor extends AbstractDataProcessor {

	Expression condition;

	/**
	 * @return the condition
	 */
	public String getCondition() {
		if (condition == null)
			return "";

		return condition.toString();
	}

	/**
	 * @param condition
	 *            the condition to set
	 */
	@Parameter(name = "condition", required = false)
	public void setCondition(String condition) {
		try {
			if (condition == null || "".equals(condition.trim())) {
				condition = null;
				return;
			}

			this.condition = ExpressionCompiler.parse(condition);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public boolean matches(Data item) {
		return (condition == null || condition.matches(context, item));
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		if (matches(data))
			return processMatchingData(data);

		return data;
	}

	public abstract Data processMatchingData(Data data);
}