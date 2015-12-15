/**
 * 
 */
package streams.profiler;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

/**
 * @author chris
 *
 */
public class ProfilerTest {

    @Test
    public void test() {
        try {
            stream.run.main(new File("examples/profiler.xml").toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
    }
}