/**
 * 
 */
package streams.profiler;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import org.junit.Test;

/**
 * @author chris
 *
 */
public class ProfilerTest {

    @Test
    public void test() {
        URL url = ProfilerTest.class.getResource("/profiler.xml");
        try {
            stream.run.main(new File("examples/profiler.xml").toURI().toURL()); // url);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
    }
}