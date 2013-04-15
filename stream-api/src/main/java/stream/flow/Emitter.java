package stream.flow;

import java.io.Serializable;

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
	protected Expression<Serializable>[] filter;

	protected String[] keys;

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
		this.sinks = new Sink[] { sink };
		filter = null;
	}

	public void setSinks(Sink[] sinks) {
		this.sinks = sinks;
	}

	/**
	 * @throws Exception
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data processMatchingData(Data data) throws Exception {
		if (data == null)
			return null;
		if (filter == null) {
			emit(data);
			return data;
		}
		filterAndEmit(data);
		return data;

	}

	protected void emit(Data data) {

		if (sinks == null) {
			log.error("No Sinks injected!");
			return;
		}

		for (int i = 0; i < sinks.length; i++) {
			try {
				sinks[i].write(data.createCopy());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		log.debug("Sending EndOfStream item to all queues...");
		emit(Data.END_OF_STREAM);
		for (int i = 0; i < sinks.length; i++) {
			sinks[i].close();
		}
	}

}
