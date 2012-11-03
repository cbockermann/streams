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
package stream.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ConditionedProcessor;
import stream.Context;
import stream.Data;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.expressions.ExpressionResolver;

/**
 * <p>
 * This class implements a processor to set a value within a data item.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Description(group = "Data Stream.Processing.Transformations.Data")
public class SetValue extends ConditionedProcessor {

	static Logger log = LoggerFactory.getLogger(SetValue.class);

	protected String key;
	protected String value;
	protected List<String> scope;

	public SetValue() {
		super();
		scope = new ArrayList<String>();
	}

	/**
     * 
     */
	@Override
	public Data processMatchingData(Data data) {
		if (key != null && value != null) {
			String val = "";
			if (value == "null") {
				if (scope.contains(Context.DATA_CONTEXT_NAME)
						|| scope.isEmpty())
					data.remove(key);
				else if (scope.contains(Context.PROCESS_CONTEXT_NAME))
					context.set(key, null);
			} else
				val = String.valueOf(ExpressionResolver.resolve(value, context,
						data));

			if (scope.contains(Context.DATA_CONTEXT_NAME) || scope.isEmpty())
				data.put(key, val);
			else if (scope.contains(Context.PROCESS_CONTEXT_NAME))
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
	@Parameter(description = "The key/name of the variable to set.")
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
	@Parameter(description = "The value to set. This can also be a runtime expression, which will be evaluated at processing time. If this value is not set, the processor has no effect.", required = true)
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
	@Parameter(defaultValue = Context.DATA_CONTEXT_NAME, description = "The scope determines where the variable will be set. Valid scopes are `process`, `data`. The default scope is `data`.", required = false)
	public void setScope(String[] scope) {
		this.scope = Arrays.asList(scope);
	}

}