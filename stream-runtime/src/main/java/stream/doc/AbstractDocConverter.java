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
package stream.doc;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author chris
 * 
 */
public abstract class AbstractDocConverter implements DocConverter {

	int level = 0;
	final Map<String, String> properties = new LinkedHashMap<String, String>();

	public AbstractDocConverter() {
	}

	/**
	 */
	public abstract void writeParameterTable(Class<?> clazz, PrintStream out);

	/**
	 */
	@Override
	public void sectionDown() {
		this.level++;
	}

	/**
	 */
	@Override
	public void sectionUp() {
		this.level--;
	}

	/**
	 * @see stream.doc.DocConverter#createTableOfContents(java.util.Collection,
	 *      java.io.OutputStream)
	 */
	@Override
	public void createTableOfContents(Collection<DocTree> elements,
			OutputStream out) {

		PrintStream p = new PrintStream(out);
		for (DocTree elem : elements) {

			if (!elem.isLeaf())
				continue;

			String path = elem.getPath();
			if (!path.isEmpty()) {
				path = path.substring(1);
			}
			p.println("\\input{" + elem.prefix + path.replace('/', '_') + "_"
					+ elem.name.replace(".md", "") + "}");
		}
		p.flush();
	}
}