/**
 * 
 */
package streams.runtime;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class FailFastTest {

    static Logger log = LoggerFactory.getLogger(FailFastTest.class);

    @Test
    public void test() {
        try {

            int syntheticErrors = 0;

            try {
                URL url = FailFastTest.class.getResource("/fail-fast-test.xml");
                stream.run.main(url);
            } catch (Exception rte) {
                log.info("Caught exception: {}", rte.getMessage());
                // rte.printStackTrace();
                syntheticErrors++;
            }

            org.junit.Assert.assertEquals(1, syntheticErrors);

        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

}
