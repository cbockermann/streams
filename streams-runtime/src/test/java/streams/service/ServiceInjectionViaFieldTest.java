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
public class ServiceInjectionViaFieldTest {

    static Logger log = LoggerFactory.getLogger(ServiceInjectionViaFieldTest.class);

    @Test
    public void test() {

        try {
            URL url = ServiceInjectionViaFieldTest.class.getResource("/services/service-field-injection.xml");
            stream.run.main(url);
        } catch (Exception e) {

            fail("Test failed: " + e.getMessage());
        }
    }

}
