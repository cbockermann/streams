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
package stream.io.multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.data.Data;
import stream.data.DataFactory;
import stream.io.DataStream;
import stream.io.active.ActiveDataStream;
import stream.io.active.ActiveDataStreamImpl;

/**
 * <p>
 * A simple multi stream implementation.
 * </p>
 * 
 * @author Hendrik Blom
 * 
 */
public abstract class AbstractMultiDataStream implements MultiDataStream {

	static Logger log = LoggerFactory.getLogger(AbstractMultiDataStream.class);

	protected ArrayList<Processor> preprocessors;
	protected Map<String, Class<?>> attributes;

	protected Long limit = -1L;
	protected Long count = 0L;

	protected Map<String, DataStream> streams;

	public AbstractMultiDataStream() {
		this.attributes = new LinkedHashMap<String, Class<?>>();
		this.preprocessors = new ArrayList<Processor>();
		this.streams = new HashMap<String, DataStream>();
	}

	protected abstract Data readNext(Data item, Map<String, DataStream> streams)
			throws Exception;

	/**
	 * Returns the next datum from this stream.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Data readNext() throws Exception {
		return readNext(DataFactory.create());
	}

	public final Data readNext(Data item) throws Exception {

		if (limit > 0 && count >= limit)
			return null;

		Data datum = null;
		while (datum == null) {

			//
			// If the source is empty (i.e. readItem(..) returned null), we
			// cannot continue, so we leave by returning null
			//
			datum = readNext(item, streams);
			if (datum == null) {
				log.debug("End-of-stream reached!");
				return null;
			}

			//
			// Hand over the item to all pre-processors. If one of them
			// discards the item, we will continue reading the next one.
			//
			for (Processor proc : preprocessors) {
				datum = proc.process(datum);
				if (datum == null)
					break;
			}
		}
		count++;
		return datum;
	}

	public void close() throws Exception {
		for (DataStream s : streams.values()) {
			try {
				s.close();
			} catch (Exception e) {
				log.error("Failed to close stream {}: {}", s, e.getMessage());
			}
		}
	}

	@Override
	public void addStream(String id, DataStream stream) {
		streams.put(id, new ActiveDataStreamImpl(stream));
	}

	@Override
	public Map<String, DataStream> getStreams() {
		return streams;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	/**
	 * This method returns a mapping of attributes to types for all substreams.
	 * 
	 * @return
	 */
	public Map<String, Class<?>> getAttributes() {
		return this.attributes;
	}

	public List<Processor> getPreprocessors() {
		return this.preprocessors;
	}

	public void addPreprocessor(Processor proc) {
		preprocessors.add(proc);
	}

	public void addPreprocessor(int idx, Processor proc) {
		preprocessors.add(idx, proc);
	}

	public boolean removePreprocessor(Processor proc) {
		return preprocessors.remove(proc);
	}

	public Processor removePreprocessor(int idx) {
		return preprocessors.remove(idx);
	}

	/**
	 * @see stream.io.DataStream#init()
	 */
	@Override
	public void init() throws Exception {
		for (DataStream s : streams.values()) {
			s.init();
			((ActiveDataStream) s).activate();

		}
	}
}