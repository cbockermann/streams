/**
 * 
 */
package stream.node.servlets;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import stream.runtime.VariableContext;
import stream.util.URLUtilities;

/**
 * @author chris
 * 
 */
public class Template {

	final String template;

	public Template(String resource) throws IOException {
		URL url = Template.class.getResource(resource);
		template = URLUtilities.readContentOrEmpty(url);
	}

	public String expand(Map<String, String> vars) {
		VariableContext ctx = new VariableContext(System.getProperties());
		ctx.addVariables(vars);
		return ctx.expand(template, true);
	}

	public String expand(String key, String value) {
		VariableContext ctx = new VariableContext(System.getProperties());
		ctx.set(key, value);
		return ctx.expand(template, true);
	}
}