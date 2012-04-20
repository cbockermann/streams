/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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

import stream.data.Data;

/**
 * @author chris
 * 
 */
public class ProcessorList extends AbstractProcessor {

	protected final List<Processor> processors = new ArrayList<Processor>();

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		Data data = input;

		if (data != null) {

			for (Processor p : processors) {
				data = p.process(data);
			}

			return data;
		}

		return input;
	}

	/**
	 * @see stream.DataProcessor#init(stream.Context)
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
	 * @see stream.DataProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		for (Processor p : processors) {
			if (p instanceof StatefulProcessor) {
				((StatefulProcessor) p).finish();
			}
		}
	}

	public void addProcessor(Processor p) {
		processors.add(p);
	}

	public void addProcessor(int idx, Processor p) {
		processors.add(idx, p);
	}

	public List<Processor> getProcessors() {
		return processors;
	}
}
