/**
 * 
 */
package streams.performance;

import stream.Data;
import stream.data.DataFactory;
import stream.util.Variables;
import streams.logging.Rlog;

/**
 * @author chris
 *
 */
public class RlogTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Variables vars = stream.runtime.StreamRuntime.loadUserProperties();
		// System.setProperty("javax.net.debug", "ssl=handshake");

		System.setProperty("rlog.token", vars.get("rlog.token"));

		Rlog rlog = new Rlog();
		rlog.log("Dies ist ein test!");

		for (int i = 0; i < 10; i++) {
			Data map = DataFactory.create();
			map.put("@timestamp", System.nanoTime());
			rlog.message(map).send();
			try {
				Thread.sleep(250);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
