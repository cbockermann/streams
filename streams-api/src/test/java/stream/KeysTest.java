/**
 * 
 */
package stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class KeysTest {

	static Logger log = LoggerFactory.getLogger(KeysTest.class);

	public final List<String> split(String s) {
		final ArrayList<String> list = new ArrayList<String>();
		String[] ks = s.split(",");

		for (String k : ks) {
			if (k.trim().isEmpty()) {
				continue;
			}
			list.add(k.trim());
		}
		return list;
	}

	/**
	 * Test method for {@link stream.Keys#Keys(java.lang.String)}.
	 */
	@Test
	public void testKeysString() {
		Keys keys = new Keys("A,B,C*");

		Set<String> selected = keys.select(split("A,B,C1,C2,D"));
		Assert.assertEquals(4, selected.size());
	}

	/**
	 * Test method for {@link stream.Keys#Keys(java.lang.String[])}.
	 */
	@Test
	public void testKeysStringArray() {
		Keys keys = new Keys("A,B,C*".split(","));

		Set<String> selected = keys.select(split("A,B,C1,C2,D"));
		Assert.assertEquals(4, selected.size());
	}

	/**
	 * Test method for {@link stream.Keys#select(java.util.Set)}.
	 */
	@Test
	public void testSelectSetOfString() {

		List<String> features = split("frame:size,frame:data,pixel:max,pixel:min,pixel:center,avg:red,avg:green,avg:blue");

		Keys keys = new Keys("frame:*,avg:*,!avg:red");

		Set<String> selected = keys.select(features);

		log.info("Full set is: {}", features);
		log.info("Keys instance is: {}", keys);
		log.info("Selected by keys: {}", selected);

		Assert.assertTrue(selected.contains("frame:size"));
		Assert.assertTrue(selected.contains("frame:data"));
		Assert.assertFalse(selected.contains("pixel:max"));
		Assert.assertFalse(selected.contains("pixel:min"));
		Assert.assertFalse(selected.contains("pixel:center"));

		Assert.assertFalse(selected.contains("avg:red"));
		Assert.assertTrue(selected.contains("avg:green"));
		Assert.assertTrue(selected.contains("avg:blue"));
	}
}
