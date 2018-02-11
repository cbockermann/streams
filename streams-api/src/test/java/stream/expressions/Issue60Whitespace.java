package stream.expressions;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import stream.expressions.version2.ConditionFactory;

public class Issue60Whitespace {

	static Logger log = LoggerFactory.getLogger(Issue60Whitespace.class);

	@Test
	public void test() {

		final ConditionFactory cf = new ConditionFactory();
		stream.expressions.version2.Condition cond = cf.create("%{data.name}=='eins zwei'");

		Data item = DataFactory.create();
		item.put("name", "eins zwei");

		try {
			log.info("Testing condition '{}'", cond);
			log.info("   with item: {}", item);
			Boolean result = cond.get(item);
			log.info("result is: {}", result);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

}
