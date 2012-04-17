/**
 * 
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

	public void addDataProcessor(Processor p) {
		processors.add(p);
	}
}
