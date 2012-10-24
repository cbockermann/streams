/**
 * 
 */
package stream.expressions;

import stream.Context;
import stream.Data;

/**
 * <p>
 * This class provides a string-constructor implementation for the Expression
 * interface. This allows for the Condition class to be used in setter methods.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public final class Condition implements Expression {

	/** The unique class ID */
	private static final long serialVersionUID = 8532037554533799385L;

	/**
	 * The 'real' expression, parsed from the string given at instantiation
	 * time.
	 */
	Expression expression;

	/**
	 * Creates a new boolean expression from the given string. If parsing of the
	 * string fails, an exception will be thrown.
	 * 
	 * @param cond
	 * @throws Exception
	 */
	public Condition(String cond) throws Exception {
		if (cond == null || cond.trim().isEmpty())
			expression = null;
		else
			expression = ExpressionCompiler.parse(cond);
	}

	/**
	 * @see stream.expressions.Expression#matches(stream.Context,
	 *      stream.Data)
	 */
	@Override
	public boolean matches(Context ctx, Data item) {
		if (expression == null)
			return true;
		return expression.matches(ctx, item);
	}
}
