/**
 * 
 */
package stream.test;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class VariableExpansionTest {

	static Logger log = LoggerFactory.getLogger(VariableExpansionTest.class);

	@Test
	public void test() {
		try {

			URL url = VariableExpansionTest.class
					.getResource("/variable-expansion.xml");
			stream.run.main(url);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Variable Expansion Test failed: " + e.getMessage());
		}
	}
}
