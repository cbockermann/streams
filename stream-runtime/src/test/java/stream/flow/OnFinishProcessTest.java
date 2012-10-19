package stream.flow;

import org.junit.Assert;
import org.junit.Test;

import stream.ProcessorList;
import stream.data.DataFactory;
import stream.mock.SimpleMockProcessor;

public class OnFinishProcessTest {

	@Test
	public void test() {
		stream.runtime.Process p = new stream.runtime.Process();
		ProcessorList onFinish = new OnFinish();
		SimpleMockProcessor m1 = new SimpleMockProcessor();
		SimpleMockProcessor m2 = new SimpleMockProcessor();
		onFinish.getProcessors().add(m2);

		p.addProcessor(m1);
		p.addProcessor(onFinish);
		// Process
		p.process(DataFactory.create());
		Assert.assertTrue(m1.getProcessed());
		Assert.assertFalse(m2.getProcessed());
		// Finish
		try {
			p.finish();
			Assert.assertTrue(m1.getFinished());
			Assert.assertTrue(m2.getProcessed());
			Assert.assertTrue(m2.getFinished());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
