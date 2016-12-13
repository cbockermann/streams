/**
 * 
 */
package streams.shutdown;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;

/**
 * @author chris
 *
 */
public class HandleProcessErrors {

    @Test
    public void test() {

        try {
            URL url = HandleProcessErrors.class.getResource("/shutdown/handle-process-errors-issue-50.xml");
            stream.run.main(url);
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }
}
