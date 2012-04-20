/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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

/**
 * <p>
 * This class provides an abstract binary operator that is provided with two
 * operands (one serializable, one string). The first operand is extracted from
 * the data item, the second operand is the string provided to this operator in
 * the filter expression.
 * </p>
 * <p>
 * For example, the following filter expression includes a single binary
 * operator that obtains the value for feature "x" (serializable) and the user
 * given value "3" as a String:
 * </p>
 * 
 * <pre>
 *      x @gt 3
 * </pre>
 * 
 * It is the operator's implementation responsibility to interpret the String
 * value accordingly.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public abstract class BinaryOperator extends Operator {

	/** The unique class ID */
	private static final long serialVersionUID = -2092261987796548990L;

	/**
	 * @param str
	 */
	public BinaryOperator(String str) {
		super(str);
	}

	public BinaryOperator(String str, String... alias) {
		super(str, alias);
	}

	public boolean isNumeric(Object val) {

		if (val instanceof Double) {
			return true;
		}

		if (val == null)
			return false;

		try {
			new Double(val.toString());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @param featureValue
	 * @param value
	 * @return
	 */
	public abstract boolean eval(Object left, Object right);
}