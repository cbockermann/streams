/**
 * 
 */
package stream.data;

import java.io.Serializable;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.MD5;
import clojure.lang.RT;
import clojure.lang.Var;

/**
 * @author chris
 * 
 */
public class DataItemCloneTest {

	static Logger log = LoggerFactory.getLogger(DataItemCloneTest.class);

	final static Map<String, Serializable> testData = new LinkedHashMap<String, Serializable>();
	static {
		log.info("Creating test-data...");
		int atts = 10000;
		for (int i = 0; i < atts / 2; i++) {
			testData.put("X_" + i, new Double(Math.random()));
		}

		for (int i = atts / 2; i < atts; i++) {
			testData.put("X_" + i, MD5.md5("" + System.currentTimeMillis()));
		}

		log.info("Test item has {} attributes.", testData.size());
	}

	int rounds = 10000;

	@Test
	public void testClojureImpl() {

		String str = "(ns user) (defn hello [who] (println (str \"Hello, \" who)))";
		clojure.lang.Compiler.load(new StringReader(str));
		Var hello = RT.var("user", "hello");
		hello.invoke("Chris");

		Data item = new ClojureItem();
		log.info("Initialization complete.");

		item = new ClojureItem();
		Data orig = item;
		orig.put("ABC", "DEF");
		for (String key : testData.keySet()) {
			item.put(key, testData.get(key));
		}
		log.info("Created initial test data item: {}", item);

		long start = System.currentTimeMillis();
		for (int i = 0; i < rounds; i++) {
			item = item.createCopy();
			item = process(item);
		}

		long end = System.currentTimeMillis();
		log.info("Cloning clojure-item {} times took {} ms", rounds,
				(end - start));

		// for (String key : orig.keySet()) {
		// log.info("  {} = {}", key, orig.get(key));
		// }
	}

	@Test
	public void testMapImpl() {

		Data item = DataFactory.create();
		for (String key : testData.keySet()) {
			item.put(key, testData.get(key));
		}
		log.info("Created initial test data item.");

		long start = System.currentTimeMillis();
		for (int i = 0; i < rounds; i++) {
			// synchronized (item) {
			item = item.createCopy();
			item = process(item);
			// }
		}
		long end = System.currentTimeMillis();
		log.info("Cloning data-item {} times took {} ms", rounds, (end - start));
	}

	public Data process(Data item) {
		return fakeProcessList(item);
	}

	protected Data fakeProcessList(Data item) {
		for (int i = 0; i < 10; i++) {
			item.put("X_" + i, Math.random());
			item.remove("X_" + (i + 10));
		}
		item.remove("ABC");
		return item;
	}
}
