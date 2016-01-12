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
package stream.runtime;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class RuntimeClassLoader extends URLClassLoader {

	static Logger log = LoggerFactory.getLogger(RuntimeClassLoader.class);
	final Set<URL> extraURLs = new LinkedHashSet<URL>();

	/**
	 * @param urls
	 */
	public RuntimeClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public final void addExtraURLs(URL url) {

		if (url == null || extraURLs.contains(url))
			return;

		if (url.toString().indexOf("stream-runtime") >= 0) {
			log.warn(
					"Ignoring URL {} as the runtime must not be loaded more than once!",
					url);
			return;
		}

		extraURLs.add(url);
		this.addURL(url);
	}

	public final void addExtraURLs(Collection<URL> urls) {
		for (URL url : urls)
			addURL(url);
	}

	public Set<URL> getExtraURLs() {
		return Collections.unmodifiableSet(extraURLs);
	}
}
