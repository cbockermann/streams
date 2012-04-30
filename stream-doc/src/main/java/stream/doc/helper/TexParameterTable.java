/**
 * 
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
public class TexParameterTable implements ParameterTableWriter {

	/* (non-Javadoc)
	 * @see stream.doc.helper.ParameterTableCreator#writeParameterTable(java.lang.Class, java.io.PrintStream)
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
		TexParameterTable table = new TexParameterTable();
		table.writeParameterTable(stream.data.AsJSON.class, System.out);
		table.writeParameterTable(stream.parser.ParseJSON.class, System.out);
	}
}
