/**
 *
 */
package streams.performance;

import org.junit.Test;

/**
 * Test sending performance to a server
 *
 * @author chris
 */
public class DataRateTest {

    @Test
    public void testDataRate() throws Exception {
        stream.run.main(DataRateTest.class.getResource("/datarate-test.xml"));
    }

}
