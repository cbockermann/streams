package stream.flow;

import org.junit.Assert;
import org.junit.Test;

import stream.ProcessorList;
import stream.data.Data;
import stream.data.DataFactory;
import stream.mock.SimpleMockProcessor;

public class OnFinishTest {

	@Test
	public void test() throws Exception {
		ProcessorList onFinish = new OnFinish();
		SimpleMockProcessor m = new SimpleMockProcessor();
		onFinish.getProcessors().add(m);

		Data item = onFinish.process(DataFactory.create());
		Assert.assertTrue(item.isEmpty());
		onFinish.finish();
		Assert.assertTrue(m.getProcessed());
	}
}
