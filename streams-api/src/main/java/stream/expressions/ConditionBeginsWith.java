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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Instances of this class check all values for the given variable if they start
 * with a pre-defined substring.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class ConditionBeginsWith extends BinaryOperator {
	/** The unique class ID */
	private static final long serialVersionUID = 6247657062073419253L;
	static Logger log = LoggerFactory.getLogger(ConditionBeginsWith.class);

	public ConditionBeginsWith() {
		super("@beginsWith");
	}

	public boolean eval(Object input, Object value) {
		return value != null && input != null
				&& input.toString().startsWith(value.toString());
	}
}