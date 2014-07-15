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
package stream.runtime.setup.handler;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.container.IContainer;
import stream.runtime.DependencyInjection;
import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;
import stream.runtime.dependencies.Dependency;
import stream.runtime.dependencies.DependencyResolver;
import stream.runtime.setup.factory.ObjectFactory;
import stream.util.Variables;

/**
 * @author chris
 * 
 */
public class LibrariesElementHandler implements DocumentHandler, ElementHandler {

	static Logger log = LoggerFactory.getLogger(LibrariesElementHandler.class);
	final ObjectFactory objectFactory;
	final DependencyResolver resolver = new DependencyResolver();

	public LibrariesElementHandler(ObjectFactory factory) {
		this.objectFactory = factory;
	}

	/**
	 * @see stream.runtime.ElementHandler#getKey()
	 */
	@Override
	public String getKey() {
		return "LibsHandler";
	}

	/**
	 * @see stream.runtime.ElementHandler#handlesElement(org.w3c.dom.Element)
	 */
	@Override
	public boolean handlesElement(Element element) {
		if (element.getNodeName().equalsIgnoreCase("libraries")
				|| element.getNodeName().equalsIgnoreCase("libs"))
			return true;
		return false;
	}

	/**
	 * @see stream.runtime.ElementHandler#handleElement(stream.container.ProcessContainer
	 *      , org.w3c.dom.Element)
	 */
	@Override
	public void handleElement(ProcessContainer container, Element element,
			Variables variables, DependencyInjection dependencyInjection)
			throws Exception {

		String text = element.getTextContent();
		if (text == null) {
			return;
		}

		BufferedReader reader = new BufferedReader(new StringReader(text));
		String line = reader.readLine();
		while (line != null) {

			String[] dep = line.trim().split(":");
			if (dep.length != 3) {

			} else {
				Dependency d = new Dependency(dep[0], dep[1], dep[2]);
				try {
					resolver.resolve(d);
				} catch (Exception e) {

				}
			}

			line = reader.readLine();
		}

		objectFactory.addClassPathUrls(resolver.getClasspathURLs());
	}

	/**
	 * @see stream.runtime.setup.handler.DocumentHandler#handle(stream.container.ProcessContainer,
	 *      org.w3c.dom.Document)
	 */
	@Override
	public void handle(IContainer container, Document doc, Variables variables,
			DependencyInjection dependencyInjection) throws Exception {

		log.debug("Checking for dependency definitions...");

		Set<Dependency> deps = findDependencies(doc.getDocumentElement());
		log.debug("Found {} dependencies...", deps.size());

		for (Dependency dep : deps) {
			resolver.resolve(dep);
		}

		objectFactory.addClassPathUrls(resolver.getClasspathURLs());
	}

	public Set<Dependency> findDependencies(Element root) {

		Set<Dependency> deps = new LinkedHashSet<Dependency>();

		NodeList list = root.getElementsByTagName("dependency");
		for (int i = 0; i < list.getLength(); i++) {

			Element el = (Element) list.item(i);

			String groupId = null;
			String artifactId = null;
			String version = null;
			String scope = null;

			NodeList children = el.getChildNodes();
			for (int j = 0; j < children.getLength(); j++) {

				Node node = children.item(j);
				if (node.getNodeName().equals("groupId"))
					groupId = node.getTextContent();

				if (node.getNodeName().equals("artifactId"))
					artifactId = node.getTextContent();

				if (node.getNodeName().equals("version"))
					version = node.getTextContent();

				if (node.getNodeName().equals("scope"))
					scope = node.getTextContent();
			}

			if (groupId != null && artifactId != null && version != null) {

				if (version.startsWith("["))
					version = version.substring(1);

				if (version.endsWith(",)"))
					version = version.replace(",)", "");

				if (resolver.isScopeIncluded(scope)) {
					log.debug("Adding dependency {} with scope {}", artifactId,
							scope);
					deps.add(new Dependency(groupId, artifactId, version));
				} else {
					log.debug("Dependencies with scope '{}' will be ignored.",
							scope);
				}

			}
		}

		return deps;
	}
}