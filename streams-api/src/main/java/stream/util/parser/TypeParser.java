/**
 * 
 */
package stream.util.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class TypeParser {

	final static Map<String, String> aliases = new HashMap<String, String>();
	static {
		aliases.put("byte", "Byte");
		aliases.put("short", "Short");
		aliases.put("int", "Integer");
		aliases.put("long", "Long");
		aliases.put("float", "Float");
		aliases.put("string", "String");
	}

	final static String[] packages = new String[] { "", "java.lang.",
			"java.util." };

	static Logger log = LoggerFactory.getLogger(TypeParser.class);

	public static Class<?>[] parse(String[] types) {
		List<Class<?>> classes = new ArrayList<Class<?>>();

		for (String t : types) {
			String name = t.trim();
			boolean array = name.endsWith("[]");

			if (array) {
				name = name.substring(0, t.length() - 2);
			}

			// Do alias-mapping - this also resolves natives to Object types
			//
			if (aliases.containsKey(name)) {
				final String alias = aliases.get(name);
				log.info("Mapping aliased type:  {} ~> {}", name, alias);
				name = alias;
			}

			Class<?> typeIdentified = null;

			for (String prefix : packages) {
				try {

					Class<?> clazz = Class.forName(prefix + name);
					typeIdentified = clazz;
					classes.add(clazz);
					break;

				} catch (java.lang.ClassNotFoundException cnfe) {
					log.debug("No class found for '{}': {}", prefix + name,
							cnfe.getMessage());
					if (log.isDebugEnabled()) {
						cnfe.printStackTrace();
					}
				}

			}

			if (typeIdentified == null) {
				log.error("Failed to parse type '{}' which resolved to {}", t,
						name);
				throw new RuntimeException("Failed to parse type '" + t
						+ "' which resolved to '" + name + "'!");
			}

		}
		log.debug("List of types to remove: {}", classes);

		Class<?>[] resultTypes = classes.toArray(new Class<?>[classes.size()]);
		return resultTypes;
	}
}
