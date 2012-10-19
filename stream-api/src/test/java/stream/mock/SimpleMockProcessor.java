package stream.mock;

import stream.AbstractProcessor;
import stream.ProcessContext;
import stream.data.Data;

public class SimpleMockProcessor extends AbstractProcessor {

	private Boolean initialized;
	private Boolean processed;
	private Boolean finished;

	public SimpleMockProcessor() {
		processed = false;
	}

	public Boolean getInitialized() {
		return initialized;
	}

	public void setInitialized(Boolean initialized) {
		this.initialized = initialized;
	}

	public Boolean getProcessed() {
		return processed;
	}

	public void setProcessed(Boolean processed) {
		this.processed = processed;
	}

	public Boolean getFinished() {
		return finished;
	}

	public void setFinished(Boolean finished) {
		this.finished = finished;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		initialized = true;
	}

	@Override
	public Data process(Data input) {
		processed = true;
		return input;
	}

	@Override
	public void finish() throws Exception {
		super.finish();
		finished = true;
	}

}
