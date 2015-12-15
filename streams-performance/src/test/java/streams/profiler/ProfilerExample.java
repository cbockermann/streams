/**
 * 
 */
package streams.profiler;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;

/**
 * @author chris
 *
 */
public class ProfilerExample {

    @Test
    public void test() {
        URL url = ProfilerExample.class.getResource("/profiler-example.xml");
        try {
            stream.run.main(url);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
    }
}