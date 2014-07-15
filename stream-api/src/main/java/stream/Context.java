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
 * This interface defines a general context. A context provides the
 * functionality of a {@link stream.service.NamingService} and may additionally
 * resolve variables to objects.
 * </p>
 * <p>
 * This general definition of a context is a read-only context.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt; Hendrik Blom
 * 
 */
public interface Context {

	/**
	 * This constant defines the name of the data item context, i.e. when
	 * accessing attributes in a data item using the expression language. For
	 * example as <code>%{data.myKey}</code>.
	 */
	public final static String DATA_CONTEXT_NAME = "data";

	/**
	 * This constant defines the name of the process context within the
	 * expression language, e.g. when using <code>%{process.myVariable}</code>.
	 */
	public final static String PROCESS_CONTEXT_NAME = "process";

	public final static String COPY_CONTEXT_NAME = "copy";

	/**
	 * This constant defines the name of the container context, i.e. when
	 * referring to elements as <code>%{container.element-name}</code>.
	 */
	public final static String CONTAINER_CONTEXT_NAME = "container";

	/**
	 * This method can be used to look up a variable in the context.
	 * 
	 * @param variable
	 * @return
	 */
	public Object resolve(String key); // TODO: Shouldn't we rename this
											// method to 'get(String)' ??
	
	public boolean contains(String key);
}
