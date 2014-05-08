package stream.flow;

import junit.framework.Assert;

import org.junit.Test;

import stream.Data;
import stream.data.DataFactory;
import stream.data.ProcessContextMock;
import stream.io.Barrel;
import stream.io.Sink;
import stream.mock.SimpleMockBarrel;

public class HeartbeatTest {

	@Test
	public void test() throws Exception {
		// Config
		Heartbeat h = new Heartbeat();
		h.setKeys(new String[] { "@timestamp", "test" });
		h.setIndex("@timestamp");
		h.setEvery(1000);

		// Sink
		Barrel k = new SimpleMockBarrel();
		h.setSinks(new Sink[] { k });

		// INIT
		h.init(new ProcessContextMock());

		// Create Data

		for (long i = 0; i < 10010; i++) {
			Data d = DataFactory.create();
			d.put("@timestamp", i);
			d.put("test", "testvalue:" + i);
			h.process(d);
		}
		int c =0;
		boolean run = true;
		while (run) {
			Data d = k.read();
			if (d == null)
				run = false;
			else
				c++;
			
		}
		if(c!=10)
			Assert.fail();

	}
}
