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
public class NaiveBayesTestThenTrain {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		URL url = NaiveBayesTestThenTrain.class.getResource("/NB.xml");
		ProcessContainer container = new ProcessContainer(url);
		container.run();
	}

}
