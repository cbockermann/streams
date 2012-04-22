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
package stream.data;

import java.io.Serializable;

/**
 * @author chris
 * 
 */
public class Conventions {

	public final static class Key implements Serializable {

		/** The unique class ID */
		private static final long serialVersionUID = -4855161867291167265L;

		public final String name;
		public final String annotation;

		private Key(String annotation, String name) {
			if (annotation != null)
				this.annotation = annotation.trim();
			else
				this.annotation = null;
			this.name = name;
		}

		public String toString() {
			if (annotation == null)
				return name;

			StringBuilder s = new StringBuilder();
			if (annotation != null) {
				s.append(Data.ANNOTATION_PREFIX);
				s.append(annotation);
			}

			if (name != null && !name.equals("")) {
				if (annotation != null)
					s.append(":");
				s.append(name);
			}

			return s.toString();
		}

		public int hashCode() {
			return (name + annotation).hashCode();
		}
	}

	/**
	 * Creates a key from the specified string. The string is either a regular
	 * attribute name or an attribute name starting with '@' and an annotation
	 * prefix.
	 * 
	 * @param name
	 * @return
	 */
	public static Key createKey(String name) {
		if (!isAnnotated(name)) {
			return new Key(null, name);
		}

		int idx = name.indexOf(":");
		if (idx > 0 && name.length() > idx) {
			return new Key(name.substring(1, idx), name.substring(idx + 1));
		} else {
			return new Key(name.substring(1), name.substring(1));
		}
	}

	/**
	 * Creates a new key from the specified annotation and the given name.
	 * 
	 * @param annotation
	 * @param name
	 * @return
	 */
	public static Key createKey(String annotation, String name) {
		return new Key(annotation, name);
	}

	public static boolean isAnnotated(String name) {
		return name != null && name.startsWith(Data.ANNOTATION_PREFIX);
	}
}
