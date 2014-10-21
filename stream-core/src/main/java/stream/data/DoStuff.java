package stream.data;

import java.util.Random;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;

public class DoStuff extends AbstractProcessor {

	private Random r;

	@Override
	public void init(ProcessContext ctx) throws Exception {

		super.init(ctx);
		r = new Random();
	}

	@Override
	public Data process(Data data) {
		int result = 0;
		int rnd = r.nextInt(10);
		for (int i = 0; i < 100000; i++) {
			result += rnd;
		}
		data.put("result", result);
		return data;
	}
}
