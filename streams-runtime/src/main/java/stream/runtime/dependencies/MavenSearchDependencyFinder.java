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
package stream.runtime.dependencies;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import stream.util.URLUtilities;

/**
 * @author chris
 * 
 */
public class MavenSearchDependencyFinder implements DependencyFinder {

	static Logger log = LoggerFactory
			.getLogger(MavenSearchDependencyFinder.class);

	/**
	 * @see stream.runtime.dependencies.DependencyFinder#find(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public String find(String groupId, String artifactId, String version)
			throws Exception {

		char sep = '?';
		StringBuffer base = new StringBuffer(
				"http://search.maven.org/solrsearch/select?q=");

		// the query string to build
		StringBuffer qs = new StringBuffer();

		if (groupId != null) {
			qs.append("g:" + groupId);
		} else {
			throw new Exception("A groupId needs to be specified!");
		}

		if (artifactId != null) {
			if (qs.length() > 0)
				qs.append("+");
			qs.append("a:" + artifactId);
		}

		if (version != null) {
			if (qs.length() > 0)
				qs.append("+");
			qs.append("v:" + version);
		}

		sep = '&';
		base.append(qs.toString());
		base.append( sep );
		base.append("rows=1");
		base.append( sep );
		base.append("wt=xml");

		URL url = new URL(base.toString());
		String res = URLUtilities.readContent(url);

		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document xml = builder.parse(url.openStream());

		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.transform(new DOMSource(xml), new StreamResult(System.out));

		XPath xp = XPathFactory.newInstance().newXPath();
		XPathExpression exp = xp.compile("//result[name=response]");

		NodeList list = (NodeList) exp.evaluate(xml, XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++) {
			log.info("");
		}

		return res;
	}

	public static void main(String[] args) throws Exception {

		DependencyFinder finder = new MavenSearchDependencyFinder();

		String res = finder.find("org.jwall", "stream-plotter", null);
		log.info("Resolved query to: {}", res);
	}
}
