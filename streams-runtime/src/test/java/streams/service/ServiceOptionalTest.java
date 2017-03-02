/**
 * 
 */
package streams.service;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class ServiceOptionalTest {

    static Logger log = LoggerFactory.getLogger(ServiceOptionalTest.class);

    @Test
    public void test() {

        try {
            URL url = ServiceOptionalTest.class.getResource("/services/service-optional-test.xml");
            stream.run.main(url);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
    }

}
