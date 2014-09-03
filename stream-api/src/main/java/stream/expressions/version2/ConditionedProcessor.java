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
package stream.expressions.version2;

import stream.AbstractProcessor;
import stream.Data;
import stream.annotations.Parameter;

/**
 * <p>
 * This class is a processor that will process items only, if a given condition
 * matches. The condition can be an expression in the streams expression
 * language.
 * </p>
 * 
 * @author Hendrik Blom, Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public abstract class ConditionedProcessor extends AbstractProcessor {

	/** The expression to check before processing an event */
	protected Condition condition;
	
	
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
			ConditionFactory cf = new ConditionFactory();
			this.condition = cf.create(condition);
			if (condition.isEmpty())
				this.condition = null;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public boolean matches(Data item) throws Exception {
		if(condition==null)
			return true;
		final Boolean b=  condition.get(context, item);
		if(b == null)
			return true;
		return b;
	}

	/**
	 * @see stream.DataProcessor#process(stream.Data)
	 */
	@Override
	public Data process(Data data) {
		try {
			if (matches(data))
				return processMatchingData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	public abstract Data processMatchingData(Data data) throws Exception;
}