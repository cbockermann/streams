/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
package stream.plugin.data;

import stream.data.Conventions;
import stream.data.Conventions.Key;

import com.rapidminer.streaming.ioobject.StreamingAttributeHeader;

/**
 * @author chris
 * 
 */
public class ConventionMapping {

	public final static String DEFAULT_ATTRIBUTE_ROLE = "regular";

	/**
	 * This method maps a name to an attribute header.
	 * 
	 * @param name
	 * @return
	 */
	public static StreamingAttributeHeader map(String name) {
		Key key = Conventions.createKey(name);
		return map(key);
	}

	/**
	 * This method maps a key to a StreamingAttributeHeader.
	 * 
	 * @param key
	 * @return
	 */
	public static StreamingAttributeHeader map(Key key) {
		return new StreamingAttributeHeader(key.name, 0,
				mapToRole(key.annotation), null);
	}

	/**
	 * This method creates a Key from an attribute header.
	 * 
	 * @param header
	 * @return
	 */
	public static Key map(StreamingAttributeHeader header) {
		Key key = Conventions.createKey(mapToRole(header.getRole()),
				header.getName());
		return key;
	}

	/**
	 * This method maps an annotation to an attribute role string.
	 * 
	 * @param role
	 * @return
	 */
	private static String mapToRole(String role) {

		if (role == null)
			return DEFAULT_ATTRIBUTE_ROLE;

		if ("@label".equals(role)) {
			return "label";
		}

		if ("@prediction".equals(role)) {
			return "prediction";
		}

		return DEFAULT_ATTRIBUTE_ROLE;
	}
}
