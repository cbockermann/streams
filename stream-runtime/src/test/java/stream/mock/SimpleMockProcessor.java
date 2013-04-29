package stream.mock;

import stream.Data;
import stream.ProcessContext;
import stream.StatefulProcessor;
import stream.io.Sink;

public class SimpleMockProcessor implements StatefulProcessor {

	private Boolean initialized;
	private Boolean processed;
	private Boolean finished;
	Sink output;

	public SimpleMockProcessor() {
		initialized = false;
		processed = false;
		finished = false;
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
		initialized = true;
	}

	@Override
	public Data process(Data input) {
		processed = true;
		return input;
	}

	@Override
	public void finish() throws Exception {
		finished = true;
	}

	@Override
	public void resetState() throws Exception {
	}

	/**
	 * @return the output
	 */
	public Sink getOutput() {
		return output;
	}

	/**
	 * @param output
	 *            the output to set
	 */
	public void setOutput(Sink output) {
		this.output = output;
	}

}
