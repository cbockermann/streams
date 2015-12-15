/**
 * 
 */
package streams.performance;

import org.junit.Test;

import stream.Data;
import stream.data.DataFactory;
import streams.logging.Rlog;

/**
 * @author chris
 *
 */
public class RlogTest {

    @Test
    public void testRlog() {

        // Variables vars = stream.runtime.StreamRuntime.loadUserProperties();
        // System.setProperty("javax.net.debug", "ssl=handshake");

        System.setProperty("rlog.host", "performance.sfb876.de");
        System.setProperty("rlog.token", "ab09cfe1d60b602cb7600b5729da939f");
        // System.setProperty("rlog.token", vars.get("rlog.token"));

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
