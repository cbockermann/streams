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
public class PlotterTest {

	static Logger log = LoggerFactory.getLogger(PlotterTest.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		URL url = PlotterTest.class.getResource("/plotter-test.xml");
		log.info("Running experiment from {}", url);
		stream.run.main(url.toString());
	}
}
