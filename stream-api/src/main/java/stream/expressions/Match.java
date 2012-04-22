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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Context;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public class Match implements Expression {
	static Logger log = LoggerFactory.getLogger(Match.class);

	/** The unique class ID */
	private static final long serialVersionUID = 7007162167342940123L;

	String left;
	Operator op;
	String right;
	boolean negated = false;

	public Match(String left, Operator o, String right) {
		this.left = left;
		this.op = o;
		this.right = right;
		this.negated = o.isNegated();
	}

	public boolean matches(Context ctx, Data item) {

		Object leftObject = ExpressionResolver.resolve(left, ctx, item);
		if (leftObject == null)
			if (ExpressionResolver.isMacroObject(left) || left.equals("null"))
				leftObject = null;
			else
				leftObject = left;

		Object rightObject = ExpressionResolver.resolve(right, ctx, item);
		if (rightObject == null)
			if (ExpressionResolver.isMacroObject(right) || right.equals("null"))
				rightObject = null;
			else
				rightObject = right;

		// Serializable featureValue = item.get(variable);
		if (op instanceof BinaryOperator) {
			BinaryOperator binOp = (BinaryOperator) op;
			boolean match = binOp.eval(leftObject, rightObject);

			boolean result = match;
			if (negated) {
				result = !match;
			}

			return result;
		}

		throw new RuntimeException("Unsupported non-binary operator: " + op);
	}

	public String toString() {
		return left + " " + op.toString() + " " + right;
	}
}
