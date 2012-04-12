package stream.runtime.expressions;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.runtime.Context;

/**
 * <p>
 * Implements a complex expression which ORs or ANDs multiple single filter
 * expressions into one.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 */
public class ExpressionList implements Expression {
	/** The unique class ID */
	private static final long serialVersionUID = -6592861898522001021L;

	static Logger log = LoggerFactory.getLogger(ExpressionList.class);

	BooleanOperator op;
	Collection<Expression> exps;

	public ExpressionList(BooleanOperator op, Collection<Expression> exps) {
		this.op = op;
		this.exps = exps;
	}

	public ExpressionList(BooleanOperator op, List<Match> matches) {
		this.op = op;
		exps = new LinkedList<Expression>();
		for (Match m : matches)
			exps.add(m);
	}

	public BooleanOperator getOperator() {
		return op;
	}

	/**
	 * @see stream.stream.runtime.expressions.Expression.web.audit.filter.FilterExpression#matches(org.jwall.web.audit.AuditEvent)
	 */
	@Override
	public boolean matches(Context ctx, Data evt) {
		switch (op) {
		case OR:
			return or(ctx, evt);
		default:
			return and(ctx, evt);
		}
	}

	private boolean and(Context ctx, Data evt) {
		log.debug("Asserting all matches!");
		for (Expression exp : exps) {
			if (!exp.matches(ctx, evt))
				return false;
		}

		return true;
	}

	private boolean or(Context ctx, Data evt) {
		log.debug("Asserting any match!");

		for (Expression exp : exps) {
			if (exp.matches(ctx, evt))
				return true;
		}

		return false;
	}

	public int size() {
		return exps.size();
	}

	public Collection<Expression> getElements() {
		return exps;
	}

	public Expression getFirst() {
		if (exps.isEmpty())
			return null;
		return exps.iterator().next();
	}

	public String toString() {
		StringBuffer s = new StringBuffer("");
		if (exps.size() > 1)
			s.append("{");

		for (Expression e : exps) {
			if (s.length() > 2)
				s.append("  " + op + "  ");
			s.append(e.toString()); // FilterCompiler.toFilterString( e ) );
		}
		if (exps.size() > 1)
			s.append(" }");
		return s.toString();
	}
}