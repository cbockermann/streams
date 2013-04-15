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
package stream.expressions.version2;

import java.io.Serializable;

import stream.Context;
import stream.Data;

/**
 * @author Hendrik Blom
 * 
 * @param <T>
 */
public abstract class AbstractExpression<T extends Serializable> implements
		Expression<T> {

	protected String expression;
	protected ExpressionResolver r;
	protected String key;

	protected String DATA_START = "%{";
	protected String DATA_END = "}";

	public AbstractExpression(String e) {
		if (e == null)
			return;
		expression = e.trim();
		if (expression != null) {

			if (expression.startsWith(DATA_START)
					&& expression.endsWith(DATA_END)) {

				expression = expression.substring(DATA_START.length(),
						expression.length() - 1);
				if (expression.indexOf(".") >= 0) {
					key = expression.split("\\.", 2)[1];
					r = new DataExpressionResolver(key);
				}
			} else {
				if (expression.equals("null"))
					r = new StaticNullExpressionResolver(expression);
				else
					try {
						Double.parseDouble(expression);
						r = new StaticDoubleExpressionResolver(expression);
					} catch (Exception exc) {
						r = new StaticStringExpressionResolver(expression);
					}

			}
		}

	}

	@Override
	public Expression<Serializable> toSerializableExpression() {
		return r;
	}

	@Override
	public String getExpression() {
		return expression;
	}

	@Override
	public String toString() {
		return expression;
	}

	protected abstract class ExpressionResolver implements
			Expression<Serializable> {

		protected final String key;

		public ExpressionResolver(String key) {
			this.key = key;
		}

		public Class<Serializable> type() {
			return Serializable.class;
		}

		@Override
		public Expression<Serializable> toSerializableExpression() {
			return this;
		}

		@Override
		public String getExpression() {
			return key;
		}
	}

	private class StaticDoubleExpressionResolver extends ExpressionResolver {

		private final Double d;

		public StaticDoubleExpressionResolver(String key) {
			super(key);
			d = new Double(key);
		}

		@Override
		public Serializable get(Context ctx, Data item) throws Exception {
			return d;
		}

		@Override
		public String toString() {
			return "StaticDoubleExpressionResolver [key=" + key + "]";
		}
	}

	public class StaticStringExpressionResolver extends ExpressionResolver {

		public StaticStringExpressionResolver(String key) {
			super(key);
		}

		@Override
		public Serializable get(Context ctx, Data item) throws Exception {
			return key;
		}

		@Override
		public String toString() {
			return "StaticStringExpressionResolver [key=" + key + "]";
		}
	}

	public class StaticNullExpressionResolver extends ExpressionResolver {

		public StaticNullExpressionResolver(String key) {
			super(key);
		}

		@Override
		public Serializable get(Context ctx, Data item) throws Exception {
			return null;
		}

		@Override
		public String toString() {
			return "StaticNullExpressionResolver [key=" + key + "]";
		}

	}

	public class ContextExpressionResolver extends ExpressionResolver {

		public ContextExpressionResolver(String key) {
			super(key);
		}

		@Override
		public Serializable get(Context ctx, Data item) throws Exception {
			return "error";
		}

		@Override
		public String toString() {
			return "ContextExpressionResolver [key=" + key + "]";
		}
	}

	public class DataExpressionResolver extends ExpressionResolver {

		public DataExpressionResolver(String key) {
			super(key);
		}

		@Override
		public Serializable get(Context ctx, Data item) throws Exception {
			return item.get(key);
		}

		@Override
		public String toString() {
			return "DataExpressionResolver [key=" + key + "]";
		}

	}

}
