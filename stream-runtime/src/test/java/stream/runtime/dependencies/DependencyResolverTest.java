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
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class DependencyResolverTest {

	static Logger log = LoggerFactory.getLogger(DependencyResolverTest.class);

	// @Test
	// public void test() throws Exception {
	//
	// DependencyResolver resolver = new DependencyResolver();
	// Set<Dependency> deps = resolver.resolve(new Dependency("org.jwall",
	// "stream-analysis", "0.9.0"));
	//
	// deps = resolver.resolve(new Dependency("org.apache.axis",
	// "axis-jaxrpc", "1.4"));
	//
	// log.info("{} dependencies: {}", deps.size(), deps);
	//
	// log.info("URLs:");
	// int i = 0;
	// for (URL url : resolver.getClasspathURLs()) {
	// log.info(" {})  {}", i++, url);
	// }
	// }

	@Test
	public void testMoa() throws Exception {

		DependencyResolver resolver = new DependencyResolver();

		Set<Dependency> deps = resolver.resolve(new Dependency(
				"nz.ac.waikato.cms.moa", "moa", "2013.11"));

		log.info("{} dependencies: {}", deps.size(), deps);

		log.info("URLs:");
		int i = 0;
		for (URL url : resolver.getClasspathURLs()) {
			log.info(" {})  {}", i++, url);
		}
	}

}
