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
package stream.expressions;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import stream.util.WildcardPattern;

/**
 * <p>
 * An enumeration of available operators.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public abstract class Operator implements Serializable {
	/** The unique class ID */
	private static final long serialVersionUID = 5150175070404610787L;

	public final static Operator EQ = new BinaryOperator("@eq", "=", "==") {
		/** The unique class ID */
		private static final long serialVersionUID = -7185932909087120854L;

		public boolean eval(Object left, Object right) {
			if (left == null && right == null)
				return true;
			if (left == null && right != null)
				if (right.toString().equals("null"))
					return true;
				else
					return false;
			if (left != null && right == null)
				if (left.toString().equals("null"))
					return true;
				else
					return false;
			if (isNumeric(left) && isNumeric(right)) {
				try {
					Double v = new Double(left.toString());
					Double w = new Double(right.toString());
					int rc = v.compareTo(w);
					return rc == 0;
				} catch (Exception e) {
				}
			}
			return left.equals(right);
		}
	};

	public final static Operator NEQ = new BinaryOperator("@neq", "!=", "<>") {
		/** The unique class ID */
		private static final long serialVersionUID = -7185932909087120854L;

		public boolean eval(Object left, Object right) {
			if (left == null && right == null)
				return false;
			if (left != null && right != null && isNumeric(left)
					&& isNumeric(right)) {
				try {
					Double v = new Double(left.toString());
					Double w = new Double(right.toString());
					int rc = v.compareTo(w);
					return rc != 0;
				} catch (Exception e) {
				}
			}
			return !("" + left).equals(("" + right));
		}
	};

	/**
	 * This operator implements a 'less than' relation. Both arguments are
	 * parsed as double values and compared. If parsing fails, their string
	 * representation is compared.
	 */
	public final static Operator LT = new BinaryOperator("@lt", "<") {
		/** The unique class ID */
		private static final long serialVersionUID = 7290798880449531730L;

		public boolean eval(Object left, Object right) {
			if (isNumeric(left) && isNumeric(right)) {
				try {
					Double v = new Double(left.toString());
					Double w = new Double(right.toString());
					int rc = v.compareTo(w);
					return rc < 0;
				} catch (Exception e) {
				}
			}

			return ("" + left).compareTo("" + right) < 0;
		}
	};

	/**
	 * This operator implement a 'less than or equal' relation.
	 */
	public final static Operator LE = new BinaryOperator("@le", "<=", "=<") {
		/** The unique class ID */
		private static final long serialVersionUID = -6196215282881485160L;

		/**
		 * @see stream.runtime.expressions.jwall.web.audit.rules.Condition#matches(java.lang.String,
		 *      java.lang.String)
		 */
		public boolean eval(Object left, Object right) {
			if (isNumeric(left) && isNumeric(right)) {
				try {
					return (new Double(left.toString()).compareTo(new Double(
							right.toString()))) <= 0;
				} catch (Exception e) {
				}
			}

			return ("" + left).compareTo("" + right) <= 0;
		}
	};

	/**
	 * This operator implements a 'greater than' relation.
	 */
	public final static Operator GT = new BinaryOperator("@gt", ">") {
		/** The unique class ID */
		private static final long serialVersionUID = 5904824908737265272L;

		@Override
		public boolean eval(Object left, Object right) {
			if (isNumeric(left) && isNumeric(right)) {
				try {
					Double v = new Double(left.toString());
					Double w = new Double(right.toString());
					int rc = v.compareTo(w);
					return rc > 0;
				} catch (Exception e) {
				}
			}

			return ("" + left).compareTo("" + right) > 0;
		}
	};

	/**
	 * This operators implements a 'greater than or equal' relation
	 */
	public final static Operator GE = new BinaryOperator("@ge", ">=", "=>") {
		/** The unique class ID */
		private static final long serialVersionUID = -1267321837098130076L;

		/**
		 * @see stream.runtime.expressions.jwall.web.audit.rules.Condition#matches(java.lang.String,
		 *      java.lang.String)
		 */
		public boolean eval(Object left, Object right) {
			if (isNumeric(left) && isNumeric(right)) {
				try {
					return (new Double(left.toString()).compareTo(new Double(
							right.toString()))) >= 0;
				} catch (Exception e) {
				}
			}

			return ("" + left).compareTo("" + right) >= 0;
		}
	};

	public final static Operator PM = new ConditionPM();

	/**
	 * This operator implements a match against a regular expression.
	 */
	public final static Operator RX = new BinaryOperator("@rx", "~") {
		/** The unique class ID */
		private static final long serialVersionUID = -2886662198464559265L;

		/**
		 * @see stream.runtime.expressions.jwall.web.audit.rules.Condition#matches(java.lang.String,
		 *      java.lang.String)
		 */
		public boolean eval(Object left, Object right) {
			if (right != null && right instanceof String) {
				Pattern pattern = Pattern.compile(right.toString());
				Matcher m = pattern.matcher(left + "");
				return m.matches();
			}
			return false;
		}
	};

	/**
	 * This operator implements a match against a regular expression.
	 */
	public final static Operator NRX = new BinaryOperator("@nrx", "!~") {
		/** The unique class ID */
		private static final long serialVersionUID = -2886662198464559265L;

		/**
		 * @see stream.runtime.expressions.jwall.web.audit.rules.Condition#matches(java.lang.String,
		 *      java.lang.String)
		 */
		public boolean eval(Object left, Object right) {
			if (right != null && right instanceof String) {
				Pattern pattern = Pattern.compile(right.toString());
				Matcher m = pattern.matcher(left + "");
				return !m.matches();
			}
			return false;
		}
	};

	/**
	 * This operator implements a simple wildcard-match where wildcards are
	 * either '*' (matching zero or more) and '?' matching exactly one
	 * character.
	 */
	public final static Operator SX = new BinaryOperator("@sx") {
		/** The unique class ID */
		private static final long serialVersionUID = 7929480963946320536L;

		@Override
		public boolean eval(Object left, Object right) {
			WildcardPattern pattern = new WildcardPattern( "" + left );
			return pattern.matches( "" + right );
		}
	};

	public final static Map<String, Operator> OPERATORS = new LinkedHashMap<String, Operator>();

	static {
		registerOperator(NEQ);
		registerOperator(EQ);
		registerOperator(LT);
		registerOperator(LE);
		registerOperator(GT);
		registerOperator(GE);
		registerOperator(PM);
		registerOperator(RX);
		registerOperator(NRX);
		registerOperator(SX);
	}

	public final static void registerOperator(Operator op) {
		OPERATORS.put(op.name, op);
		OPERATORS.put("!" + op.name, op);
		for (String alias : op.getAliases()) {
			registerAlias(op, alias);
		}
	}

	public final static void registerAlias(Operator op, String alias) {
		if (OPERATORS.get(op.name) != null) {
			OPERATORS.put(alias, OPERATORS.get(op.name));
		}
	}

	private final String name;
	private final String[] aliases;

	public Operator(String str) {
		this(str, new String[0]);
	}

	public Operator(String str, String[] aliases) {
		this.name = str;
		this.aliases = aliases;
	}

	public boolean isNegated() {
		return this.name.startsWith("!");
	}

	public String toString() {
		return name;
	}

	public String[] getAliases() {
		return aliases;
	}

	public static Operator read(String str) throws ExpressionException {

		for (String key : OPERATORS.keySet()) {
			if (key.equals(str)) {
				return OPERATORS.get(key);
			}
		}

		for (Operator op : OPERATORS.values()) {
			if (op.name.equals(str)) {
				return op;
			}
		}

		throw new ExpressionException("Invalid operator name: '" + str + "'!");
	}
}