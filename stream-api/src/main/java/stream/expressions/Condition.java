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
