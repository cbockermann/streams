/**
 * 
 */
package stream.plotter;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class LiveStreamPlotterTest {

	static Logger log = LoggerFactory.getLogger(LiveStreamPlotterTest.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		URL url = LiveStreamPlotterTest.class.getResource("/plotter-test.xml");
		log.info("Running experiment from {}", url);
		stream.run.main(new String[] { url.toString() });
	}
}
