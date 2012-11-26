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
package stream.util;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.annotations.Internal;
import stream.io.Stream;

/**
 * @author chris
 * 
 */
public class DocFinder {

	static Logger log = LoggerFactory.getLogger(DocFinder.class);

	public final static Class<?>[] CLASSES = new Class<?>[] { Processor.class,
			Stream.class };

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Class<?>[] classes = ClassFinder.getClasses("");

		SortedSet<String> docs = new TreeSet<String>();
		SortedSet<String> missing = new TreeSet<String>();

		for (Class<?> clazz : classes) {

			if (Modifier.isAbstract(clazz.getModifiers())
					|| Modifier.isInterface(clazz.getModifiers()))
				continue;

			if (clazz.isAnnotationPresent(Internal.class)) {
				System.out.println("Skipping internal class " + clazz);
				continue;
			}

			for (Class<?> apiClass : CLASSES) {

				if (apiClass.isAssignableFrom(clazz)) {

					if (clazz.isAnnotationPresent(java.lang.Deprecated.class)) {
						System.out
								.println("Skipping deprecated class " + clazz);
						break;
					}

					log.debug("Found processor-class {}", clazz);
					log.debug("    clazz.getName() = {}", clazz.getName());
					String doc = "/" + clazz.getName().replace('.', '/')
							+ ".md";
					log.debug("    docs are at {}", doc);

					URL url = DocFinder.class.getResource(doc);
					if (url != null)
						docs.add(doc);
					else {
						missing.add(doc);
						log.error("No documentation provided for class {}",
								clazz);
					}
				}
			}
		}

		for (String doc : docs) {
			System.out.println("   " + doc);
		}
		System.out.println("");
		System.out.println("Missing documentation:");
		for (String doc : missing) {
			System.out.println("   " + doc);
		}

		Integer total = missing.size() + docs.size();
		Integer complete = docs.size();

		DecimalFormat fmt = new DecimalFormat("0.00%");
		System.out.println("Documentation completness is "
				+ fmt.format(complete.doubleValue() / total.doubleValue()));
	}

}
