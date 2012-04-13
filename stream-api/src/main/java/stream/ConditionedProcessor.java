/**
 * 
 */
package stream;

import stream.annotations.Parameter;
import stream.data.Data;
import stream.expressions.Expression;
import stream.expressions.ExpressionCompiler;

/**
 * @author chris
 * 
 */
public abstract class ConditionedProcessor extends AbstractProcessor {

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
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		if (matches(data))
			return processMatchingData(data);

		return data;
	}

	public abstract Data processMatchingData(Data data);
}