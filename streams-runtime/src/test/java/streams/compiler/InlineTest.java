/**
 * 
 */
package streams.compiler;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class InlineTest {

    static Logger log = LoggerFactory.getLogger(InlineTest.class);

    @Test
    public void test() {
        try {
            URL url = InlineTest.class.getResource("/compiler/inline-test.xml");
            stream.run.main(url);
        } catch (Exception e) {
            e.printStackTrace();
            fail("test failed: " + e.getMessage());
        }
    }

}
