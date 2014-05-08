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
