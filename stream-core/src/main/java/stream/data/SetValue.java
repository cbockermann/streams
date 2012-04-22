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