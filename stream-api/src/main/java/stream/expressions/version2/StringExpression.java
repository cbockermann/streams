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

/**
 * @author Hendrik Blom
 * 
 */
public class StringExpression extends AbstractExpression<String> {

	public final String result;

	public StringExpression(String e) {
		super(e == null ? null : e.replace("'", ""));
		String st = "";
		if (this.isStatic()) {
			Serializable s;
			try {
				s = r.get(null, null);
				st = (s == null) ? null : s.toString();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		result = st;
	}

	@Override
	public String get(Context ctx, Data item) throws Exception {
		if (statics)
			return result;
		Serializable s = r.get(ctx, item);
		return (s == null) ? null : s.toString();
	}

	public Class<String> type() {
		return String.class;
	}

}
