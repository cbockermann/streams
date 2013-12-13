package stream.flow;

import junit.framework.Assert;

import org.junit.Test;

import stream.Data;
import stream.data.DataFactory;
import stream.data.ProcessContextMock;

public class IndexTest {

	@Test
	public void indexTest() throws Exception {
		Index index = new LongIndex();
		String indexId = "testIndex";
		index.setId(indexId);
		index.setIndexKey("time");

		index.init(new ProcessContextMock());

		Data data = DataFactory.create();
		long start = System.currentTimeMillis();
		long time = start;

		data.put("time", start);
		Data indexedData = index.process(data);
		Assert.assertEquals(0l, indexedData.get("@index:" + indexId));

		for (int i = 0; i < 1000; i++) {
			time += 1000;
			data.put("time", time);
			indexedData = index.process(data);

		}
		Assert.assertEquals(1000000l, indexedData.get("@index:" + indexId));

		// Reset
		index.reset();

		start = System.currentTimeMillis();
		time = start;
		data.put("time", start);
		indexedData = index.process(data);
		Assert.assertEquals(0l, indexedData.get("@index:" + indexId));

		for (int i = 0; i < 1000; i++) {
			time += 1000;
			data.put("time", time);
			indexedData = index.process(data);
		}
		Assert.assertEquals(1000000l, indexedData.get("@index:" + indexId));
		for (int i = 0; i < 1000; i++) {
			indexedData = index.process(data);
		}
		Assert.assertEquals(1000000l, indexedData.get("@index:" + indexId));
	}
}
