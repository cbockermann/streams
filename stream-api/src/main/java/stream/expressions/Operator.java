package stream.expressions;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import stream.util.WildcardMatch;

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

	public final static Operator EQ = new BinaryOperator("@eq", "=", "==", "!=") {
		/** The unique class ID */
		private static final long serialVersionUID = -7185932909087120854L;

		public boolean eval(Object input, String pattern) {
			return input != null && input.toString().equals(pattern);
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

		public boolean eval(Object input, String pattern) {
			if (isNumeric(input)) {
				try {
					Double v = new Double("" + input);
					Double w = new Double(pattern);
					int rc = v.compareTo(w);
					return rc < 0;
				} catch (Exception e) {
				}
			}

			return ("" + input).compareTo(pattern) < 0;
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
		public boolean eval(Object input, String pattern) {
			if (isNumeric(pattern)) {
				try {
					return (new Double(input + "")
							.compareTo(new Double(pattern))) <= 0;
				} catch (Exception e) {
				}
			}

			return ("" + input).compareTo(pattern) <= 0;
		}
	};

	/**
	 * This operator implements a 'greater than' relation.
	 */
	public final static Operator GT = new BinaryOperator("@gt", ">") {
		/** The unique class ID */
		private static final long serialVersionUID = 5904824908737265272L;

		@Override
		public boolean eval(Object input, String pattern) {
			if (isNumeric(input)) {
				try {
					Double v = new Double(input + "");
					Double w = new Double(pattern);
					int rc = v.compareTo(w);
					return rc > 0;
				} catch (Exception e) {
				}
			}

			return ("" + input).compareTo(pattern) > 0;
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
		public boolean eval(Object input, String pattern) {
			if (isNumeric(input)) {
				try {
					return (new Double(input + "")
							.compareTo(new Double(pattern))) >= 0;
				} catch (Exception e) {
				}
			}

			return ("" + input).compareTo(pattern) >= 0;
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
		public boolean eval(Object input, String p) {
			Pattern pattern = Pattern.compile(p);
			Matcher m = pattern.matcher(input + "");
			return m.matches();
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
		public boolean eval(Object featureValue, String value) {
			return WildcardMatch.matches("" + featureValue, value);
		}
	};

	public final static Map<String, Operator> OPERATORS = new LinkedHashMap<String, Operator>();

	static {
		registerOperator(EQ);
		registerOperator(LT);
		registerOperator(LE);
		registerOperator(GT);
		registerOperator(GE);
		registerOperator(PM);
		registerOperator(RX);
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