/**
 * 
 */
package stream.data;

import java.util.ArrayList;
import java.util.List;

import stream.AbstractDataProcessor;
import stream.DataProcessor;
import stream.Processor;
import stream.runtime.Context;

/**
 * @author chris
 * 
 */
public class DataProcessorList extends AbstractDataProcessor {

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
	 * @see stream.DataProcessor#init(stream.runtime.Context)
	 */
	@Override
	public void init(Context context) throws Exception {
		super.init(context);
		this.context = context;
		for (Processor p : processors) {
			if (p instanceof DataProcessor) {
				((DataProcessor) p).init(context);
			}
		}
	}

	/**
	 * @see stream.DataProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		for (Processor p : processors) {
			if (p instanceof DataProcessor) {
				((DataProcessor) p).finish();
			}
		}
	}

	public void addDataProcessor(Processor p) {
		processors.add(p);
	}
}
