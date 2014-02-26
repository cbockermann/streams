package stream.test;

import stream.AbstractProcessor;
import stream.Data;

public class CounterTestProcessor extends AbstractProcessor implements
		CounterTestService {

	private int count = 0;

	@Override
	public Data process(Data data) {
		count++;
		return data;
	}

	@Override
	public void reset() throws Exception {
		count = 0;

	}

	public int getCount() {
		return count;
	}

}
