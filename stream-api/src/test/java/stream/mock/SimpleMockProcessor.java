package stream.mock;

import stream.AbstractProcessor;
import stream.data.Data;

public class SimpleMockProcessor extends AbstractProcessor {

	private Boolean processed;

	public SimpleMockProcessor() {
		processed = false;
	}

	public Boolean getProcessed() {
		return processed;
	}

	public void setProcessed(Boolean processed) {
		this.processed = processed;
	}

	@Override
	public Data process(Data input) {
		processed = true;
		return input;
	}

}
