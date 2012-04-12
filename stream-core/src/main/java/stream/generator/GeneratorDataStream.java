package stream.generator;

import java.util.ArrayList;
import java.util.List;

import stream.Processor;
import stream.io.DataStream;

public abstract class GeneratorDataStream implements DataStream {

	final List<Processor> processors = new ArrayList<Processor>();

	@Override
	public void addPreprocessor(Processor proc) {
		processors.add(proc);
	}

	@Override
	public void addPreprocessor(int idx, Processor proc) {
		processors.add(idx, proc);
	}

	@Override
	public List<Processor> getPreprocessors() {
		return processors;
	}

}
