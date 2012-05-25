/**
 * 
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

	@Test
	public void test() throws Exception {

		DependencyResolver resolver = new DependencyResolver();
		Set<Dependency> deps = resolver.resolve(new Dependency("org.jwall",
				"stream-analysis", "0.9.0"));

		deps = resolver.resolve(new Dependency("org.apache.axis",
				"axis-jaxrpc", "1.4"));

		log.info("{} dependencies: {}", deps.size(), deps);

		log.info("URLs:");
		int i = 0;
		for (URL url : resolver.getClasspathURLs()) {
			log.info(" {})  {}", i++, url);
		}
	}

}
