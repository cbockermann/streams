/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
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
package stream;

import java.util.ArrayList;
import java.util.List;

import stream.annotations.Internal;

/**
 * <p>
 * This class implements a processor that contains nested processor elements.
 * The nested processors are executed in order for each elements processed by
 * this instance.
 * </p>
 * <p>
 * If any processor of the nested processor list returns a <code>null</code>
 * item, then the processing of further processor is stopped and this instance
 * returns <code>null</code> itself.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Internal
public class ProcessorList extends AbstractProcessor {

	protected final List<Processor> processors = new ArrayList<Processor>();

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data data) {
		if (data != null) {
			for (Processor p : processors) {
				data = p.process(data);
				// If any nested processor returns null we stop further
				// processing.
				//
				if (data == null)
					return null;
			}

		}
		return data;
	}

	/**
	 * @see stream.AbstractProcessor#init(ProcessContext)
	 */
	@Override
	public void init(ProcessContext context) throws Exception {
		super.init(context);
		this.context = context;
		for (Processor p : processors) {
			if (p instanceof StatefulProcessor) {
				((StatefulProcessor) p).init(context);
			}
		}
	}

	/**
	 * @see AbstractProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		for (Processor p : processors) {
			if (p instanceof StatefulProcessor) {
				((StatefulProcessor) p).finish();
			}
		}
	}

	public void add(Processor p) {
		processors.add(p);
	}

	public void add(int idx, Processor p) {
		processors.add(idx, p);
	}

	/**
	 * Adds a new processor to the list.
	 * 
	 * @param p
	 *            The processor to add.
	 * @deprecated use {@link #getProcessors()} to obtain the processor list and
	 *             add new processors to that list.
	 */
	public void addProcessor(Processor p) {
		processors.add(p);
	}

	/**
	 * Adds a new processor to the list at the specified index.
	 * 
	 * @param idx
	 *            The index where to insert the processor.
	 * @param p
	 *            The processor to add.
	 * @deprecated use {@link #getProcessors()} to obtain the processor list and
	 *             add new processors to that list.
	 */
	public void addProcessor(int idx, Processor p) {
		processors.add(idx, p);
	}

	/**
	 * Returns the list of processors executed by this class.
	 * 
	 * @return The list of processors (final).
	 */
	public List<Processor> getProcessors() {
		return processors;
	}
}