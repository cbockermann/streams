/**
 * 
 */
package stream.urls;

import static org.junit.Assert.fail;

import org.junit.Test;

import stream.io.SourceURL;

/**
 * @author chris
 *
 */
public class SourceURLTest {

    @Test
    public void urlWithNoProtocol() {

        try {

            new SourceURL("path/to/file.xml");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
    }
}
