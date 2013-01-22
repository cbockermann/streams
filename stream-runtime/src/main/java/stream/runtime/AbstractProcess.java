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

import stream.Context;
import stream.Data;
import stream.ProcessContext;
import stream.Processor;
import stream.StatefulProcessor;
import stream.io.Sink;
import stream.io.Source;

/**
 * This class implements the basic active component, ie. a thread executing
 * within the ProcessContainer.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public abstract class AbstractProcess implements stream.Process {

	static Logger log = LoggerFactory.getLogger(AbstractProcess.class);

	protected Context parentContext;
	protected ProcessContext processContext;

	protected Source source;
	protected Sink sink;

	protected final List<Processor> processors = new ArrayList<Processor>();
	int counts[];
	long millis[];

	/**
	 * @see stream.Process#setSource(stream.io.Source)
	 */
	@Override
	public void setSource(Source ds) {
		this.source = ds;
	}

	/**
	 * @see stream.Process#getSource()
	 */
	@Override
	public Source getSource() {
		return this.source;
	}

	/**
	 * @see stream.Process#setSink(stream.io.Sink)
	 */
	@Override
	public void setSink(Sink sink) {
		this.sink = sink;
	}

	/**
	 * @see stream.Process#getSink()
	 */
	@Override
	public Sink getSink() {
		return this.sink;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	public Data process(Data input) {

		Data data = input;
		log.debug("processing data {}", input);
		int i = 0;
		for (Processor proc : processors) {
			long start = System.currentTimeMillis();
			data = proc.process(data);
			counts[i]++;
			millis[i] += (System.currentTimeMillis() - start);
			if (data == null) {
				return null;
			}
			i++;
		}

		return data;
	}

	/**
	 * @see stream.DataProcessor#init(stream.runtime.Context)
	 */
	public void init(Context context) throws Exception {

		parentContext = context;
		processContext = new ProcessContextImpl(context);

		for (Processor proc : processors) {
			if (proc instanceof StatefulProcessor) {
				((StatefulProcessor) proc).init(processContext);
			}
		}
		log.debug("Process {} (source: {}) initialized, processors: ", this,
				getSource());
		int i = 0;
		for (Processor proc : processors) {
			log.debug("   {}", proc);
			counts[i] = 0;
			millis[i] = 0;
			i++;
		}
	}

	/**
	 * @see stream.DataProcessor#finish()
	 */
	public void finish() throws Exception {

		log.debug("Finishing process {} (source: {})...", this, this
				.getSource().getId());
		try {
			int i = 0;
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
				log.info("processor {} processed {} items", proc, counts[i]);
				log.info("   average time is {} ms/item", ((double) millis[i])
						/ ((double) counts[i]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see stream.Process#execute()
	 */
	@Override
	public void execute() {

		try {
			Data item = getSource().read();

			while (item != null) {
				// process the item
				//
				item = process(item);

				if (getSink() != null) {
					log.debug("Sending process output to connected sink {}",
							getSink());
					getSink().write(item);
				}

				// obtain the next item to be processed
				//
				item = getSource().read();
			}
			log.debug("No more items could be read, exiting this process.");

		} catch (Exception e) {
			log.error("Aborting process due to errors: {}", e.getMessage());
			e.printStackTrace();
		}

		try {
			finish();
		} catch (Exception e) {
			log.warn("Error while finishing process: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @return the context
	 */
	public ProcessContext getContext() {
		return processContext;
	}

	public void add(Processor p) {
		processors.add(p);
		counts = new int[processors.size()];
		millis = new long[processors.size()];
	}

	public void remove(Processor p) {
		processors.remove(p);
		counts = new int[processors.size()];
		millis = new long[processors.size()];
	}

	public List<Processor> getProcessors() {
		return processors;
	}

	public String toString() {
		return this.getClass().getCanonicalName() + "[" + super.toString()
				+ "]";
	}
}