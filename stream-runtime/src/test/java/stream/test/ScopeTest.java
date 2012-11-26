/**
 * 
 */
package stream.test;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.data.DataFactory;
import stream.data.PrintData;
import stream.data.SetValue;
import stream.flow.If;
import stream.runtime.ProcessContextImpl;

/**
 * @author chris
 * 
 */
public class ScopeTest {

	static Logger log = LoggerFactory.getLogger(ScopeTest.class);

	@Test
	public void test() {

		try {
			stream.runtime.Process p = new stream.runtime.Process();

			ProcessContext ctx = new ProcessContextImpl();

			SetValue sv = new SetValue();
			sv.setKey("tetst");
			sv.setValue("1");
			sv.setScope(new String[] { "process" });

			p.addProcessor(sv);

			If cond = new If();
			cond.setCondition("%{data.frame:red:avg} @lt 10 AND %{data.frame:green:avg} @lt 10 AND %{data.frame:blue:avg} @lt 10");

			SetValue sv2 = new SetValue();
			sv2.setKey("kapselStart");
			sv2.setValue("1");
			sv2.setScope(new String[] { "process" });

			cond.getProcessors().add(sv2);
			p.addProcessor(cond);

			PrintData pd = new PrintData();
			p.addProcessor(pd);

			p.init(ctx);

			Data item = DataFactory.create();
			item.put("frame:red:avg", 1.0d);
			item.put("frame:green:avg", 1.0d);
			item.put("frame:blue:avg", 1.0d);

			item = p.process(item);

			log.info("Value of 'kapselStart' is: {}", ctx.get("kapselStart"));
			log.info("Value of 'test' is: {}", ctx.get("test"));

		} catch (Exception e) {
			fail("Not yet implemented");
		}
	}
}
