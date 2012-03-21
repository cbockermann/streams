/**
 * 
 */
package stream.data.filter;

import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author chris
 * 
 */
public class ExpressionParseTest {

	@Test
	public void test() {

		try {
			ExpressionCompiler.parse("TriggerType == 4 ");
		} catch (Exception e) {
			fail("Parsing failed: " + e.getMessage());
		}
	}
}