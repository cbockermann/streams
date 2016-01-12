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
package stream.util;

import static org.junit.Assert.fail;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import stream.io.SourceURL;

/**
 * @author chris
 * 
 */
public class XIncluderTest {

	static Logger log = LoggerFactory.getLogger(XIncluderTest.class);

	@Test
	public void test() {

		try {
			SourceURL url = new SourceURL("classpath:/include.xml");
			log.info("Expanding inclusions in file {}", url);
			Document doc = XMLUtils.parseDocument(url.openStream());
			XIncluder includer = new XIncluder();
			doc = includer.perform(doc);

			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(new DOMSource(doc), new StreamResult(
					System.out));

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
}
