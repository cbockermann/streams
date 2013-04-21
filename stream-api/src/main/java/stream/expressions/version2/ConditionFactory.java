package stream.expressions.version2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionFactory {
	static Logger log = LoggerFactory.getLogger(ConditionFactory.class);

	private Map<String, Expression<Double>> dExps;
	private Map<String, Expression<String>> sExps;
	private Map<String, String> subCond;

	public ConditionFactory() {
		subCond = new HashMap<String, String>();
		dExps = new HashMap<String, Expression<Double>>();
		sExps = new HashMap<String, Expression<String>>();
	}

	public Condition create(String ex) {
		// Remove all whitespaces
		ex = ex.replace(" ", "");
		// Handle empty Condition
		if (ex == null || ex.isEmpty()) {
			log.debug("Created new Empty Condition");
			return new EmptyCondition("EMPTY");
		}

		/*
		 * Replace the ExpressionStrings by a Map.key and create the
		 * corresponding Instances of Expression
		 */
		ex = replaceAndCreateExpressions(ex);
		printDoubleExpressions();
		printStringExpressions();
		log.debug("Expression: {}", ex);

		// Replace subConditions (e.g. (...)) by a Map.key
		ex = readSubConditions(ex);
		print("SubConditions", subCond);
		log.debug("Expression: {}", ex);

		// Create ConditionTree
		ConditionTree t = new ConditionTree(subCond);
		t.init();
		t.setRoot(ex);
		t.eval();
		log.debug("ConditionTree:\n {}", t);

		// Create the condition from the given tree

		Condition result = createCondition(t);
		reset();
		return result;
	}

	private Condition createCondition(ConditionTree t) {
		if (t == null)
			throw new IllegalArgumentException("Bad ConditionTree \n" + t);
		if (t.isLeaf()) {
			if (t.getRoot().contains("null"))
				return createNullCondition(t.getRoot());
			if (t.getRoot().contains("\'"))
				return createStringCondition(t.getRoot());
			return createDoubleCondition(t.getRoot());
		}
		if (t.getOp().equals("AND"))
			return new AndCondition(createCondition(t.left),
					createCondition(t.right));
		if (t.getOp().equals("OR"))
			return new OrCondition(createCondition(t.left),
					createCondition(t.right));

		throw new IllegalArgumentException("Bad ConditionTree \n" + t);
	}

	private Condition createNullCondition(String c) {
		if (c == null || c.isEmpty())
			throw new IllegalArgumentException("Bad ConditionString" + c);
		if (c.contains("==")) {
			String[] exps = c.split("==");
			if (exps.length != 2)
				throw new IllegalArgumentException("Bad ConditionString" + c);
			if (exps[0].contains("null")) {
				Expression<Serializable> ex = buildSerializableExpression(exps[1]);
				if (ex == null)
					return new EmptyCondition(c);
				return new EqualsNullCondition(c, ex);
			}
			Expression<Serializable> ex = buildSerializableExpression(exps[0]);
			if (ex == null)
				return new EmptyCondition(c);
			return new EqualsNullCondition(c, ex);
		}
		if (c.contains("!=")) {
			String[] exps = c.split("!=");
			if (exps.length != 2)
				throw new IllegalArgumentException("Bad ConditionString" + c);
			if (exps[0].contains("null")) {
				Expression<Serializable> ex = buildSerializableExpression(exps[1]);
				if (ex == null)
					return new EmptyCondition(c);
				return new NotEqualsNullCondition(c, ex);
			}
			Expression<Serializable> ex = buildSerializableExpression(exps[0]);
			if (ex == null)
				return new EmptyCondition(c);
			return new NotEqualsNullCondition(c, ex);
		}
		throw new IllegalArgumentException("Bad ConditionString" + c);
	}

	private OperatorCondition<String> createStringCondition(String c) {
		if (c == null || c.isEmpty())
			throw new IllegalArgumentException("Bad ConditionString" + c);

		// S Expressions
		if (c.contains("==")) {
			String[] exps = c.split("==");
			if (exps.length != 2)
				throw new IllegalArgumentException("Bad ConditionString" + c);
			Expression<String> left = createOrBuildStringExpression(exps[0]);
			Expression<String> right = createOrBuildStringExpression(exps[1]);
			return new EqualsStringCondition(left, right);
		}

		if (c.contains("!=")) {
			String[] exps = c.split("!=");
			if (exps.length != 2)
				throw new IllegalArgumentException("Bad ConditionString" + c);
			Expression<String> left = createOrBuildStringExpression(exps[0]);
			Expression<String> right = createOrBuildStringExpression(exps[1]);
			return new NotEqualsStringCondition(left, right);
		}
		return null;
	}

	private OperatorCondition<Double> createDoubleCondition(String c) {
		if (c == null || c.isEmpty())
			throw new IllegalArgumentException("Bad ConditionString" + c);

		// Double Expressions
		if (c.contains("==")) {
			String[] exps = c.split("==");
			if (exps.length != 2)
				throw new IllegalArgumentException("Bad ConditionString" + c);
			Expression<Double> left = createOrBuildDoubleExpression(exps[0]);
			Expression<Double> right = createOrBuildDoubleExpression(exps[1]);
			return new EqualsDoubleCondition(left, right);
		}
		if (c.contains("!=")) {
			String[] exps = c.split("!=");
			if (exps.length != 2)
				throw new IllegalArgumentException("Bad ConditionString" + c);
			Expression<Double> left = createOrBuildDoubleExpression(exps[0]);
			Expression<Double> right = createOrBuildDoubleExpression(exps[1]);
			return new NotEqualsDoubleCondition(left, right);
		}
		if (c.contains("<=")) {
			String[] exps = c.split("<=");
			if (exps.length != 2)
				throw new IllegalArgumentException("Bad ConditionString" + c);
			Expression<Double> left = createOrBuildDoubleExpression(exps[0]);
			Expression<Double> right = createOrBuildDoubleExpression(exps[1]);
			return new LEDoubleCondition(left, right);
		}
		if (c.contains(">=")) {
			String[] exps = c.split(">=");
			if (exps.length != 2)
				throw new IllegalArgumentException("Bad ConditionString" + c);
			Expression<Double> left = createOrBuildDoubleExpression(exps[0]);
			Expression<Double> right = createOrBuildDoubleExpression(exps[1]);
			return new GEDoubleCondition(left, right);
		}
		if (c.contains(">")) {
			String[] exps = c.split(">");
			if (exps.length != 2)
				throw new IllegalArgumentException("Bad ConditionString" + c);
			Expression<Double> left = createOrBuildDoubleExpression(exps[0]);
			Expression<Double> right = createOrBuildDoubleExpression(exps[1]);
			return new GDoubleCondition(left, right);
		}
		if (c.contains("<")) {
			String[] exps = c.split("<");
			if (exps.length != 2)
				throw new IllegalArgumentException("Bad ConditionString" + c);
			Expression<Double> left = createOrBuildDoubleExpression(exps[0]);
			Expression<Double> right = createOrBuildDoubleExpression(exps[1]);
			return new LDoubleCondition(left, right);
		}
		return null;

	}

	private Expression<Serializable> buildSerializableExpression(String key) {
		Expression<Double> ed = dExps.get(key);
		if (ed != null)
			return ed.toSerializableExpression();
		Expression<String> es = sExps.get(key);
		if (es != null)
			return es.toSerializableExpression();
		return null;
	}

	private Expression<Double> createOrBuildDoubleExpression(String key) {
		Expression<Double> e = dExps.get(key);
		if (e == null)
			e = new DoubleExpression(key);
		return e;
	}

	private Expression<String> createOrBuildStringExpression(String key) {
		Expression<String> e = sExps.get(key);
		if (e == null)
			e = new StringExpression(key);
		return e;
	}

	private String replaceAndCreateExpressions(String ex) {
		// PRIO 10
		List<Integer> ms = find(ex, "%{");
		List<Integer> me = find(ex, "}");
		if (ms.size() != me.size())
			throw new IllegalArgumentException("Bad %{...}");
		boolean run = true;
		int i = 0;
		while (run) {
			int s = ex.indexOf("%{");
			if (s >= 0) {
				String se;
				String key;
				int e = ex.indexOf("}");
				if (s - 1 >= 0 && ex.charAt(s - 1) == '\'') {
					if (e + 1 < ex.length() && ex.charAt(e + 1) == '\'') {
						se = ex.substring(s - 1, e + 2);
						key = ";p10e_" + i;
						StringExpression exp = new StringExpression(se);
						sExps.put(key, exp);
					} else
						throw new IllegalArgumentException("Bad String ''");
				} else {
					se = ex.substring(s, e + 1);
					key = ":p10e_" + i;
					DoubleExpression exp = new DoubleExpression(se);
					dExps.put(key, exp);
				}
				ex = ex.replace(se, key);
				i++;
			} else
				run = false;

		}
		// System.out.println("P10: " + ex);
		return ex;

	}

	private String readSubConditions(String ex) {
		// PRIO 9

		boolean run = true;
		int c = 0;
		boolean start = false;
		while (run) {
			// Bis start gefunden
			if (!start) {
				ex = brackets(ex, c);
				c++;
				// If all Brackets are removed
				if (!ex.contains("("))
					run = false;
			}
		}
		return ex;

	}

	private String brackets(String ex, int count)
			throws IllegalArgumentException {

		List<Integer> ms = find(ex, "(");
		List<Integer> me = find(ex, ")");
		if (ms.size() != me.size())
			throw new IllegalArgumentException("Bad (...) ");

		// No Brackets
		if (ms.size() == 0)
			return ex;
		// One pair of Brackets
		if (ms.size() == 1 || (me.get(0) < ms.get(1) && ms.get(0) < me.get(0))) {
			String subexp = ex.substring(ms.get(0), me.get(0) + 1);
			String key = ":subc_" + count;
			subCond.put(key, subexp.substring(1, subexp.length() - 1));
			return ex.replace(subexp, key);
		}

		// Multiple Brackets
		boolean run = true;
		int i = 1;
		while (run) {
			if (i == ms.size()) {
				i--;
				run = false;
			} else if (ms.get(i) > me.get(0)) {
				i--;
				run = false;
			} else
				i++;
		}

		String subexp = ex.substring(ms.get(i), me.get(0) + 1);
		String key = ":subc_" + count;
		subCond.put(key, subexp.substring(1, subexp.length() - 1));
		return ex.replace(subexp, key);
	}

	public List<Integer> find(String condition, String f)
			throws IllegalArgumentException {
		List<Integer> results = new ArrayList<Integer>();
		boolean run = true;
		int start = 0;
		while (run) {
			int p = condition.indexOf(f, start);
			if (p < 0)
				run = false;
			else {
				start = p + 1;
				results.add(p);
				if (start >= condition.length())
					run = false;
			}

		}
		return results;

	}

	private void printDoubleExpressions() {
		if (dExps.size() == 0)
			log.debug("No DoubleExpressions created.");
		else {
			StringBuilder b = new StringBuilder();
			b.append("DoubleExpression created:\n");
			for (Map.Entry<String, Expression<Double>> e : dExps.entrySet()) {
				b.append(e.getKey() + " = " + e.getValue() + "\n");
			}
			log.debug(b.toString());
		}
	}

	private void printStringExpressions() {
		if (sExps.size() == 0)
			log.debug("No StringExpressions created.");
		else {
			StringBuilder b = new StringBuilder();
			b.append("StringExpressions created:\n");
			for (Map.Entry<String, Expression<String>> e : sExps.entrySet()) {
				b.append(e.getKey() + " = " + e.getValue() + "\n");
			}
			log.debug(b.toString());
		}
	}

	private void print(String name, Map<String, String> m) {
		if (m.isEmpty())
			log.debug("{} is empty", name);
		else {
			StringBuilder b = new StringBuilder();
			b.append("{} contains:\n");
			for (Map.Entry<String, String> e : m.entrySet()) {
				b.append(e.getKey() + " = " + e.getValue() + "\n");
			}
			log.debug(b.toString());
		}
	}

	private final class ConditionTree {

		private String root;
		private String op = null;
		private ConditionTree left;
		private ConditionTree right;
		private boolean eval = false;
		private boolean leaf = true;
		private Map<String, String> subCond;

		public ConditionTree(Map<String, String> l9) {
			this.subCond = l9;
		}

		public String getRoot() {
			return root;
		}

		public void setRoot(String root) {
			this.root = root;
		}

		public String getOp() {
			return op;
		}

		public boolean isLeaf() {
			return leaf;
		}

		public void init() {
			left = new ConditionTree(this.subCond);
			right = new ConditionTree(this.subCond);
		}

		@Override
		public String toString() {
			if (eval = false || (eval && leaf))
				return root;

			StringBuilder s = new StringBuilder();
			s.append(op + "\n");
			s.append("left:" + left.toString() + "[" + left.leaf + "]\n");
			s.append("right:" + right.toString() + "[" + right.leaf + "]\n");
			return s.toString();
		}

		public void eval() {
			String[] split = root.split("or", 2);
			if (split.length == 2) {
				left.root = split[0];
				right.root = split[1];
				op = "OR";
				leaf = false;
				left.init();
				right.init();
				left.eval();
				right.eval();
				eval = true;
				return;
			}
			// split = root.split("||", 2);
			// if (split.length == 2) {
			// left.root = split[0];
			// right.root = split[1];
			// op = "OR";
			// leaf = false;
			// left.init();
			// right.init();
			// left.eval();
			// right.eval();
			// eval = true;
			// return;
			// }
			split = root.split("and", 2);
			if (split.length == 2) {
				left.root = split[0];
				right.root = split[1];
				op = "AND";
				leaf = false;
				left.init();
				right.init();
				left.eval();
				right.eval();
				eval = true;
				return;
			}
			// split = root.split("&&", 2);
			// if (split.length == 2) {
			// left.root = split[0];
			// right.root = split[1];
			// op = "AND";
			// leaf = false;
			// left.init();
			// right.init();
			// left.eval();
			// right.eval();
			// eval = true;
			// return;
			// }

			if (root.contains(":subc_")) {
				root = subCond.get(root);
				eval();
			}
			eval = true;
		}

	}

	private void reset() {
		subCond = new HashMap<String, String>();
		dExps = new HashMap<String, Expression<Double>>();
		sExps = new HashMap<String, Expression<String>>();
	}
}
