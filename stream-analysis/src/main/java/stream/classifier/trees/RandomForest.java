/**
 * 
 */
package stream.classifier.trees;

import java.io.Serializable;
import java.rmi.RemoteException;

import stream.classifier.AbstractClassifier;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public class RandomForest extends AbstractClassifier {

	/** The unique class ID */
	private static final long serialVersionUID = -225055924829811675L;

	/**
	 * @see stream.learner.PredictionService#predict(stream.data.Data)
	 */
	@Override
	public Serializable predict(Data item) throws RemoteException {
		return null;
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
	}

	/**
	 * @see stream.classifier.AbstractClassifier#train(stream.data.Data)
	 */
	@Override
	public void train(Data item) {

	}
}