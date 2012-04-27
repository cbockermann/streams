/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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
package stream;

import stream.annotations.Parameter;
import stream.data.Data;
import stream.expressions.Expression;
import stream.expressions.ExpressionCompiler;

/**
 * <p>
 * This class is a processor that will process items only, if a given condition
 * matches. The condition can be an expression in the streams expression
 * language.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public abstract class ConditionedProcessor extends AbstractProcessor {

	/** The expression to check before processing an event */
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
	@Parameter(name = "condition", required = false, description = "The condition parameter allows to specify a boolean expression that is matched against each item. The processor only processes items matching that expression.")
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