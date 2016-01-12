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

/**
 * <p>
 * This condition checks for equality.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class ConditionContains extends BinaryOperator {

	/** The unique class ID */
	private static final long serialVersionUID = 7254577500140365820L;

	public ConditionContains() {
		super("@contains");
	}

	/**
	 * @see stream.runtime.expressions.jwall.web.audit.rules.Condition#matches(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean eval(Object input, Object pattern) {
		return pattern != null && input != null
				&& input.toString().indexOf(pattern.toString()) >= 0;
	}
}