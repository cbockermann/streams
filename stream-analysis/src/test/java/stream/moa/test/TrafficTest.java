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
public class TrafficTest {

	@Test
	public void test() throws Exception {

		ObjectFactory.registerObjectCreator(new MoaObjectFactory());

		URL url = TrafficTest.class.getResource("/traffic-test.xml");
		ProcessContainer pc = new ProcessContainer(url);
		pc.run();
	}

	public static void main(String[] args) throws Exception {
		new TrafficTest().test();
	}
}
