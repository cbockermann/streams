package stream.test;

import stream.AbstractProcessor;
import stream.Data;

public class ThrowExceptionOnProcess extends AbstractProcessor {

	@Override
	public Data process(Data input) {
		throw new IllegalArgumentException("Test failed.");
	}

}
