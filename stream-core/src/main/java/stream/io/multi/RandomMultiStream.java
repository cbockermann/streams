package stream.io.multi;

import java.util.Map;

import stream.Data;
import stream.io.Stream;

public class RandomMultiStream extends AbstractMultiDataStream {

	Double[] weights;

	public Double[] getWeights() {
		return weights;
	}

	public void setWeights(Double[] weights) {
		this.weights = weights;
	}

	@Override
	public void init() throws Exception {
		super.init();

		if (weights == null) {
			log.warn("No weights specified, using unified distribution over all streams.");
			weights = new Double[this.additionOrder.size()];
			for (int i = 0; i < weights.length; i++) {
				weights[i] = 1.0d / additionOrder.size();
			}
		} else {
			double totalWeight = 0.0d;
			for (int i = 0; i < weights.length; i++) {
				totalWeight += weights[i];
			}

			for (int i = 0; i < weights.length; i++) {
				weights[i] = weights[i] / totalWeight;
			}
		}
	}

	protected String selectNextStream() {

		if (additionOrder.isEmpty())
			return null;

		double rnd = Math.random();

		double sum = 0.0d;

		for (int i = 0; i < additionOrder.size(); i++) {

			if (rnd >= sum && rnd < (sum + weights[i])) {
				return additionOrder.get(i);
			}

			sum += weights[i];
		}

		return additionOrder.get(additionOrder.size() - 1);
	}

	@Override
	protected Data readNext(Data item, Map<String, Stream> streams)
			throws Exception {
		String nextKey = selectNextStream();
		if (nextKey == null) {
			log.info("No more streams found for selection!");
			return null;
		}

		Stream stream = streams.get(nextKey);

		Data nextItem = stream.read();
		if (nextItem == null) {
			streams.remove(nextKey);
			additionOrder.remove(nextKey);
			return readNext(item, streams);
		}

		return nextItem;
	}
}
