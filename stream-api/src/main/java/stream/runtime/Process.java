/**
 * 
 */
package stream.runtime;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.Processor;
import stream.io.DataStream;

/**
 * @author chris
 * 
 */
public class Process extends Thread {

	static Logger log = LoggerFactory.getLogger(Process.class);
	static Integer LAST_ID = 0;
	DataStream input;
	DataProcessor output;
	List<Processor> processors = new ArrayList<Processor>();
	boolean running = false;
	String processId;
	Long limit = -1L;
	ProcessContext context;

	public Process(String processId, ProcessContext ctx, DataStream input) {
		this.processId = processId;
		this.context = ctx;
		if (this.processId == null || "".equals(processId.trim())) {
			synchronized (LAST_ID) {
				this.processId = "spu:" + LAST_ID++;
			}
		}
		this.input = input;
	}

	public Process(String processId, ProcessContext ctx, DataStream input,
			DataProcessor output) {
		this(processId, ctx, input);
		addDataProcessor(output);
	}

	public void addDataProcessor(Processor proc) {
		if (!processors.contains(proc))
			processors.add(proc);
	}

	public void removeDataProcessor(Processor proc) {
		processors.remove(proc);
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

	/**
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		log.debug(" ##  StreamProcessing.run()");
		running = true;

		for (Processor proc : processors) {
			try {
				if (proc instanceof DataProcessor)
					((DataProcessor) proc).init(context);
			} catch (Exception e) {
				log.error("Failed to initialize processor '{}': {}", proc,
						e.getMessage());
				throw new RuntimeException(e.getMessage());
			}
		}

		long cnt = 0;
		try {
			log.debug("Starting to read from stream {}", input);
			Data item = input.readNext();
			log.debug("First item is: {}", item);

			while (item != null && (limit < 0 || cnt < limit)) {
				cnt++;
				log.debug("Processing {}", item);

				for (Processor proc : processors) {
					log.trace("pushing copy of item to processor {}", proc);
					item = proc.process(item);
					if (item == null)
						break;
				}
				item = input.readNext();
			}
		} catch (Exception e) {
			log.error("Failed to process item: {}", e.getMessage());
			e.printStackTrace();
		}
		log.debug("{} items processed.", cnt);

		for (Processor proc : processors) {
			try {
				if (proc instanceof DataProcessor) {
					((DataProcessor) proc).finish();
				}
			} catch (Exception e) {
				log.error("Failed to finish processor '{}': {}", proc,
						e.getMessage());
			}
		}

		running = false;
	}

	public boolean isRunning() {
		return running;
	}
}