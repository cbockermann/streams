package stream.flow;

import stream.ProcessorList;
import stream.data.Data;
import stream.data.DataFactory;

/**
 * @author Hendrik Blom This ProcessorList calls the subprocessors with an empty
 *         DataItem on finish.
 */
public class OnFinish extends ProcessorList {

	@Override
	public Data process(Data input) {
		return input;
	}

	@Override
	public void finish() throws Exception {
		super.process(DataFactory.create());
	}
}