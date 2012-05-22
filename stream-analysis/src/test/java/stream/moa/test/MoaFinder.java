/**
 * 
 */
package stream.moa.test;

import static org.junit.Assert.fail;

import java.util.Map;

import moa.classifiers.Classifier;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.moa.MoaParameterFinder;

/**
 * @author chris
 * 
 */
public class MoaFinder {

	static Logger log = LoggerFactory.getLogger(MoaFinder.class);

	MoaParameterFinder finder = new MoaParameterFinder();

	@Test
	public void test() {

		try {
			Class<?>[] classes; // = ClassFinder.getClasses("moa");
			classes = new Class<?>[] { moa.classifiers.trees.HoeffdingTree.class };

			for (Class<?> clazz : classes) {

				if (clazz.isInterface())
					continue;

				if (Classifier.class.isAssignableFrom(clazz)) {
					log.info("Found classifier: {}", clazz);
					Map<String, Class<?>> types = finder.findParameters(clazz);
					for (String key : types.keySet()) {
						log.info("  {} = {}", key, types.get(key));
					}

				} else
					log.debug("Clazz: {}", clazz);
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail("Not yet implemented");
		}
	}
}
