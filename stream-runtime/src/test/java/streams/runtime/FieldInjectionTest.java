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
public class FieldInjectionTest {

    static Logger log = LoggerFactory.getLogger(FieldInjectionTest.class);

    @Test
    public void test() {
        try {
            // System.setSecurityManager(null);
            // System.setProperty("parameter.validate.fields", "false");
            URL url = FailFastTest.class.getResource("/field-injection-test.xml");
            stream.run.main(url);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
    }

}
