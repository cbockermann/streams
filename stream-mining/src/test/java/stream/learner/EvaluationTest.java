/**
 * 
 */
package stream.learner;

import java.net.URL;

import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class EvaluationTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		URL url = EvaluationTest.class.getResource("/eval.xml");
		ProcessContainer container = new ProcessContainer(url);
		container.run();
	}
}