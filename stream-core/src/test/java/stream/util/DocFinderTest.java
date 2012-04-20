/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.setup.ResourceFinder;
import stream.runtime.setup.ResourceFinder.Condition;

/**
 * @author chris
 * 
 */
public class DocFinderTest {

	static Logger log = LoggerFactory.getLogger(DocFinderTest.class);

	public final static Condition isMarkdownAndProcessor = new Condition() {
		@Override
		public boolean matches(String path) {
			try {
				if (path.endsWith(".md")) {
					String f = path.replace(".md", "");
					log.debug("  replaced .md to {}", f);
					String cn = path.replace(".md", "").replaceAll("/", ".")
							.replaceFirst("\\.", "");
					log.debug("Expecting class to be {}", cn);
					Class.forName(cn);
					return true;
				}
				return false;
			} catch (Exception e) {
				return false;
			}
		}
	};

	@Test
	public void test() {
		// dummy test method to prevent junit initialization failure...
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			String[] resources = ResourceFinder.getResources("",
					isMarkdownAndProcessor);
			for (String res : resources) {
				log.info("Found: {}", res);
			}
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
		}
	}

}
