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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	protected ContextResolver r;
	protected String key;
	protected String context;
	protected boolean statics;

	protected String DATA_START = "%{";
	protected String DATA_START_REGEXP = "%\\{";
	protected String DATA_END = "}";
	protected String DATA_END_REGEXP = "}";

	public AbstractExpression(String e) {
		statics = false;
		if (e == null) {
			return;
		}
		expression = e.trim();
		if (expression != null) {

			if (expression.startsWith(DATA_START)
					&& expression.endsWith(DATA_END)) {
				r = createContextExpression(expression);
				return;
			}
			if (expression.equalsIgnoreCase("null")) {
				r = new StaticNullExpressionResolver(expression);
				statics = true;
				return;
			}
			if (expression.equalsIgnoreCase("true")) {
				r = new StaticTrueExpressionResolver(expression);
				statics = true;
				return;
			}
			if (expression.equalsIgnoreCase("false")) {
				r = new StaticFalseExpressionResolver(expression);
				statics = true;
				return;
			}
			try {
				Double.parseDouble(expression);
				r = new StaticDoubleContextResolver(expression);
				statics = true;
				return;
			} catch (Exception exc) {
				if (expression.startsWith("'") && expression.endsWith("'"))
					expression = expression.substring(1, e.length() - 1);
				r = createStringExpression();
			}

		}
	}

	public T get(Context ctx) throws Exception {
		return get(ctx, null);
	}

	public T get(Data data) throws Exception {
		return get(null, data);
	}

	public boolean isStatic() {
		return statics;
	}

	private ContextResolver createStringExpression() {
		if (expression.contains(DATA_START)
				&& expression.contains(DATA_END)
				&& expression.indexOf(DATA_START) < expression
						.indexOf(DATA_END))
			return new StringBuilderContextResolver(expression);
		statics = true;
		return new StaticStringContextResolver(expression);
	}

	private ContextResolver createContextExpression(String expr) {
		expr = expr.substring(DATA_START.length(),
				expr.length() - DATA_END.length());

		// Correct Format
		if (expr.indexOf(".") >= 0) {
			String[] vals = expr.split("\\.", 2);
			// TODO select Context
			context = vals[0];
			key = vals[1];

			// // SetExpressions
			// if (key.startsWith("{")) {
			// // Extract Regexp
			// key = key.substring(1);
			// if (key.endsWith("+") || key.endsWith("*"))
			// key = key.substring(0, key.length() - 1);
			// if (key.endsWith("}"))
			// key = key.substring(0, key.length() - 1);
			// else {
			// throw new IllegalArgumentException("Bad Set Definition");
			// }
			// r = new SetExpressionResolver(key);
			// return;
			// }

			if (context.equals(Context.DATA_CONTEXT_NAME))
				return new DataExpressionResolver(key);
			if (context.equals(Context.PROCESS_CONTEXT_NAME))
				return new ObjectContextResolver(expr);
			if (context.equals(Context.CONTAINER_CONTEXT_NAME))
				return new ObjectContextResolver(expr);

		}
		return null;
	}

	public String getKey() {
		return key;
	}

	public String getContext() {
		return context;
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

	protected abstract class ContextResolver implements
			Expression<Serializable> {

		protected final String key;

		public ContextResolver(String key) {
			this.key = key;
		}

		public Serializable get(Context ctx) throws Exception {
			return get(ctx, null);
		}

		public Serializable get(Data data) throws Exception {
			return get(null, data);
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

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String getContext() {
			return null;
		}
	}

	private class StaticDoubleContextResolver extends ContextResolver {

		private final Double d;

		public StaticDoubleContextResolver(String key) {
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

	public class StringBuilderContextResolver extends ContextResolver {

		// private final String prefix;
		// private final String suffix;
		// private ExpressionResolver r;
		private StringBuilder sb;
		int[] starts;
		int[] ends;
		String[] nonExps;
		List<ContextResolver> ers;

		public StringBuilderContextResolver(String key) {
			super(key);
			String[] mids = this.key.split(DATA_START_REGEXP);

			nonExps = new String[mids.length + 1];
			ers = new ArrayList<AbstractExpression<T>.ContextResolver>();

			for (int i = 0; i < mids.length; i++) {
				String s = mids[i];
				// FIRST NONEXP
				if (s.isEmpty())
					continue;

				String[] r = s.split(DATA_END_REGEXP);
				// END OR TO EXPS WITHOUT TEXT
				if (r.length == 1) {
					if (s.contains(DATA_END)) {
						ers.add(createContextExpression(DATA_START + r[0]
								+ DATA_END));
						nonExps[i] = null;
						continue;
					}
					nonExps[i] = r[0];
					continue;
				}
				if (r.length == 2) {
					ers.add(createContextExpression(DATA_START + r[0]
							+ DATA_END));
					nonExps[i] = r[1];
					continue;
				}
			}

			// int i1 = key.indexOf(DATA_START);
			// int i2 = key.indexOf(DATA_END);
			// prefix = key.substring(0, i1);
			// if (i2 + 1 < key.length())
			// suffix = key.substring(i2 + 1, key.length());
			// else
			// suffix = "";
			// r = createContextExpression(key.substring(i1, i2 + 1));
		}

		@Override
		public Serializable get(Context ctx, Data item) throws Exception {
			sb = new StringBuilder();
			int i = 0;
			for (ContextResolver er : ers) {
				String s = nonExps[i];
				if (s != null)
					sb.append(s);
				sb.append(er.get(ctx, item));
				i++;
			}
			String s = nonExps[i];
			if (s != null)
				sb.append(s);

			// sb.append(prefix);
			// sb.append(r.get(ctx, item));
			// sb.append(suffix);
			return sb.toString();
		}
	}

	public class StaticStringContextResolver extends ContextResolver {

		public StaticStringContextResolver(String key) {
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

	public class StaticNullExpressionResolver extends ContextResolver {

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

	public class StaticTrueExpressionResolver extends ContextResolver {

		public StaticTrueExpressionResolver(String key) {
			super(key);
		}

		@Override
		public Serializable get(Context ctx, Data item) throws Exception {
			return true;
		}

		@Override
		public String toString() {
			return "StaticTrueExpressionResolver [key=" + key + "]";
		}

	}

	public class StaticFalseExpressionResolver extends ContextResolver {

		public StaticFalseExpressionResolver(String key) {
			super(key);
		}

		@Override
		public Serializable get(Context ctx, Data item) throws Exception {
			return false;
		}

		@Override
		public String toString() {
			return "StaticFalseExpressionResolver [key=" + key + "]";
		}

	}

	public class ObjectContextResolver extends ContextResolver {

		public ObjectContextResolver(String key) {
			super(key);
		}

		@Override
		public Serializable get(Context ctx, Data item) throws Exception {
			Object o = ctx.resolve(key);
			if (o != null && o instanceof Serializable)
				return (Serializable) o;
			return null;
		}

		@Override
		public String toString() {
			return "ContextExpressionResolver [key=" + key + "]";
		}
	}

	public class DataExpressionResolver extends ContextResolver {

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

	public class SetExpressionResolver extends ContextResolver {

		private DataRegExpIterator iter;

		public SetExpressionResolver(String regexp) {
			super(regexp);
			iter = new DataRegExpIterator(key);
		}

		@Override
		public Serializable get(Context ctx, Data item) throws Exception {
			// Bad for parallel?
			// TODO setContext(item)
			iter.setData(item);
			return iter;
		}

		@Override
		public String toString() {
			return "DataExpressionResolver [key=" + key + "]";
		}

	}

	public class DataRegExpIterator implements Iterator<String>, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private Iterator<String> keysIterator;
		private Pattern pattern;
		private String key;
		private boolean calc;

		public DataRegExpIterator(String regexp) {
			pattern = Pattern.compile(regexp);
			calc = false;
		}

		private void setData(Data data) {
			if (data != null)
				keysIterator = data.keySet().iterator();
		}

		@Override
		public boolean hasNext() {

			// Auf Key gucken?
			if (calc)
				return true;
			key = calcNextKey();
			calc = (key == null) ? false : true;
			return calc;
		}

		@Override
		public String next() {
			if (calc)
				return key;
			key = calcNextKey();
			calc = (key == null) ? false : true;
			return key;

		}

		private String calcNextKey() {
			for (Iterator<String> iter = keysIterator; iter.hasNext();) {
				String tempKey = iter.next();
				Matcher m = pattern.matcher(tempKey);
				if (m.matches())
					return tempKey;
			}
			return null;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove not possible");
		}

	}
}
