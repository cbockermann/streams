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
package stream.expressions.version2;

import stream.Context;
import stream.Data;

/**
 * @author Hendrik Blom
 * 
 */
public class NotEqualsStringCondition extends OperatorCondition<String> {

	public NotEqualsStringCondition(Expression<String> left,
			Expression<String> right) {
		super(left, right, "!=s");
	}

	@Override
	public Boolean get(Context ctx, Data item) throws Exception {
		String l = left.get(ctx, item);
		String r = right.get(ctx, item);
		if (l == null)
			return r != null;
		return !l.equals(r);
	}

	public Class<Boolean> type() {
		return Boolean.class;
	}

}