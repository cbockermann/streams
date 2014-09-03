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
package stream.flow;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.annotations.Parameter;
import stream.data.DataFactory;

/**
 * @author Hendrik Blom
 * 
 */
public class Heartbeat extends Emitter {

	static Logger log = LoggerFactory.getLogger(Enqueue.class);

	protected String index;

	private int every;
	protected Long last;
	protected String sourceKey;
	protected String sourceValue;

	public String getSourceKey() {
		return sourceKey;
	}

	@Parameter(required = true)
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}

	public String getSourceValue() {
		return sourceValue;
	}

	@Parameter(required = true)
	public void setSourceValue(String sourceValue) {
		this.sourceValue = sourceValue;
	}

	public String getIndex() {
		return index;
	}

	@Parameter(required = true)
	public void setIndex(String index) {
		this.index = index;
	}

	public Integer getEvery() {
		return every;
	}

	@Parameter(required = true)
	public void setEvery(Integer every) {
		this.every = every;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		last = 0l;
		if (every <= 0)
			throw new IllegalArgumentException("every is not set.");
	}

	/**
	 * @throws Exception
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data processMatchingData(Data data) throws Exception {
		Serializable i = data.get(index);
		if (i != null && i instanceof Long) {
			Long idx = (Long) i;
			if (idx - last > every) {
				last = idx - (idx % every);
//				log.info("{}:emit {} into: {}", this, last,sinks);
				Data emit = DataFactory.create();
				for (String key : keys) {
					emit.put(key, data.get(key));
				}
				emit.put(sourceKey, sourceValue);
				emit(emit);
			}
		}
		return data;

	}
}
