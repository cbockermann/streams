/**
 * 
 */
package stream.runtime;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ProcessContext;
import stream.Processor;
import stream.StatefulProcessor;
import stream.data.Data;

/**
 * This class implements the basic active component, ie. a thread executing
 * within the ProcessContainer.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public abstract class AbstractProcess extends Thread implements Runnable,
		Processor {

	static Logger log = LoggerFactory.getLogger(AbstractProcess.class);
	protected boolean running = true;
	protected ProcessContext context;
	Long interval = 1000L;
	String intervalString = "1000ms";
	protected final List<Processor> processors = new ArrayList<Processor>();

	protected Long count = 0L;

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
	 * @see stream.Processor#process(stream.data.Data)
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
	 * @see stream.DataProcessor#init(stream.runtime.Context)
	 */
	public void init(ProcessContext context) throws Exception {
		this.context = context;

		for (Processor proc : processors) {
			if (proc instanceof StatefulProcessor) {
				((StatefulProcessor) proc).init(context);
			}
		}

	}

	/**
	 * @see stream.DataProcessor#finish()
	 */
	public void finish() throws Exception {

		running = false;

		for (Processor proc : processors) {
			if (proc instanceof StatefulProcessor) {
				((StatefulProcessor) proc).finish();
			}
		}

		Thread.currentThread().interrupt();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		try {
			while (running) {

				// obtain the next item to be processed
				//
				Data item = getNextItem();
				if (item == null) {
					log.debug("No more items could be read, exiting this process.");
					running = false;
					break;
				}

				// process the item
				//
				item = process(item);
				count++;
			}
		} catch (Exception e) {
			log.error("Aborting process due to errors: {}", e.getMessage());
			e.printStackTrace();
			if (log.isDebugEnabled())
				e.printStackTrace();
			running = false;
		}
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

	public Long getNumberOfItemsProcessed() {
		return count;
	}

	public boolean isRunning() {
		return running;
	}
}
