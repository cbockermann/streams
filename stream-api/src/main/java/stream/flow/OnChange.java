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
import stream.ProcessContext;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.expressions.ExpressionResolver;
import stream.expressions.version2.StringExpression;
import stream.service.Service;

/**
 * <p>
 * This class provides a processorList that will be processed under certain
 * conditions.
 * </p>
 * 
 * @author Hendrik Blom &lt;hendrik.blom@udo.edu&gt;
 * 
 */
@Description(group = "Data Stream.Flow")
public class OnChange extends If implements Service {

	static Logger log = LoggerFactory.getLogger(OnChange.class);

	private String key;
	private String oldValue;

	private String from;
	private String to;
	private StringExpression expression;

	public OnChange() {
		try {
			reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getFrom() {
		return from;
	}

	@Parameter(required = false, defaultValue = "")
	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	@Parameter(required = false, defaultValue = "")
	public void setTo(String to) {
		this.to = to;
	}

	@Parameter(required = true, defaultValue = "")
	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	@Override
	public void init(ProcessContext context) throws Exception {
		super.init(context);
		if (key != null)
			expression = new StringExpression(key);
	}

	/**
	 * * There are 4 use cases:
	 * <p>
	 * 1. "from" and "to" are not set: Any change from the last item to the new
	 * item will be detected and the processorList will be processed.
	 * </p>
	 * <p>
	 * 2. "from" is not set but "to" is set: if the new value equals to the "to"
	 * value and it is different to the last value, then the processorList will
	 * be processed.
	 * </p>
	 * <p>
	 * 3. "from" is set but "to" is not set: if the last value equals to the
	 * "from" value and the new value is different to the from value, then the
	 * processorList will be processed.
	 * </p>
	 * <p>
	 * 4. "from" and "to" is set: if the last value equals "from" and the new
	 * value equals to "to", then the processorList will be processed.
	 * </p>
	 **/
	public boolean matches(Data item) {
		if (condition != null) {
			try {
				if (!condition.get(context, item))
					return false;
			} catch (Exception e1) {
				e1.printStackTrace();
				return false;
			}
		}
		
		String value = null;
		try {
			value = expression.get(context, item);
			if(value==null)
				value="null";
		} catch (Exception e) {
			e.printStackTrace();
		}

		boolean result = false;
		// UseCase 1

		if (from == null && to == null) {
			if (oldValue == null && (value == null || value.equals("null")))
				return false;
			else if (oldValue == null && value != null) {
				oldValue = value;
				return true;
			}
			result = !oldValue.equals(value);
			oldValue = value;
			return result;
			// UseCase 1
		} else if (from == null && to != null) {
			if (oldValue == null && (value == null || to.equals("null"))) {
				oldValue = value;
				return false;
			} else if (oldValue == null && value != null) {
				oldValue = value;
				return to.equals(value);
			}
			result = !oldValue.equals(value) && to.equals(value);
			oldValue = value;
			return result;
		} else if (from != null && to == null) {
			if (from.equals("null") && oldValue == null && value != null
					&& !"null".equals(value)) {
				oldValue = value;
				return true;
			}
			if (oldValue == null) {
				oldValue = value;
				return false;
			}
			if (from.equals("!null")) {
				if (oldValue != null && !oldValue.equals(value)) {
					oldValue = value;
					return true;
				}
				if (oldValue == null)
					return false;
			}
			if (from.equals(oldValue) && (value == null || !from.equals(value))) {
				oldValue = value;
				return true;
			}

		} else {
			if (from.equals(to))
				return false;
			if (from.equals("null") && oldValue == null && to.equals(value)) {
				oldValue = value;
				return true;
			}
			if (from.equals(oldValue) && to.equals(value)) {
				oldValue = value;
				return true;
			}

		}
		oldValue = value;
		return false;
	}

	@Override
	public void reset() throws Exception {
		oldValue = null;
	}
}