/**
 * 
 */
package stream.data;

import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author chris
 * 
 */
public class EncryptionTest {

	@Test
	public void test() {
		try {
			stream.run.main(EncryptionTest.class
					.getResource("/encryption-test.xml"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Not yet implemented");
		}
	}

}
