/**
 * 
 */
package stream.util;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.io.DataStream;

/**
 * @author chris
 * 
 */
public class DocFinder {

	static Logger log = LoggerFactory.getLogger(DocFinder.class);

	public final static Class<?>[] CLASSES = new Class<?>[] { Processor.class,
			DataStream.class };

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Class<?>[] classes = ClassFinder.getClasses("");

		SortedSet<String> docs = new TreeSet<String>();
		SortedSet<String> missing = new TreeSet<String>();

		for (Class<?> clazz : classes) {

			if (Modifier.isAbstract(clazz.getModifiers())
					|| Modifier.isInterface(clazz.getModifiers()))
				continue;

			for (Class<?> apiClass : CLASSES) {

				if (apiClass.isAssignableFrom(clazz)) {

					if (clazz.isAnnotationPresent(java.lang.Deprecated.class)) {
						System.out
								.println("Skipping deprecated class " + clazz);
						break;
					}

					log.debug("Found processor-class {}", clazz);
					log.debug("    clazz.getName() = {}", clazz.getName());
					String doc = "/" + clazz.getName().replace('.', '/')
							+ ".md";
					log.debug("    docs are at {}", doc);

					URL url = DocFinder.class.getResource(doc);
					if (url != null)
						docs.add(doc);
					else {
						missing.add(doc);
						log.error("No documentation provided for class {}",
								clazz);
					}
				}
			}
		}

		for (String doc : docs) {
			System.out.println("   " + doc);
		}
		System.out.println("");
		System.out.println("Missing documentation:");
		for (String doc : missing) {
			System.out.println("   " + doc);
		}

		Integer total = missing.size() + docs.size();
		Integer complete = docs.size();

		DecimalFormat fmt = new DecimalFormat("0.00%");
		System.out.println("Documentation completness is "
				+ fmt.format(complete.doubleValue() / total.doubleValue()));
	}

}
