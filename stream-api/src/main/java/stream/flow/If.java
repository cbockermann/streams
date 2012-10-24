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
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessorList;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.expressions.Expression;
import stream.expressions.ExpressionCompiler;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Flow")
public class If extends ProcessorList {

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
		return (condition == null || condition.matches(context, item));
	}

	/**
	 * @see stream.ProcessorList#process(stream.Data)
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
