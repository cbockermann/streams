package stream.test;

import stream.AbstractProcessor;
import stream.Data;

public class CounterTestProcessor extends AbstractProcessor {

	public static int count;

	@Override
	public Data process(Data data) {
		count++;
		return data;
	}
}
