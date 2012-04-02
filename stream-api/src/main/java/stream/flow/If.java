/**
 * 
 */
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessorList;
import stream.data.filter.Expression;
import stream.data.filter.ExpressionCompiler;
import stream.runtime.annotations.Parameter;

/**
 * @author chris
 * 
 */
public class If extends DataProcessorList {

	static Logger log = LoggerFactory.getLogger(If.class);
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
		return (condition == null || condition.matches(item));
	}

	/**
	 * @see stream.data.DataProcessorList#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		if (matches(input)) {
			log.debug("processing item {}", input);
			return super.process(input);
		} else {
			log.debug("skipping item {}", input);
			return input;
		}
	}
}
