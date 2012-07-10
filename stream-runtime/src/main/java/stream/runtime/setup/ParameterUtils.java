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
package stream.runtime.setup;

/**
 * @author chris
 * 
 */
public class ParameterUtils {

	public final static String[] split(String keys) {
		return split(keys, ",");
	}

	public final static String[] split(String keys, String separator) {

		if (keys == null || keys.isEmpty())
			return new String[0];

		String[] elements = keys.split(separator);

		int cnt = 0;
		for (int i = 0; i < elements.length; i++) {
			String val = elements[i].trim();
			if (!val.isEmpty())
				cnt++;
		}

		String[] out = new String[cnt];
		cnt = 0;
		for (int i = 0; i < elements.length; i++) {
			if (!elements[i].isEmpty()) {
				out[cnt++] = elements[i];
			}
		}

		return out;
	}

	public final static String join(String[] keys) {

		if (keys == null)
			return null;

		StringBuffer s = new StringBuffer();

		for (int cnt = 0; cnt < keys.length; cnt++) {
			String key = keys[cnt];
			if (!key.trim().isEmpty()) {
				s.append(key.trim());

				if (cnt > 0)
					s.append(",");
			}
		}

		return s.toString();
	}
}