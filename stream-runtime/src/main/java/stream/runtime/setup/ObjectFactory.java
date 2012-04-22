/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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
package stream.runtime.setup;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import stream.annotations.EmbeddedContent;
import stream.runtime.VariableContext;

/**
 * This class implements a generic object factory that is able to instantiate
 * objects from XML elements.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class ObjectFactory extends VariableContext {

	static Logger log = LoggerFactory.getLogger(ObjectFactory.class);
	static Map<String, Integer> globalObjectNumbers = new HashMap<String, Integer>();
	final static Map<String, String> classNames = new HashMap<String, String>();

	final static String[] DEFAULT_PACKAGES = new String[] { "", "stream.data.",
			"stream.data.mapper.", "stream.data.tree.", "stream.filter.",
			"stream.data.filter.", "stream.data.stats.", "stream.data.vector.",
			"stream.data.test.", "stream.logic", "stream.flow",
			"stream.scripting" };

	// TODO: Extend this with a custom class loader that will search other
	// places like ${user.home}/lib or ${user.home}/.streams/lib/ or any
	// other search path list found in the system environment/system settings
	final ClassLoader classLoader = ObjectFactory.class.getClassLoader();

	final List<String> searchPath = new ArrayList<String>();

	protected ObjectFactory() {
		super(new HashMap<String, String>());

		for (String pkg : DEFAULT_PACKAGES) {
			addPackage(pkg);
		}
	}

	public void addPackage(String pkg) {
		String name = pkg;
		if (!name.endsWith("."))
			name = name + ".";

		if (!searchPath.contains(name))
			searchPath.add(0, name);
		else
			log.warn("Package {} already in search-path!", pkg);
	}

	public List<String> getSearchPaths() {
		return searchPath;
	}

	public static void register(String name, Class<?> clazz) {
		classNames.put(name.toLowerCase(), clazz.getName());
	}

	public static ObjectFactory newInstance() {
		return new ObjectFactory();
	}

	public static synchronized String getNextIdentifier(String obj) {
		Integer cur = globalObjectNumbers.get(obj);
		if (cur == null)
			cur = new Integer(0);

		globalObjectNumbers.put(obj, cur + 1);
		return obj + cur;
	}

	public Object create(Element node) throws Exception {
		Map<String, String> params = getAttributes(node);
		log.debug("Creating object '{}' with attributes: {}",
				node.getNodeName(), params);
		Object obj = create(this.findClassForElement(node), params);
		return obj;
	}

	public Object create(String className, Map<String, String> parameter)
			throws Exception {

		Map<String, String> params = new HashMap<String, String>();
		params.putAll(variables);
		params.putAll(parameter);

		log.debug("Parameters for new class: {}", params);
		log.debug("object-factory.variables: {}", this.variables);
		Class<?> clazz = Class.forName(className, false, classLoader);

		// create an instance of this class
		//
		Object object = clazz.newInstance();

		Map<String, String> p = new HashMap<String, String>();
		for (String key : parameter.keySet()) {
			//
			// TODO: move the macro-expansion into the ParameterInjection!
			//
			if (parameter.get(key).indexOf("%{container") >= 0) {
				String orig = parameter.get(key);
				String expanded = expand(orig);
				p.put(key, expanded);
				log.debug("Expanded {} to {}", orig, expanded);
			} else {
				String orig = parameter.get(key);
				String expanded = expand(orig);
				log.debug("Expanded {} to {}", orig, expanded);
				p.put(key, expanded);
			}
		}

		// Inject the parameters into the object...
		//
		log.debug("Injecting parameters: {}", p);
		ParameterInjection.inject(object, p, this);
		return object;
	}

	public Map<String, String> getAttributes(Node node) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		NamedNodeMap att = node.getAttributes();
		for (int i = 0; i < att.getLength(); i++) {
			Node attr = att.item(i);
			map.put(attr.getNodeName(), attr.getNodeValue());
		}

		//
		// Special case for handling the XML content as __EMBEDDED_CONTENT__
		// parameter (e.g. for the ScriptDataProcessor)
		//
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			String text = element.getTextContent();
			if (text != null && !"".equals(text.trim())) {
				map.put(EmbeddedContent.KEY, text);
			}
		}

		return map;
	}

	public String findClassForElement(Element node) throws Exception {
		if (node.getAttribute("class") != null
				&& !"".equals(node.getAttribute("class")))
			return node.getAttribute("class");

		try {
			Class.forName(node.getNodeName());
			log.debug("Found direct class-match: {}", node.getNodeName());

			URL doc = findDocumentation(node.getNodeName());
			if (doc == null)
				log.warn("No documentation provided for class '{}'!",
						node.getNodeName());

			return node.getNodeName();
		} catch (Exception e) {
		}

		for (String prefix : searchPath) {

			String cn = prefix + node.getNodeName();
			try {
				while (cn.startsWith("."))
					cn = cn.substring(1);

				log.debug("Checking for class {}", cn);
				Class<?> clazz = Class.forName(cn);
				log.debug("Auto-detected class {} for node {}", clazz,
						node.getNodeName());

				URL doc = findDocumentation(clazz.getName());
				if (doc == null)
					log.warn("No documentation provided for class '{}'!",
							clazz.getName());

				return clazz.getName();
			} catch (Exception e) {
				log.debug("No class '{}' found", cn);
			}
		}

		throw new Exception("Failed to determine class for node '"
				+ node.getNodeName() + "'!");
	}

	public URL findDocumentation(String className) {
		String docResource = "/" + className.replace('.', '/') + ".md";
		log.trace("Doc resource for '{}' is '{}'", className, docResource);
		URL url = ObjectFactory.class.getResource(docResource);
		return url;
	}
}