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
public class Issue49 {

    @Test
    public void test() {

        try {
            URL url = Issue49.class.getResource("/shutdown/shutdown-issue-49.xml");
            stream.run.main(url);
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }
}
