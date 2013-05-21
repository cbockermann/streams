package stream.flow;

import java.io.Serializable;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import stream.expressions.version2.ConditionedProcessor;
import stream.expressions.version2.DoubleExpression;
import stream.expressions.version2.Expression;
import stream.expressions.version2.StringExpression;
import stream.io.Sink;

public class Emitter extends ConditionedProcessor {

	static Logger log = LoggerFactory.getLogger(Enqueue.class);

	protected Sink[] sinks;
	protected Data[][] batch;
	protected Expression<Serializable>[] filter;

	protected String[] keys;
	protected boolean skip = false;

	protected int batchSize = 10;
	protected int batchCount = 0;

	public String[] getKeys() {
		return keys;
	}

	@SuppressWarnings("unchecked")
	public void setKeys(String[] keys) {
		this.keys = keys;
		this.filter = new Expression[keys.length];
		int i = 0;
		for (String key : keys) {
			if (key.contains("\'")) {
				filter[i] = new StringExpression(key)
						.toSerializableExpression();
			} else
				filter[i] = new DoubleExpression(key)
						.toSerializableExpression();
			i++;
		}
	}

	public void setSink(Sink sink) {
		if (sink != null) {
			this.keys = new String[] {};
			this.sinks = new Sink[] { sink };
			batch = new Data[1][batchSize];
			filter = null;
		}

	}

	public void setSinks(Sink[] sinks) {
		if (sinks != null) {
			this.sinks = sinks;
			batch = new Data[sinks.length][batchSize];
		}
	}

	public void setSkip(Boolean skip) {
		this.skip = skip;
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

	protected void emit(Data[] data) {
		if (sinks == null)
			return;

		for (int i = 0; i < sinks.length; i++) {
			try {
				ArrayList<Data> items = new ArrayList<Data>(data.length);
				for (int j = 0; j < data.length; j++) {
					items.add(data[j].createCopy());
				}
				if (sinks[i] != null)
					sinks[i].write(items);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	protected void emit(Data data) {
		if (sinks != null) {
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
					if (sinks[i] != null)
						sinks[i].write(d);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// }

			}

		}
	}

	/**
	 * @param data
	 * @return a new Data witch was created by filtering the given data item
	 * @throws Exception
	 */
	protected void filterAndEmit(Data data) throws Exception {
		Data d = DataFactory.create();
		for (Expression<Serializable> f : filter) {
			d.put(f.getExpression(), f.get(this.context, data));
		}
		if (sinks == null) {
			log.error("No Sinks injected!");
			return;
		}

		for (int i = 0; i < sinks.length; i++) {
			try {
				sinks[i].write(d);
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
		log.debug("Closing all Sinks...");
		for (int i = 0; i < sinks.length; i++) {
			if (sinks[i] != null) {
				sinks[i].close();
			}
		}
	}

}
