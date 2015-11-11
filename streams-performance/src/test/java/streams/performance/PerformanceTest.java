/**
 * 
 */
package streams.performance;

import java.net.URL;

import stream.runtime.StreamRuntime;
import stream.util.Variables;

/**
 * @author chris
 *
 */
public class PerformanceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		URL url = PerformanceTest.class.getResource("/performance-test.xml");
		System.setProperty("rlog.token", "ab09cfe1d60b602cb7600b5729da939f");
		Variables vars = StreamRuntime.loadUserProperties();
		System.out.println("vars: " + vars);
		System.out.println("rlog.token = " + vars.get("rlog.token"));
		stream.run.main(url);
	}

}
