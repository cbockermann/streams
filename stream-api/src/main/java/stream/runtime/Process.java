/**
 * 
 */
package stream.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.io.DataStream;

/**
 * @author chris
 * 
 */
public class Process extends AbstractProcess {

	static Logger log = LoggerFactory.getLogger(Process.class);
	static Integer LAST_ID = 0;
	DataStream dataStream;
	DataProcessor output;
	String processId;
	Long limit = -1L;
	ProcessContext context;
	String input;

	public Process(String processId, ProcessContext ctx, DataStream input) {
		this.processId = processId;
		this.context = ctx;
		if (this.processId == null || "".equals(processId.trim())) {
			synchronized (LAST_ID) {
				this.processId = "spu:" + LAST_ID++;
			}
		}
		this.dataStream = input;
	}

	public Process(String processId, ProcessContext ctx, DataStream input,
			DataProcessor output) {
		this(processId, ctx, input);
		addProcessor(output);
	}

	public Process() {

	}

	/**
	 * @return the input
	 */
	public String getInput() {
		return input;
	}

	/**
	 * @param input
	 *            the input to set
	 */
	public void setInput(String input) {
		this.input = input;
	}

	public void setDataStream(DataStream ds) {
		dataStream = ds;
	}

	/**
	 * @see stream.runtime.AbstractProcess#getNextItem()
	 */
	@Override
	public Data getNextItem() {
		try {
			return dataStream.readNext();
		} catch (Exception e) {
			log.error("Failed to read next item from input '{}'", dataStream);
			throw new RuntimeException("Failed to read next item from input '"
					+ dataStream + "': " + e.getMessage());
		}
	}

	/**
	 * @return the context
	 */
	public ProcessContext getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(ProcessContext context) {
		this.context = context;
	}

	/**
	 * @return the limit
	 */
	public Long getLimit() {
		return limit;
	}

	/**
	 * @param limit
	 *            the limit to set
	 */
	public void setLimit(Long limit) {
		this.limit = limit;
	}

	/**
	 * @return the processId
	 */
	public String getProcessId() {
		if (processId == null)
			processId = "spu:" + getId();
		return processId;
	}

	/**
	 * @param processId
	 *            the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public boolean isRunning() {
		return running;
	}

	public void shutdown() {
		running = false;
		this.interrupt();
	}
}