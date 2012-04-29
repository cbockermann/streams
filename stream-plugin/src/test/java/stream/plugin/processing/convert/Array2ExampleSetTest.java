package stream.plugin.processing.convert;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataFactory;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;

/**
 * 
 */

/**
 * @author chris
 * 
 */
public class Array2ExampleSetTest {

	static Logger log = LoggerFactory.getLogger(Array2ExampleSetTest.class);

	/**
	 * Test method for
	 * {@link stream.plugin.processing.convert.Array2ExampleSet#expand(stream.data.Data, int, java.lang.String, boolean)}
	 * .
	 */
	@Test
	public void testExpand() {

		Double[] array = new Double[] { 1.0d, 2.0d, 3.0d, 4.0d, 5.0d, 6.0d };
		Data item = DataFactory.create();
		item.put("array", array);

		List<Data> rows = Array2ExampleSet.expand(item, 3, "array", true);

		ExampleSetFactory factory = ExampleSetFactory.newInstance();
		ExampleSet exampleSet = factory.createExampleSet(rows);

		StringBuffer s = new StringBuffer("|");
		Iterator<Attribute> it = exampleSet.getAttributes().allAttributes();
		while (it.hasNext()) {
			Attribute a = it.next();
			s.append(" " + a.getName() + " |");
		}
		log.info(s.toString());
		log.info("|-------------------------|");

		for (Example example : exampleSet) {
			s = new StringBuffer("|");
			it = exampleSet.getAttributes().allAttributes();
			while (it.hasNext()) {
				Attribute a = it.next();
				s.append(" " + example.getValue(a) + " |");
			}
			log.info(s.toString());
		}

		for (int i = 0; i < rows.size(); i++) {
			Data row = rows.get(i);
			log.info("row[{}]: {}", i, row);
		}

		// fail("Not yet implemented");
	}
}
