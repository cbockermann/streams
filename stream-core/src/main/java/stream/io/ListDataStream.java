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
package stream.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stream.Processor;
import stream.data.Data;
import stream.data.DataFactory;

public class ListDataStream implements DataStream {

	final List<Processor> processors = new ArrayList<Processor>();
	List<Data> data;
	int pos = 0;

	public ListDataStream(Collection<? extends Data> items) {
		data = new ArrayList<Data>(items);
		pos = 0;
	}

	@Override
	public Map<String, Class<?>> getAttributes() {
		return new HashMap<String, Class<?>>();
	}

	@Override
	public Data readNext() throws Exception {
		return readNext(DataFactory.create());
	}

	@Override
	public Data readNext(Data datum) throws Exception {
		if (datum == null)
			datum = DataFactory.create();

		if (pos < data.size()) {
			datum.putAll(data.get(pos++));
			return datum;
		}

		return null;
	}

	@Override
	public List<Processor> getPreprocessors() {
		return processors;
	}

	public void close() {
		data.clear();
	}

	/**
	 * @see stream.io.DataStream#init()
	 */
	@Override
	public void init() throws Exception {
	}
}