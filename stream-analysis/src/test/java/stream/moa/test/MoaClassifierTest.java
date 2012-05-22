/**
 * 
 */
package stream.moa.test;

import java.net.URL;

import org.junit.Test;

import stream.moa.MoaObjectFactory;
import stream.runtime.ProcessContainer;
import stream.runtime.setup.ObjectFactory;

/**
 * @author chris
 * 
 */
public class MoaClassifierTest {

	@Test
	public void test() throws Exception {

		ObjectFactory.registerObjectCreator(new MoaObjectFactory());

		URL url = MoaClassifierTest.class.getResource("/moa-test.xml");
		ProcessContainer pc = new ProcessContainer(url);
		pc.run();
	}

	public static void main(String[] args) throws Exception {
		new MoaClassifierTest().test();
	}
}
