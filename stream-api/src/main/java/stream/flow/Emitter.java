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

public class Emitter extends ConditionedProcessor {

	static Logger log = LoggerFactory.getLogger(Enqueue.class);

	protected Sink[] sinks;
	// protected Data[][] batch;
	// protected Expression<Serializable>[] filter;

	protected String[] keys;
	protected boolean skip = false;

	// protected int batchSize = 10;
	// protected int batchCount = 0;

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
		// this.filter = new Expression[keys.length];
		// int i = 0;
		// for (String key : keys) {
		// if (key.contains("\'")) {
		// filter[i] = new StringExpression(key)
		// .toSerializableExpression();
		// } else
		// filter[i] = new DoubleExpression(key)
		// .toSerializableExpression();
		// i++;
		// }
	}

	public void setSink(Sink sink) {
		if (sink != null) {
			this.keys = new String[] {};
			this.sinks = new Sink[] { sink };
			// batch = new Data[1][batchSize];
			// filter = null;
		}

	}

	public void setSinks(Sink[] sinks) {
		if (sinks != null) {
			this.sinks = sinks;
			// batch = new Data[sinks.length][batchSize];
		}
	}

	public void setSkip(Boolean skip) {
		this.skip = skip;
	}

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
		// if (data == null)
		// return null;
		// if (filter == null) {
		emit(data);
		return data;
		// }
		// filterAndEmit(data);
		// if (skip)
		// return null;
		// return data;

	}

	// protected void emit(Data[] data) {
	// if (sinks == null)
	// return;
	//
	// for (int i = 0; i < sinks.length; i++) {
	// try {
	// ArrayList<Data> items = new ArrayList<Data>(data.length);
	// for (int j = 0; j < data.length; j++) {
	// items.add(data[j].createCopy());
	// }
	// if (sinks[i] != null)
	// sinks[i].write(items);
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }

	protected void emit(Data data) {
		// log.error("No Sinks injected!");
		// return;
		// }
		// if (batchCount < batchSize) {
		// for (int i = 0; i < sinks.length; i++) {
		// batch[i][batchCount] = data.createCopy();
		// }
		// batchCount++;
		// return;
		// }
		// // batchCount = 0;
		// for (int i = 0; i < sinks.length; i++) {
		// try {
		// sinks[i].write(batch[i]);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// data.createCopy();

		for (int i = 0; i < sinks.length; i++) {
			Data d = data.createCopy();
			// if (!sinks[i].offer(d)) {
			try {
				sinks[i].write(d);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// }

		}
	}

	// /**
	// * @param data
	// * @return a new Data witch was created by filtering the given data item
	// * @throws Exception
	// */
	// protected void filterAndEmit(Data data) throws Exception {
	// Data d = DataFactory.create();
	// for (Expression<Serializable> f : filter) {
	// d.put(f.getExpression(), f.get(this.context, data));
	// }
	// if (sinks == null) {
	// log.error("No Sinks injected!");
	// return;
	// }
	//
	// for (int i = 0; i < sinks.length; i++) {
	// try {
	// sinks[i].write(d);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }

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
		log.debug("Closing all Sinks...");

		boolean[] isEmpty = new boolean[sinks.length];
		int sum = 0;
		while (sum < sinks.length) {
			sum = 0;
			for (int i = 0; i < sinks.length; i++) {
				if (isEmpty[i])
					sum++;
				else {
					Sink s = sinks[i];

					if (s == null)
						isEmpty[i] = true;
					else {
						if (s instanceof Queue && ((Queue) s).getSize() > 0)
							continue;
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

}
