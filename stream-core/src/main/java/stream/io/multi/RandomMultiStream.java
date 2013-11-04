package stream.io.multi;

import stream.Data;
import stream.io.Stream;

/**
 * @author chris, hendrik
 *
 */
public class RandomMultiStream extends AbstractMultiStream {

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
	public Data readNext() throws Exception {
		String nextKey = selectNextStream();
		if (nextKey == null) {
			log.debug("No more streams found for selection!");
			return null;
		}

		Stream stream = streams.get(nextKey);

		Data nextItem = stream.read();
		if (nextItem == null) {
			log.info("Stream {} has ended, removing it from the multistream.",
					nextKey);
			streams.remove(nextKey);
			additionOrder.remove(nextKey);
			return readNext();
		}

		return nextItem;
	}
}
