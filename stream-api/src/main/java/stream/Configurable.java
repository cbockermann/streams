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
package stream;

/**
 * <p>
 * The Configurable interface allows having xml elements in additon to xml
 * attributes.
 * </p>
 * <p>
 * The configuration elements must be wrapped inside a &lt:configuration /&gt;
 * element. The {@link #configure(org.w3c.dom.Document)} method is then called
 * with a new {@link org.w3c.dom.Document} created from a deep copy of the xml
 * element.
 * </p>
 * 
 * 
 * @author Thomas Scharrenbach
 * @version 0.9.9
 * @since 0.9.9
 * 
 */
public interface Configurable {

	/**
	 * This method is called with the DOM element that was used to create the
	 * instance implementing this interface.
	 * 
	 * @param document
	 */
	public void configure(org.w3c.dom.Element document);

}
