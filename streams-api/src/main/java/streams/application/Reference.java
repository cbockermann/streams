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
package streams.application;

/**
 * <p>
 * This class defines a reference for dependency injection.
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public class Reference {

	/** The object to inject the resolved reference later on */
	final Object object;

	/** The property of the object into which the reference is to be injected */
	final String property;

	/** IDs of the referenced objects may be more than 1 for array injection */
	final String[] ids;

	public Reference(Object target, String property, String id) {
		this.object = target;
		this.property = property;
		this.ids = new String[] { id };
	}

	public Reference(Object target, String property, String[] id) {
		this.object = target;
		this.property = property;
		this.ids = id;
	}

	public Object object() {
		return object;
	}

	public String property() {
		return property;
	}

	public String[] ids() {
		return ids;
	}

	public String toString() {
		StringBuffer s = new StringBuffer(this.getClass().getSimpleName() + "["
				+ object + "]{ '" + property + "':[");
		for (int i = 0; i < ids.length; i++) {
			s.append(ids[i]);
			if (i + 1 < ids.length)
				s.append(", ");
		}
		s.append("] }");
		return s.toString();
	}
}
