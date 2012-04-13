package stream.expressions;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExpressionCompiler {

	static Logger log = LoggerFactory.getLogger(ExpressionCompiler.class);

	public final static Expression parse(String str) throws ExpressionException {
		log.debug("Parsing expression: '{}'", str);
		if (str == null || str.trim().isEmpty())
			return null;

		ExpressionReader r = new ExpressionReader(str);
		return r.readFilterExpression();
	}

	public final static Expression parse(String str,
			Collection<String> variables) throws ExpressionException {
		log.debug("Parsing expression: '{}'", str);
		ExpressionReader r = new ExpressionReader(str, variables);
		return r.readFilterExpression();
	}

	public static List<Expression> expand(ExpressionList list) {
		List<Expression> exp = new LinkedList<Expression>();

		for (Expression e : list.getElements()) {
			if (e instanceof ExpressionList)
				exp.addAll(expand((ExpressionList) e));
			else
				exp.add(e);
		}

		return exp;
	}
}
