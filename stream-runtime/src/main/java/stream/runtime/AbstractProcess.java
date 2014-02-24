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

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

	private String id;
	protected Context parentContext;
	protected ProcessContext processContext;

	protected Source source;
	protected Sink sink;

	protected final List<Processor> processors = new ArrayList<Processor>();

	protected final Map<String, String> properties = new LinkedHashMap<String, String>();

	protected Priority priority = new Priority();

	/**
	 * @see stream.Process#setInput(stream.io.Source)
	 */
	@Override
	public void setInput(Source ds) {
		this.source = ds;
	}

	/**
	 * @see stream.Process#getInput()
	 */
	@Override
	public Source getInput() {
		return this.source;
	}

	/**
	 * @see stream.Process#setOutput(stream.io.Sink)
	 */
	@Override
	public void setOutput(Sink sink) {
		this.sink = sink;
	}

	/**
	 * @see stream.Process#getOutput()
	 */
	@Override
	public Sink getOutput() {
		return this.sink;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	public Data process(Data data) {
		log.debug("processing data {}", data);

		for (Processor proc : processors) {
			data = proc.process(data);
			if (data == null)
				return null;
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
			if (proc instanceof StatefulProcessor)
				((StatefulProcessor) proc).init(processContext);
		}
		log.debug("Process {} (source: {}) initialized, processors: ", this,
				getInput());
	}

	/**
	 * @see stream.DataProcessor#finish()
	 */
	public void finish() throws Exception {
		log.debug("Finishing process {} (source: {})...", this, this.getInput());
		for (Processor proc : processors) {
			if (proc instanceof StatefulProcessor) {
				try {
					log.debug("Finishing processor {}", proc);
					((StatefulProcessor) proc).finish();
				} catch (Exception e) {
					log.error("Failed to finish processor '{}': {}", proc,
							e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @see stream.Process#execute()
	 */
	@Override
	public void execute() {

		try {
			if (getInput() == null) {
				log.error("Could not read from input!");
				throw new IOException("Can't read from input");
			}
			Data item = getInput().read();

			while (item != null) {
				// process the item
				//
				item = process(item);

				if (item != null && getOutput() != null) {
					log.debug("Sending process output to connected sink {}",
							getOutput());
					getOutput().write(item);
				}

				// obtain the next item to be processed
				//
				item = getInput().read();
			}
			log.debug("No more items could be read, exiting this process.");

		} catch (Exception e) {
			// TODO ExceptionHandling
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
	}

	public void remove(Processor p) {
		processors.remove(p);
	}

	public List<Processor> getProcessors() {
		return processors;
	}

	public Map<String, String> getProperties() {
		return this.properties;
	}

	/**
	 * @return the priority
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public String toString() {
		if (id != null)
			return getClass().getCanonicalName() + "[" + id + "]";

		return this.getClass().getCanonicalName() + "[" + super.toString()
				+ "]";
	}
}