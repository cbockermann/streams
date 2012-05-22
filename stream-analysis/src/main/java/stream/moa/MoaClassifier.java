/**
 * 
 */
package stream.moa;

import java.io.Serializable;
import java.rmi.RemoteException;

import stream.ProcessContext;
import stream.data.Data;
import stream.learner.PredictionService;
import weka.core.Instance;

/**
 * <p>
 * This class is a generic wrapper for MOA operators.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class MoaClassifier extends MoaProcessor implements PredictionService {

	private Object lock = new Object();

	public MoaClassifier(Class<?> moaClass) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		super(moaClass);
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
	}

	/**
	 * @see stream.moa.MoaProcessor#process(weka.core.Instance)
	 */
	@Override
	public void process(Instance instance) {
		synchronized (lock) {
			classifier.trainOnInstance(instance);
		}
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		classifier.resetLearning();
	}

	/**
	 * @see stream.learner.PredictionService#getName()
	 */
	@Override
	public String getName() throws RemoteException {
		if (id != null)
			return getId();

		return this.moaClass.getCanonicalName();
	}

	/**
	 * @see stream.learner.PredictionService#predict(stream.data.Data)
	 */
	@Override
	public Serializable predict(Data item) throws RemoteException {

		Instance instance = wrap(item);
		double[] votes = new double[0];
		synchronized (lock) {
			votes = classifier.getVotesForInstance(instance);
		}
		if (votes == null || votes.length == 0)
			return null;

		log.debug("Votes: {}", votes);
		double max = votes[0];
		int maxIdx = 0;
		for (int i = 1; i < votes.length; i++) {
			if (votes[i] > max) {
				max = votes[i];
				maxIdx = i;
			}
		}

		return instance.classAttribute().value(maxIdx);
	}
}