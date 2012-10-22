/**
 * 
 */
package stream.data;

import org.junit.Test;

import stream.Processor;

/**
 * @author chris
 * 
 */
public class WithKeysTest {

	@Test
	public void test() {

		WithKeys wk = new WithKeys();
		wk.setKeys("*,!@fertig".split(","));

		Data item = DataFactory.create();
		item.put("x1", 1.0);
		item.put("@fertig", "true");

		RenameKey rk = new RenameKey();
		rk.setFrom("x1");
		rk.setTo("x2");
		wk.getProcessors().add(rk);

		Processor p = new Processor() {
			public Data process(Data item) {
				if (item.containsKey("@fertig"))
					throw new RuntimeException(
							"assertion failed! '@fertig' must not be present!");
				return item;
			}
		};
		wk.getProcessors().add(p);

		item = wk.process(item);
		System.out.println("item: " + item);
	}
}
