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
package stream.doc.helper;

import java.io.PrintStream;
import java.util.Map;

import stream.annotations.Parameter;
import stream.runtime.setup.ParameterDiscovery;

/**
 * This class simply checks the parameters of a given class and writes a table
 * for that.
 * 
 * @author chris
 * 
 */
public class MarkdownParameterTable implements ParameterTableWriter {

	/**
	 * @see stream.doc.helper.ParameterTableWriter#writeParameterTable(java.lang
	 *      .Class, java.io.PrintStream)
	 */
	@Override
	public void writeParameterTable(Class<?> clazz, PrintStream out) {

		out.println("<table>");
		out.println("<tr>");
		out.println("<th>Parameter</th><th>Type</th><th>Description</th><th>Required</th>");
		out.println("</tr>");

		Map<String, Class<?>> tmp = ParameterDiscovery
				.discoverParameters(clazz);

		for (String key : tmp.keySet()) {
			out.println("<tr>");

			Parameter p = ParameterDiscovery.getParameterAnnotation(clazz, key);
			if (p != null) {
				String name = key;
				if (p.name() != null && !p.name().trim().isEmpty())
					name = p.name();
				out.println("<td>" + name + "</td>");
				out.println("<td>" + p.description() + "</td>");
				out.println("<td>" + p.required() + "</td>");
			} else {
				out.println("<td>" + key + "</td>");
				out.println("<td></td>");
				out.println("<td>?</td>");
			}
			out.println("</tr>");
		}
		out.println("</table>");
	}

	public static void main(String args[]) throws Exception {
		MarkdownParameterTable table = new MarkdownParameterTable();
		table.writeParameterTable(stream.data.AsJSON.class, System.out);
		table.writeParameterTable(stream.parser.ParseJSON.class, System.out);
	}
}
