/**
 * 
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
