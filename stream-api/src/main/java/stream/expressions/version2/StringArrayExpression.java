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
public class StringArrayExpression extends AbstractExpression<String[]> {

	private final StringExpression[] exps;
	private final String[] result;
	private final boolean statics;
	private final boolean dynamic;

	public StringArrayExpression(String e) {
		super(e);
		String[] sexps = null;
		if (super.statics) {
			dynamic = false;
			sexps = e.split(",");

			exps = new StringExpression[sexps.length];
			result = new String[sexps.length];
			int count = 0;
			for (int i = 0; i < sexps.length; i++) {
				exps[i] = new StringExpression(sexps[i]);
				if (exps[i].isStatic()) {
					count++;
				}
			}
			if (count == exps.length) {
				statics = true;
				for (int i = 0; i < exps.length; i++) {
					result[i] = exps[i].getExpression();
				}
			} else
				statics = false;
		} else {
			statics = false;
			result = null;
			dynamic = true;

			exps = null;
		}
	}

	@Override
	public String[] get(Context ctx, Data item) throws Exception {
		if (statics)
			return result;
		if (dynamic) {
			Serializable s = r.get(ctx, item);
			if (s == null)
				return null;
			String st = s.toString();
			// TODO remove whitespace?
			String[] rs = st.split(",");
			for (int i = 0; i < rs.length; i++) {
				rs[i] = rs[i].replace(" ", "");
			}
			return rs;
		}

		for (int i = 0; i < exps.length; i++) {
			result[i] = exps[i].get(ctx, item);
		}
		return result;
	}

	public Class<String[]> type() {
		return String[].class;
	}

	public boolean isStatic() {
		return statics;
	}
}
