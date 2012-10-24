/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.runtime;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.Processor;
import stream.StatefulProcessor;

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
	protected final List<ProcessListener> processListener = new ArrayList<ProcessListener>();

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
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		Data data = input;
		log.debug("processing data {}", input);
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

		log.debug("Finishing process...");
		running = false;

		try {

			for (Processor proc : processors) {
				if (proc instanceof StatefulProcessor) {
					try {
						log.debug("Finishing processor {}", proc);
						((StatefulProcessor) proc).finish();
					} catch (Exception e) {
						log.error("Failed to finish processor '{}': {}", proc,
								e.getMessage());
						if (log.isDebugEnabled())
							e.printStackTrace();
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.interrupt();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		for (ProcessListener l : this.processListener) {
			l.processStarted(this);
		}

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
			if (log.isDebugEnabled())
				e.printStackTrace();
			running = false;
		}

		try {
			finish();
		} catch (Exception e) {
			log.warn("Error while finishing process: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}

		for (ProcessListener l : processListener) {
			l.processFinished(this);
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

	public String toString() {
		return this.getClass().getCanonicalName() + "[" + super.toString()
				+ "]";
	}

	public void addListener(ProcessListener l) {
		this.processListener.add(l);
	}

	public void removeListener(ProcessListener l) {
		this.processListener.remove(l);
	}
}