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

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.expressions.version2.ConditionedProcessor;
import stream.io.Queue;
import stream.io.Sink;

/**
 * @author Hendrik Blom
 *
 */
public class Emitter extends ConditionedProcessor {

	static Logger log = LoggerFactory.getLogger(Enqueue.class);

	protected Sink[] sinks;

	protected String[] keys;
	protected boolean skip = false;

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		if (sinks == null)
			throw new IllegalArgumentException("sinks are not set");
	}

	/**
	 * @throws Exception
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data processMatchingData(Data data) throws Exception {
		emit(data);
		return data;
	}

	protected void emit(Data data) {
		for (Sink sink : sinks) {
			Data d = data.createCopy();
			try {
				log.debug("emitting to {}", sink.getId());
				sink.write(d);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void emit(Data[] data) {
		for (Sink sink : sinks) {
			try {
				sink.write(Arrays.asList(data));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see stream.AbstractProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		super.finish();

		if (sinks == null || sinks.length == 0) {
			log.debug("Closing no Sinks...");
			return;
		}
		int numberSinks = sinks.length;
		log.debug("Closing all Sinks...");

		boolean[] isEmpty = new boolean[numberSinks];
		int sum = 0;
		while (sum < numberSinks) {
			sum = 0;
			for (int i = 0; i < numberSinks; i++) {
				if (isEmpty[i]) {
					sum++;
				} else {
					Sink s = sinks[i];

					if (s == null) {
						isEmpty[i] = true;
					} else {
						if (s instanceof Queue && ((Queue) s).getSize() > 0) {
							continue;
						}
						s.close();
						isEmpty[i] = true;
						sum++;
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Emitter [sinks=" + Arrays.toString(sinks) + ", keys="
				+ Arrays.toString(keys) + "]";
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public void setSink(Sink sink) {
		if (sink != null) {
			this.keys = new String[] {};
			this.sinks = new Sink[] { sink };
		}
	}

	public void setSinks(Sink[] sinks) {
		if (sinks != null) {
			this.sinks = sinks;
		}
	}

	public void setSkip(Boolean skip) {
		this.skip = skip;
	}
}
