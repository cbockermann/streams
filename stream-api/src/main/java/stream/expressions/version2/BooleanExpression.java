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
package stream.expressions.version2;

import java.io.Serializable;

import stream.Context;
import stream.Data;

public class BooleanExpression extends AbstractExpression<Boolean> {

	public BooleanExpression(String e) {
		super(e);
	}

	@Override
	public Boolean get(Context ctx, Data item) throws Exception {
		Serializable s = r.get(ctx, item);
		if (s == null)
			return null;
		if (s instanceof Boolean)
			return ((Boolean) s);
		if (s instanceof Double) {
			Double d = (Double) s;
			if (d == 1)
				return true;
			if (d == 0)
				return false;
			return null;
		}
		if (s instanceof Integer) {
			Integer d = (Integer) s;
			if (d == 1)
				return true;
			if (d == 0)
				return false;
			return null;
		}

		if (s instanceof String) {
			String d = (String) s;
			if (d.equals("1") || d.equals("1.0"))
				return true;
			if (d.equals("0") || d.equals("0.0"))
				return false;
			return null;
		}
		return null;
	}

	@Override
	public Class<Boolean> type() {
		return Boolean.class;
	}

}
