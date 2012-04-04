/**
 * 
 */
package stream.runtime;

import java.util.ArrayList;
import java.util.List;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.DataProcessor;
import stream.data.Processor;
import stream.util.parser.TimeParser;

/**
 * @author chris
 * 
 */
public abstract class AbstractProcess extends Thread implements Processor {

	protected boolean running = true;
	protected ProcessContext context;
	Long interval = 1000L;
	String intervalString = "1000ms";
	protected final List<Processor> processors = new ArrayList<Processor>();

	protected Data lastItem = null;

	/**
	 * This method will obtain the next item from the "input stream" that this
	 * instance if processing. In case of this Monitor class, the monitor will
	 * simply work on a single item over and over again.
	 * 
	 * @return
	 */
	public abstract Data getNextItem();

	/**
	 * @see stream.data.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		Data data = input;

		for (Processor proc : processors) {
			data = proc.process(data);
			if (data == null) {
				return null;
			}
		}

		return data;
	}

	/**
	 * @see stream.data.DataProcessor#init(stream.runtime.Context)
	 */
	public void init(ProcessContext context) throws Exception {
		this.context = context;

		for (Processor proc : processors) {
			if (proc instanceof DataProcessor) {
				((DataProcessor) proc).init(context);
			}
		}

		try {
			interval = TimeParser.parseTime(getInterval());
		} catch (Exception e) {
			interval = 1000L;
			throw new Exception("Failed to initialize Monitor: "
					+ e.getMessage());
		}
	}

	/**
	 * @see stream.data.DataProcessor#finish()
	 */
	public void finish() throws Exception {

		running = false;

		for (Processor proc : processors) {
			if (proc instanceof DataProcessor) {
				((DataProcessor) proc).finish();
			}
		}

		interrupt();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Data item = new DataImpl();

		while (running) {
			item = process(item);
			if (item == null) {
				item = new DataImpl();
			}
			try {
				Thread.sleep(interval);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the interval
	 */
	public String getInterval() {
		return intervalString;
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(String intervalString) {
		this.intervalString = intervalString;
	}

	/**
	 * @return the context
	 */
	public ProcessContext getContext() {
		return context;
	}

	public void addProcessor(Processor p) {
		processors.add(p);
	}

	public void removeProcessor(Processor p) {
		processors.remove(p);
	}

	public List<Processor> getProcessors() {
		return processors;
	}

	public boolean isRunning() {
		return running;
	}
}
