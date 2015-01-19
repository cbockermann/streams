/**
 * 
 */
package stream.data;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;

/**
 * @author chris
 * 
 */
public class RemoveTypesTest {

	static Logger log = LoggerFactory.getLogger(RemoveTypesTest.class);

	@Test
	public void test() {

		Data item = DataFactory.create();
		item.put("MyInt", 123);
		item.put("MyInteger", new Integer(456));
		item.put("MyInt[]", new int[] { 1, 2, 3 });
		item.put("MyInteger[]", new Integer[] { 4, 5, 6 });
		item.put("MyDouble", 2.1415);
		item.put("MyFloat", 1.134f);

		item.put("MyList", new ArrayList<Integer>());

		RemoveTypes remove = new RemoveTypes();

		try {
			remove.setTypes("int,int[],java.util.List".split(","));

			log.info("## item before  RemoveTypes: {}", item);
			item = remove.process(item);
			log.info("## item *after* RemoveTypes: {}", item);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

	@Test
	public void testRemoveAllArrays() {

		RemoveTypes remove = new RemoveTypes();
		remove.setTypes(new String[] { "java.lang.Object[]" });

		Data item = DataFactory.create();
		item.put("MyInteger", 123);
		item.put("MyString[]", "A,B,C".split(","));
		item.put("MyInteger[]", new Integer[] { 1, 2, 3 });
		item.put("MyObject[0]", new Object[] {});

		log.info("## item before RemoveTypes: {}", item);
		item = remove.process(item);
		log.info("## item AFTER  RemoveTypes: {}", item);
	}

	@Test
	public void testRemoveAllStringArrays() {

		log.info("#### BEGIN: RemoveAllStringArrays");
		log.info("##");
		RemoveTypes remove = new RemoveTypes();
		remove.setTypes(new String[] { "java.lang.String[]" });

		Data item = DataFactory.create();
		item.put("MyInteger", 123);
		item.put("MyString[]", "A,B,C".split(","));
		item.put("MyInteger[]", new Integer[] { 1, 2, 3 });
		item.put("MyObject[0]", new Object[] {});

		log.info("## item before RemoveTypes: {}", item);
		item = remove.process(item);
		log.info("## item AFTER  RemoveTypes: {}", item);
		log.info("##");
		log.info("#### END: RemoveAllStringArrays");
	}
}