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
package stream.expressions;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Context;
import stream.data.Data;

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
		StringBuffer s = new StringBuffer("( ");

		for (Expression e : exps) {
			if (s.length() > 2)
				s.append("  " + op + "  ");
			s.append(e.toString()); // FilterCompiler.toFilterString( e ) );
		}
		s.append(" )");
		return s.toString();
	}
}