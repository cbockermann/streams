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
package stream.annotations;

/**
 * <p>
 * This class simply represents text that is provided by the XML body content. A
 * processor implementing a setter method with an argument of type
 * {@link BodyContent} can be fed with the content as value.
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 *     <my.package.MyClass>
 *        Hello World!
 *     </my.package.MyClass>
 * </pre>
 * <p>
 * If the class <code>my.package.MyClass</code> provides a <b>single</b> setter
 * that expects a parameter of type {@link BodyContent}, then this setter is
 * provided with the string <code>Hello World!</code>.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class BodyContent {

	public final static String KEY = "__EMBEDDED_CONTENT__";
	String content;

	public BodyContent(String txt) {
		content = txt;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
}