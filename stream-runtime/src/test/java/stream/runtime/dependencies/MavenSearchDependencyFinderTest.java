/**
 * 
 */
package stream.runtime.dependencies;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class MavenSearchDependencyFinderTest {

	static Logger log = LoggerFactory
			.getLogger(MavenSearchDependencyFinderTest.class);

	@Test
	public void test() {
		try {
			DependencyFinder finder = new MavenSearchDependencyFinder();

			String res = finder.find("org.jwall", "stream-plotter", null);
			log.info("Resolved query to: {}", res);

		} catch (Exception e) {
			log.error("Failed to run test: {}", e.getMessage());
			e.printStackTrace();
		}
	}

}
