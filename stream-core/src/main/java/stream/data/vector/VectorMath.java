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
package stream.data.vector;

import java.io.Serializable;

import stream.Data;
import stream.data.DataUtils;

/**
 * @author chris
 * @deprecated
 */
public class VectorMath {

	public static void add(Data x, Data y) {
		add(x, 1.0d, y);
	}

	public static void add(Data x, double scale, Data y) {

		for (String key : y.keySet()) {
			if (!DataUtils.isHiddenOrSpecial(key)) {
				Serializable v = y.get(key);
				if (v instanceof Double) {

					Serializable vx = x.get(key);
					if (vx == null) {
						x.put(key, scale * (Double) v);
					} else {
						Double d = new Double(vx.toString());
						x.put(key, d + (Double) v);
					}
				}
			}
		}
	}

	public static void scale(Data x, Double factor) {
		for (String key : x.keySet()) {
			if (!DataUtils.isHiddenOrSpecial(key)) {
				Serializable v = x.get(key);
				if (v instanceof Double) {
					Double scaled = factor * (Double) v;
					x.put(key, scaled);
				}
			}
		}
	}
}
