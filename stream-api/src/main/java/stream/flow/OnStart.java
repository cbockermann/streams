package stream.flow;

import stream.Data;
import stream.ProcessorList;

/**
 * @author Hendrik Blom The ProcessorList will be call only on the first arrival
 *         of a dataItem
 */
public class OnStart extends ProcessorList {

	private boolean started;

	public OnStart() {
		started = false;
	}

	@Override
	public Data process(Data input) {
		if (started)
			return input;

		started = true;
		return super.process(input);
	}

}