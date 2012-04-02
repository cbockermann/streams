package stream.experiment;

import java.io.OutputStream;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.learner.Learner;

public class HadoopTrainer extends HadoopMapper implements DataProcessor {
	Learner<?> learner;

	public HadoopTrainer(Learner<?> learner) {
		this.learner = learner;
	}

	@Override
	public Data process(Data data) {
		learner.learn(data);
		return data;
	}

	public void writeResults(OutputStream out) {

	}
}
